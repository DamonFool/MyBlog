# Class Method

## Related Data Structures

```cpp
class Method : public Metadata {
 private:
  // Entry point for calling both from and to the interpreter.
  address _i2i_entry;           // All-args-on-stack calling convention
  // Adapter blob (i2c/c2i) for this Method*. Set once when method is linked.
  AdapterHandlerEntry* _adapter;
  // Entry point for calling from compiled code, to compiled code if it exists
  // or else the interpreter.
  volatile address _from_compiled_entry;        // Cache of: _code ? _code->entry_point() : _adapter->c2i_entry()
  // The entry point for calling both from and to compiled code is
  // "_code->entry_point()".  Because of tiered compilation and de-opt, this
  // field can come and go.  It can transition from NULL to not-null at any
  // time (whenever a compile completes).  It can transition from not-null to
  // NULL only at safepoints (because of a de-opt).
  nmethod* volatile _code;                       // Points to the corresponding piece of native code
  volatile address           _from_interpreted_entry; // Cache of _code ? _adapter->i2c_entry() : _i2i_entry
}
```

## Related Operations

### Method::set_code(methodHandle mh, nmethod *code)
- Usage
```
void ciEnv::register_method(...)
  |
  `method->set_code(method, nm);

void AdapterHandlerLibrary::create_native_wrapper(methodHandle method)
  |
  `method->set_code(method, nm);
```

- Implementation
```cpp
// Install compiled code.  Instantly it can execute.
void Method::set_code(methodHandle mh, nmethod *code) {
  MutexLockerEx pl(Patching_lock, Mutex::_no_safepoint_check_flag);
  assert( code, "use clear_code to remove code" );
  assert( mh->check_code(), "" );

  guarantee(mh->adapter() != NULL, "Adapter blob must already exist!");

  // These writes must happen in this order, because the interpreter will
  // directly jump to from_interpreted_entry which jumps to an i2c adapter
  // which jumps to _from_compiled_entry.
  mh->_code = code;             // Assign before allowing compiled code to exec

  int comp_level = code->comp_level();
  // In theory there could be a race here. In practice it is unlikely
  // and not worth worrying about.
  if (comp_level > mh->highest_comp_level()) {
    mh->set_highest_comp_level(comp_level);
  }

  OrderAccess::storestore();
#ifdef SHARK
  mh->_from_interpreted_entry = code->insts_begin();
#else //!SHARK
  mh->_from_compiled_entry = code->verified_entry_point();
  OrderAccess::storestore();
  // Instantly compiled code can execute.
  if (!mh->is_method_handle_intrinsic())
    mh->_from_interpreted_entry = mh->get_i2c_entry();
#endif //!SHARK
}
```
### Method::clear_code(bool acquire_lock /* = true */)
- Usage
```cpp
bool nmethod::make_not_entrant_or_zombie(unsigned int state) {
  // ...
}

void nmethod::make_unloaded(BoolObjectClosure* is_alive, oop cause) {
  // ...
}

void Method::clear_native_function() {
  // Note: is_method_handle_intrinsic() is allowed here.
  set_native_function(
    SharedRuntime::native_method_throw_unsatisfied_link_error_entry(),
    !native_bind_event_is_interesting);
  clear_code();
}

~RegisteredProbes() {
  for (size_t i = 0; i < _count; ++i) {
    // Let the sweeper reclaim it
    _nmethods[i]->make_not_entrant();
    _nmethods[i]->method()->clear_code();
  }
  FREE_C_HEAP_ARRAY(nmethod*, _nmethods, mtInternal);
  _nmethods = NULL;
  _count = 0;
}
```


- Implementation
```cpp
// Revert to using the interpreter and clear out the nmethod
void Method::clear_code(bool acquire_lock /* = true */) {
  MutexLockerEx pl(acquire_lock ? Patching_lock : NULL, Mutex::_no_safepoint_check_flag);
  // this may be NULL if c2i adapters have not been made yet
  // Only should happen at allocate time.
  if (_adapter == NULL) {
    _from_compiled_entry    = NULL;
  } else {
    _from_compiled_entry    = _adapter->get_c2i_entry();
  }
  OrderAccess::storestore();
  _from_interpreted_entry = _i2i_entry;
  OrderAccess::storestore();
  _code = NULL;
}
```
# From Interpreter to Compiled Code

## Through Method Invocation

### List of Method Invocation Templates

```cpp
void TemplateTable::invokevirtual(int byte_no)
void TemplateTable::invokespecial(int byte_no)
void TemplateTable::invokestatic(int byte_no)
void TemplateTable::invokeinterface(int byte_no)
void TemplateTable::invokedynamic(int byte_no)
void TemplateTable::invokehandle(int byte_no)
```

```cpp
void TemplateTable::initialize() {
  // ...
  def(Bytecodes::_invokevirtual       , ubcp|disp|clvm|____, vtos, vtos, invokevirtual       , f2_byte      );
  def(Bytecodes::_invokespecial       , ubcp|disp|clvm|____, vtos, vtos, invokespecial       , f1_byte      );
  def(Bytecodes::_invokestatic        , ubcp|disp|clvm|____, vtos, vtos, invokestatic        , f1_byte      );
  def(Bytecodes::_invokeinterface     , ubcp|disp|clvm|____, vtos, vtos, invokeinterface     , f1_byte      );
  def(Bytecodes::_invokedynamic       , ubcp|disp|clvm|____, vtos, vtos, invokedynamic       , f1_byte      );
  // ...
  def(Bytecodes::_invokehandle        , ubcp|disp|clvm|____, vtos, vtos, invokehandle        , f1_byte      );
  // ...
}
```

### Implementation of Method Invocation Templates

```cpp
void TemplateTable::invokespecial(int byte_no) {
  transition(vtos, vtos);
  assert(byte_no == f1_byte, "use this argument");
  prepare_invoke(byte_no, rbx, noreg,  // get f1 Method*
                 rcx);  // get receiver also for null check
  __ verify_oop(rcx);
  __ null_check(rcx);
  // do the call
  __ profile_call(rax);
  __ profile_arguments_type(rax, rbx, r13, false);
  __ jump_from_interpreted(rbx, rax);
}


void TemplateTable::invokestatic(int byte_no) {
  transition(vtos, vtos);
  assert(byte_no == f1_byte, "use this argument");
  prepare_invoke(byte_no, rbx);  // get f1 Method*
  // do the call
  __ profile_call(rax);
  __ profile_arguments_type(rax, rbx, r13, false);
  __ jump_from_interpreted(rbx, rax);
}

void TemplateTable::invokehandle(int byte_no) {
  transition(vtos, vtos);
  assert(byte_no == f1_byte, "use this argument");
  const Register rbx_method = rbx;
  const Register rax_mtype  = rax;
  const Register rcx_recv   = rcx;
  const Register rdx_flags  = rdx;

  if (!EnableInvokeDynamic) {
    // rewriter does not generate this bytecode
    __ should_not_reach_here();
    return;
  }

  prepare_invoke(byte_no, rbx_method, rax_mtype, rcx_recv);
  __ verify_method_ptr(rbx_method);
  __ verify_oop(rcx_recv);
  __ null_check(rcx_recv);

  // rax: MethodType object (from cpool->resolved_references[f1], if necessary)
  // rbx: MH.invokeExact_MT method (from f2)

  // Note:  rax_mtype is already pushed (if necessary) by prepare_invoke

  // FIXME: profile the LambdaForm also
  __ profile_final_call(rax);
  __ profile_arguments_type(rdx, rbx_method, r13, true);

  __ jump_from_interpreted(rbx_method, rdx);
}

void TemplateTable::invokedynamic(int byte_no) {
  transition(vtos, vtos);
  assert(byte_no == f1_byte, "use this argument");

  if (!EnableInvokeDynamic) {
    // We should not encounter this bytecode if !EnableInvokeDynamic.
    // The verifier will stop it.  However, if we get past the verifier,
    // this will stop the thread in a reasonable way, without crashing the JVM.
    __ call_VM(noreg, CAST_FROM_FN_PTR(address,
                     InterpreterRuntime::throw_IncompatibleClassChangeError));
    // the call_VM checks for exception, so we should never return here.
    __ should_not_reach_here();
    return;
  }

  const Register rbx_method   = rbx;
  const Register rax_callsite = rax;

  prepare_invoke(byte_no, rbx_method, rax_callsite);

  // rax: CallSite object (from cpool->resolved_references[f1])
  // rbx: MH.linkToCallSite method (from f2)

  // Note:  rax_callsite is already pushed by prepare_invoke

  // %%% should make a type profile for any invokedynamic that takes a ref argument
  // profile this call
  __ profile_call(r13);
  __ profile_arguments_type(rdx, rbx_method, r13, false);

  __ verify_oop(rax_callsite);

  __ jump_from_interpreted(rbx_method, rdx);
}

void TemplateTable::invokevirtual(int byte_no) {
  transition(vtos, vtos);
  assert(byte_no == f2_byte, "use this argument");
  prepare_invoke(byte_no,
                 rbx,    // method or vtable index
                 noreg,  // unused itable index
                 rcx, rdx); // recv, flags

  // rbx: index
  // rcx: receiver
  // rdx: flags

  invokevirtual_helper(rbx, rcx, rdx); // End up with __ jump_from_interpreted(xx, xx);
}

void TemplateTable::invokeinterface(int byte_no) {
  // ...
  // End up with __ jump_from_interpreted(xx, xx);
}
```

#### InterpreterMacroAssembler::jump_from_interpreted(Register method, Register temp)

```cpp
// Jump to from_interpreted entry of a call unless single stepping is possible
// in this thread in which case we must call the i2i entry
void InterpreterMacroAssembler::jump_from_interpreted(Register method, Register temp) {
  prepare_to_jump_from_interpreted();

  if (JvmtiExport::can_post_interpreter_events()) {
    Label run_compiled_code;
    // JVMTI events, such as single-stepping, are implemented partly by avoiding running
    // compiled code in threads for which the event is enabled.  Check here for
    // interp_only_mode if these events CAN be enabled.
    // interp_only is an int, on little endian it is sufficient to test the byte only
    // Is a cmpl faster?
    cmpb(Address(r15_thread, JavaThread::interp_only_mode_offset()), 0);
    jccb(Assembler::zero, run_compiled_code);
    jmp(Address(method, Method::interpreter_entry_offset()));
    bind(run_compiled_code);
  }

  jmp(Address(method, Method::from_interpreted_offset()));

}
```

## Through OSR

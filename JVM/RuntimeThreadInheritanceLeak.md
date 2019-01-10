# Symptom

test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java failed with "java -Xcomp RuntimeThreadInheritanceLeak".

- The failure can be reproduced always.
```

Regression test for bug 4404702

created loader: java.net.FactoryURLClassLoader@b1bc7ed
exported remote object with loader as context class loader
nulled strong reference to loader
unexported remote object
waiting to be notified of loader being weakly reachable...
TEST FAILED: loader not deteced weakly reachable
current live threads and their context class loaders:
  thread: Thread[RMI TCP Accept-0,5,system]
  context class loader: jdk.internal.loader.ClassLoaders$AppClassLoader@55054057
    java.base@13-internal/java.net.PlainSocketImpl.socketAccept(Native Method)
    java.base@13-internal/java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:458)
    java.base@13-internal/java.net.ServerSocket.implAccept(ServerSocket.java:556)
    java.base@13-internal/java.net.ServerSocket.accept(ServerSocket.java:524)
    java.rmi@13-internal/sun.rmi.transport.tcp.TCPTransport$AcceptLoop.executeAcceptLoop(TCPTransport.java:394)
    java.rmi@13-internal/sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:366)
    java.base@13-internal/java.lang.Thread.run(Thread.java:835)
  thread: Thread[Common-Cleaner,8,InnocuousThreadGroup]
  context class loader: null
    java.base@13-internal/java.lang.Object.wait(Native Method)
    java.base@13-internal/java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:155)
    java.base@13-internal/jdk.internal.ref.CleanerImpl.run(CleanerImpl.java:148)
    java.base@13-internal/java.lang.Thread.run(Thread.java:835)
    java.base@13-internal/jdk.internal.misc.InnocuousThread.run(InnocuousThread.java:134)
  thread: Thread[main,5,main]
  context class loader: jdk.internal.loader.ClassLoaders$AppClassLoader@55054057
    java.base@13-internal/java.lang.Thread.dumpThreads(Native Method)
    java.base@13-internal/java.lang.Thread.getAllStackTraces(Thread.java:1653)
    app//RuntimeThreadInheritanceLeak.dumpThreads(RuntimeThreadInheritanceLeak.java:145)
    app//RuntimeThreadInheritanceLeak.main(RuntimeThreadInheritanceLeak.java:117)
  thread: Thread[Finalizer,8,system]
  context class loader: null
    java.base@13-internal/java.lang.Object.wait(Native Method)
    java.base@13-internal/java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:155)
    java.base@13-internal/java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:176)
    java.base@13-internal/java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:170)
  thread: Thread[Reference Handler,10,system]
  context class loader: null
    java.base@13-internal/java.lang.ref.Reference.waitForReferencePendingList(Native Method)
    java.base@13-internal/java.lang.ref.Reference.processPendingReferences(Reference.java:241)
    java.base@13-internal/java.lang.ref.Reference$ReferenceHandler.run(Reference.java:213)
  thread: Thread[Signal Dispatcher,9,system]
  context class loader: null
Exception in thread "main" java.lang.RuntimeException: TEST FAILED: loader not detected weakly reachable
	at RuntimeThreadInheritanceLeak.main(RuntimeThreadInheritanceLeak.java:118)
```

# Analysis

## More Experiments

- Failed by
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:CompileOnly=RuntimeThreadInheritanceLeak::main \
   RuntimeThreadInheritanceLeak
```

- Passed by
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:CompileCommand=exclude,RuntimeThreadInheritanceLeak::main \
   RuntimeThreadInheritanceLeak
```

- Failed by
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:TieredStopAtLevel=1 \
   -XX:CompileOnly=RuntimeThreadInheritanceLeak::main \
   RuntimeThreadInheritanceLeak
```

So it seems that test failed in JITed code of RuntimeThreadInheritanceLeak::main.


## JITed Code Analysis
The following is the JITed code of RuntimeThreadInheritanceLeak::main by
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:TieredStopAtLevel=1 \
   -XX:CompileOnly=RuntimeThreadInheritanceLeak::main \
   -XX:+PrintLIR \
   -XX:+PrintAssembly \
   -XX:PrintAssemblyOptions=intel \
   RuntimeThreadInheritanceLeak
```

An OopMap item for loaderRef was constructed at 0x00007f0c9c9a78ef (RuntimeThreadInheritanceLeak.java line 87).
It was optimized out at 0x00007f0c9c9a79b7 (RuntimeThreadInheritanceLeak line 88).
So loaderRef was also reclaimed after System.gc() at RuntimeThreadInheritanceLeak line 109, which caused the failure of the test.
```
The aboving code was useless for analysis and was omitted here

    0x00007f0c9c9a78e3: mov    QWORD PTR [rsp+0x58],rax
    0x00007f0c9c9a78e8: nop
    0x00007f0c9c9a78e9: nop
    0x00007f0c9c9a78ea: nop
    0x00007f0c9c9a78eb: nop
    0x00007f0c9c9a78ec: nop
    0x00007f0c9c9a78ed: nop
    0x00007f0c9c9a78ee: nop
    0x00007f0c9c9a78ef: call   0x00007f0c9c2c2120             ; ImmutableOopMap{[64]=Oop [80]=Oop [72]=Oop [88]=Oop }  <--- oopmap([88]=Oop) for loaderRef was constructed here
                                                              ;*invokespecial <init> {reexecute=0 rethrow=0 return_oop=0}
                                                              ; - RuntimeThreadInheritanceLeak::main@53 (line 87)
                                                              ;   {optimized virtual_call}
   ;;   92 move [obj:0x0000000000000000|L] [rsi|L] [patch_normal] [bci:58]
    0x00007f0c9c9a78f4: nop
    0x00007f0c9c9a78f5: nop
    0x00007f0c9c9a78f6: nop
    0x00007f0c9c9a78f7: nop
    0x00007f0c9c9a78f8: jmp    0x00007f0c9c9a834f             ;   {no_reloc}
    0x00007f0c9c9a78fd: add    BYTE PTR [rax],al
    0x00007f0c9c9a78ff: add    BYTE PTR [rax],al
    0x00007f0c9c9a7901: add    BYTE PTR [rax-0x6f6f6f70],dl
    0x00007f0c9c9a7907: nop
    0x00007f0c9c9a7908: jmp    0x00007f0c9c9a83e0             ; implicit exception: dispatches to 0x00007f0c9c9a8359
    0x00007f0c9c9a790d: nop
    0x00007f0c9c9a790e: push   r10
    0x00007f0c9c9a7910: cmp    r12,QWORD PTR [rip+0x1e88e0b1]        # 0x00007f0cbb2359c8
                                                              ;   {external_word}
    0x00007f0c9c9a7917: je     0x00007f0c9c9a7994
   ;; MacroAssembler::decode_heap_oop: heap base corrupted?
    0x00007f0c9c9a791d: mov    QWORD PTR [rsp-0x28],rsp
    0x00007f0c9c9a7922: sub    rsp,0x80
    0x00007f0c9c9a7929: mov    QWORD PTR [rsp+0x78],rax
    0x00007f0c9c9a792e: mov    QWORD PTR [rsp+0x70],rcx
    0x00007f0c9c9a7933: mov    QWORD PTR [rsp+0x68],rdx
    0x00007f0c9c9a7938: mov    QWORD PTR [rsp+0x60],rbx
    0x00007f0c9c9a793d: mov    QWORD PTR [rsp+0x50],rbp
    0x00007f0c9c9a7942: mov    QWORD PTR [rsp+0x48],rsi
    0x00007f0c9c9a7947: mov    QWORD PTR [rsp+0x40],rdi
    0x00007f0c9c9a794c: mov    QWORD PTR [rsp+0x38],r8
    0x00007f0c9c9a7951: mov    QWORD PTR [rsp+0x30],r9
    0x00007f0c9c9a7956: mov    QWORD PTR [rsp+0x28],r10
    0x00007f0c9c9a795b: mov    QWORD PTR [rsp+0x20],r11
    0x00007f0c9c9a7960: mov    QWORD PTR [rsp+0x18],r12
    0x00007f0c9c9a7965: mov    QWORD PTR [rsp+0x10],r13
    0x00007f0c9c9a796a: mov    QWORD PTR [rsp+0x8],r14
    0x00007f0c9c9a796f: mov    QWORD PTR [rsp],r15
    0x00007f0c9c9a7973: movabs rdi,0x7f0cba7b0860             ;   {external_word}
    0x00007f0c9c9a797d: movabs rsi,0x7f0c9c9a791d             ;   {internal_word}
    0x00007f0c9c9a7987: mov    rdx,rsp
    0x00007f0c9c9a798a: and    rsp,0xfffffffffffffff0
    0x00007f0c9c9a798e: call   0x00007f0cb9e6315e             ;   {runtime_call MacroAssembler::debug64(char*, long, long*)}
    0x00007f0c9c9a7993: hlt
    0x00007f0c9c9a7994: pop    r10
    0x00007f0c9c9a7996: shl    rdi,0x3                        ;*getstatic err {reexecute=0 rethrow=0 return_oop=0}
                                                              ; - RuntimeThreadInheritanceLeak::main@58 (line 88)
  
   ;;   96 move [obj:0x0000000000000000|L] [rdx|L] [patch_normal] [bci:62]
    0x00007f0c9c9a799a: nop
    0x00007f0c9c9a799b: nop
    0x00007f0c9c9a799c: nop
    0x00007f0c9c9a799d: nop
    0x00007f0c9c9a799e: nop
    0x00007f0c9c9a799f: nop
    0x00007f0c9c9a79a0: jmp    0x00007f0c9c9a83f9             ;   {no_reloc}
    0x00007f0c9c9a79a5: nop
    0x00007f0c9c9a79a6: nop
    0x00007f0c9c9a79a7: nop
    0x00007f0c9c9a79a8: nop
    0x00007f0c9c9a79a9: nop
    0x00007f0c9c9a79aa: mov    rsi,QWORD PTR [rsp+0x48]       ;*invokedynamic {reexecute=0 rethrow=0 return_oop=0}
                                                              ; - RuntimeThreadInheritanceLeak::main@62 (line 88)
  
    0x00007f0c9c9a79af: mov    QWORD PTR [rsp+0x60],rdi
    0x00007f0c9c9a79b4: nop
    0x00007f0c9c9a79b5: nop
    0x00007f0c9c9a79b6: nop
    0x00007f0c9c9a79b7: call   0x00007f0c9c2c2620             ; ImmutableOopMap{[64]=Oop [80]=Oop [72]=Oop [96]=Oop }   <--- oopmap([88]=Oop) for loaderRef lost here
                                                              ;*invokedynamic {reexecute=0 rethrow=0 return_oop=0}
                                                              ; - RuntimeThreadInheritanceLeak::main@62 (line 88)
                                                              ;   {static_call}
    0x00007f0c9c9a79bc: mov    rdx,rax
    0x00007f0c9c9a79bf: mov    rsi,QWORD PTR [rsp+0x60]       ;*invokevirtual println {reexecute=0 rethrow=0 return_oop=0}

The following code was useless for analysis and was omitted here
```

# Conclusion

- test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java failed in JITed code of RuntimeThreadInheritanceLeak::main.
It is quite confusing that behaviors of the interpreter and JITs are different with the same test case.

- It seems that RuntimeThreadInheritanceLeak.java is only suitable for testing interpreters which is not adapted to JITs at all. An OopMap item was always kept for the "loaderRef" while interpretation. But it was optimized out after "loaderRef" was constructed immediately in JITed code due to the liveness analysis optimization. Lacking of OopMap item for "loaderRef" leads to its reclaimation after "System.gc()" was called, which caused the failure of the test.

- It can be fixed by a tiny change such as
```
diff -r 02e648ae46c3 test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java
--- a/test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java        Wed Jan 09 01:06:19 2019 +0100
+++ b/test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java        Thu Jan 10 20:46:50 2019 +0800
@@ -107,6 +107,7 @@
              */
             Thread.sleep(2000);
             System.gc();
+            Reference.reachabilityFence(loaderRef);
 
             System.err.println(
                 "waiting to be notified of loader being weakly reachable...");
```

- However, a better patch is
```
diff -r 02e648ae46c3 test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java
--- a/test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java        Wed Jan 09 01:06:19 2019 +0100
+++ b/test/jdk/java/rmi/transport/runtimeThreadInheritanceLeak/RuntimeThreadInheritanceLeak.java        Thu Jan 10 20:49:22 2019 +0800
@@ -106,7 +106,10 @@
              * context class loader-- by giving it a chance to pass away.
              */
             Thread.sleep(2000);
-            System.gc();
+            while (loaderRef.get() != null) {
+                System.gc();
+                Thread.sleep(100);
+            }
 
             System.err.println(
                 "waiting to be notified of loader being weakly reachable...");
```

# Appendix: A Simplified Test Case
- The following test case was constructed for debugging.
```java
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.Map;

public class RuntimeThreadInheritanceLeak implements Remote {

    private static final int TIMEOUT = 20000;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 2000; i++) {
            System.out.println("i = " + i);
            test();
        }
    }

    public static void test() throws Exception {

        RuntimeThreadInheritanceLeak obj = new RuntimeThreadInheritanceLeak();

        ClassLoader loader = URLClassLoader.newInstance(new URL[0]);
        ReferenceQueue refQueue = new ReferenceQueue();
        Reference loaderRef = new WeakReference(loader, refQueue);

        Thread.currentThread().setContextClassLoader(loader);
        UnicastRemoteObject.exportObject(obj);
        Thread.currentThread().setContextClassLoader(
            ClassLoader.getSystemClassLoader());

        loader = null;

        UnicastRemoteObject.unexportObject(obj, true);

        System.gc();
        //Reference.reachabilityFence(loaderRef);

        Reference dequeued = refQueue.remove(TIMEOUT);
        if (dequeued == null) {
            System.out.println("TEST FAILED: loader not detected weakly reachable");
        }
    }
}
```

- The failure can be reproduced always by
```shell
${JDK}/bin/java \
   -XX:-TieredCompilation \
   -XX:CompileThreshold=10 \
   -XX:+PrintCompilation \
   -XX:CompileCommand=quiet \
   -XX:CompileOnly=RuntimeThreadInheritanceLeak::test \
   RuntimeThreadInheritanceLeak
```

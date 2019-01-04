# Editor Lost in JIT 

[JDK-8216046](https://bugs.openjdk.java.net/browse/JDK-8216046)

Thanks Alan Bateman and Sergey Bylokhov.

## Symptom 

Test case test/jdk/java/beans/PropertyEditor/Test6397609.java failed when running with -Xcomp.

- Running
```shell
${JDK}/bin/java \
   -Xcomp \
   Test6397609
```
- Output
```
Exception in thread "main" java.lang.Error: the editor is lost
	at Test6397609.main(Test6397609.java:45)
```
- JDK Version
[changeset 53109:b99b41325d89](http://hg.openjdk.org/jdk/jdk/rev/b99b41325d89)

## More Experiments

### Failed When compileonly Test6397609::main

- Test failed with
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:+PrintCompilation \
   -XX:CompileCommand=quiet \
   -XX:CompileCommand=compileonly,Test6397609::main\
   Test6397609
```
- Output
```
     46    1    b  3       Test6397609::main (60 bytes)
     47    2    b  4       Test6397609::main (60 bytes)
     47    1       3       Test6397609::main (60 bytes)   made not entrant
     52    3     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLL)L (native)   (static)
     52    4     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLL)V (native)   (static)
     53    5     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLL)L (native)   
     53    6     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLLLLL)L (native)   (static)
     54    7     n 0       java.lang.invoke.MethodHandle::linkToStatic(LL)L (native)   (static)
     54    8     n 0       java.lang.invoke.MethodHandle::invokeBasic(L)L (native)   
     54    9     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLL)L (native)   (static)
     59   10     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLL)L (native)   (static)
     60   11     n 0       java.lang.invoke.MethodHandle::invokeBasic(LL)L (native)   
     60   12     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLL)L (native)   (static)
     67   13     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLL)L (native)   (static)
     69   14     n 0       java.lang.invoke.MethodHandle::invokeBasic()L (native)   
     69   15     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LL)L (native)   (static)
     82   16     n 0       java.lang.invoke.MethodHandle::linkToStatic(IL)I (native)   (static)
     88   17     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLL)I (native)   (static)
     89   18     n 0       java.lang.invoke.MethodHandle::linkToStatic(L)L (native)   (static)
     89   19     n 0       java.lang.invoke.MethodHandle::linkToInterface(LLL)I (native)   (static)
     98   20     n 0       java.lang.invoke.MethodHandle::linkToStatic(LL)I (native)   (static)
    110   21     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLL)L (native)   
    110   22     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLL)L (native)   (static)
    130   23     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLL)L (native)   (static)
    130   24     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLL)L (native)   
    130   25     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLLLL)L (native)   (static)
    131   26     n 0       java.lang.invoke.MethodHandle::linkToStatic(L)J (native)   (static)
    131   27     n 0       java.lang.invoke.MethodHandle::invokeBasic()J (native)   
    131   28     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LL)J (native)   (static)
    131   29     n 0       java.lang.invoke.MethodHandle::linkToStatic(LJL)L (native)   (static)
    131   30     n 0       java.lang.invoke.MethodHandle::linkToStatic(JL)L (native)   (static)
    131   31     n 0       java.lang.invoke.MethodHandle::invokeBasic(LJ)L (native)   
    131   32     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLJL)L (native)   (static)
    132   33     n 0       java.lang.invoke.MethodHandle::linkToStatic(JLLL)J (native)   (static)
    132   34     n 0       java.lang.invoke.MethodHandle::invokeBasic(JLL)J (native)   
    132   35     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LJLLL)J (native)   (static)
    133   36     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLL)L (native)   (static)
    133   37     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLL)L (native)   
    133   38     n 0       java.lang.invoke.MethodHandle::invokeBasic(JL)J (native)   
    133   39     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LJLL)J (native)   (static)
    136   40     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLL)L (native)   (static)
    136   41     n 0       java.lang.invoke.MethodHandle::invokeBasic(J)L (native)   
    136   42     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LJL)L (native)   (static)
    136   43     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLL)L (native)   
    137   44     n 0       java.lang.invoke.MethodHandle::linkToStatic(JLL)J (native)   (static)
    137   45     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLL)L (native)   (static)
    138   46     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLL)L (native)   
    139   47     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLLL)L (native)   (static)
    139   48     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLLL)L (native)   
    140   49     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLLJL)L (native)   (static)
    140   50     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLLLJ)L (native)   
    141   51     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLLJLL)L (native)   (static)
    142   52     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLLLJL)L (native)   
    153   53     n 0       java.lang.invoke.MethodHandle::linkToStatic(LL)V (native)   (static)
    156   54     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLIL)I (native)   (static)
    156   55     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLL)V (native)   (static)
    159   56     n 0       java.lang.invoke.MethodHandle::linkToVirtual(LLL)I (native)   (static)
    160   57     n 0       java.lang.invoke.MethodHandle::linkToInterface(LLL)V (native)   (static)
    303   58     n 0       java.lang.invoke.MethodHandle::linkToVirtual(LL)L (native)   (static)
    310   59     n 0       java.lang.invoke.MethodHandle::linkToVirtual(LL)I (native)   (static)
    316   60     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLLL)L (native)   (static)
    381   61     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LIL)I (native)   (static)
    385   62     n 0       java.lang.invoke.MethodHandle::linkToStatic(IIL)I (native)   (static)
    386   63     n 0       java.lang.invoke.MethodHandle::linkToStatic(IL)L (native)   (static)
    386   64     n 0       java.lang.invoke.MethodHandle::invokeBasic(I)L (native)   
    386   65     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LIL)L (native)   (static)
    398   66     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLJL)L (native)   (static)
    399   67     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLLJ)L (native)   
    400   68     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLLJLL)L (native)   (static)
    401   69     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLLJL)L (native)   
    406   70     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLJL)L (native)   (static)
    407   71     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLJ)L (native)   
    407   72     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLLLLLLJLL)L (native)   (static)
    408   73     n 0       java.lang.invoke.MethodHandle::invokeBasic(LLLLLLLJL)L (native)   
    411   74     n 0       java.lang.invoke.MethodHandle::linkToStatic(LIL)I (native)   (static)
    412   75     n 0       java.lang.invoke.MethodHandle::linkToStatic(IIIL)I (native)   (static)
    413   76     n 0       java.lang.invoke.MethodHandle::linkToStatic(IIL)L (native)   (static)
    413   77     n 0       java.lang.invoke.MethodHandle::invokeBasic(II)L (native)   
    413   78     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LIIL)L (native)   (static)
    442   79     n 0       java.lang.invoke.MethodHandle::linkToVirtual(LLL)V (native)   (static)
    443   80     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LL)I (native)   (static)
    472   81     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LL)V (native)   (static)
    490   82     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLL)I (native)   (static)
    526   83     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLLL)V (native)   (static)
    540   84     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLLLL)I (native)   (static)
    546   85     n 0       java.lang.invoke.MethodHandle::linkToStatic(LLIIL)I (native)   (static)
    779   86     n 0       java.lang.invoke.MethodHandle::linkToInterface(LL)L (native)   (static)
    779   87     n 0       java.lang.invoke.MethodHandle::linkToSpecial(LLL)I (native)   (static)
    870   88     n 0       java.lang.invoke.MethodHandle::linkToStatic(L)V (native)   (static)
Exception in thread "main" java.lang.Error: the editor is lost
	at Test6397609.main(Test6397609.java:45)
```

### Passed If Excludes the Compilation of Test6397609::main

- Test passed with
```shell
${JDK}/bin/java \
   -Xcomp \
   -XX:-PrintCompilation \
   -XX:CompileCommand=quiet \
   -XX:CompileCommand=exclude,Test6397609::main\
   Test6397609
```

## Analysis 

### Java Source Code for Test6397609 Class

```java
import java.beans.PropertyEditorManager;

public class Test6397609 {
    public static void main(String[] args) throws Exception {
        MemoryClassLoader loader = new MemoryClassLoader();
        PropertyEditorManager.registerEditor(
                Object.class,
                loader.compile("Editor",
                               "public class Editor extends java.beans.PropertyEditorSupport {}"));

        if (!isEditorExist(Object.class)) {
            throw new Error("the editor is lost");
        }
        loader = null; // clean the reference
        if (isEditorExist(Object.class)) {
            throw new Error("unexpected editor is found");
        }
    }

    private static boolean isEditorExist(Class type) {
        for (int i = 0; i < 10; i++) {
            System.gc(); // clean all weak references
            if (null == PropertyEditorManager.findEditor(type)) {
                return false;
            }
        }
        return true;
    }
}
```

### JITed Code for Test6397609::main

Here is the JITed code for Test6397609::main by C1.
In block B0, the OopMap for the object loader was optimized out after the invocation of loader.compile(...) immediately due to JIT's liveness analysis optimization. Since there was no OopMap for loader when System.gc() was invoked, both loader and its corresponding editor were reclaimed after GC. And that's the reason for the lost of editor with JIT.

```
Compiled method (c1)      74    1       3       Test6397609::main (60 bytes)
 total in heap  [0x00007f2c1ce42010,0x00007f2c1ce42e48] = 3640
 relocation     [0x00007f2c1ce421a0,0x00007f2c1ce42290] = 240
 main code      [0x00007f2c1ce422a0,0x00007f2c1ce428c0] = 1568
 stub code      [0x00007f2c1ce428c0,0x00007f2c1ce42a10] = 336
 oops           [0x00007f2c1ce42a10,0x00007f2c1ce42a20] = 16
 metadata       [0x00007f2c1ce42a20,0x00007f2c1ce42a30] = 16
 scopes data    [0x00007f2c1ce42a30,0x00007f2c1ce42b60] = 304
 scopes pcs     [0x00007f2c1ce42b60,0x00007f2c1ce42e40] = 736
 dependencies   [0x00007f2c1ce42e40,0x00007f2c1ce42e48] = 8
Loaded disassembler from /home/fool/fujie/workspace/jdk/build/linux-x86_64-server-fastdebug/images/jdk/lib/server/hsdis-amd64.so
----------------------------------------------------------------------
Test6397609.main([Ljava/lang/String;)V  [0x00007f2c1ce422a0, 0x00007f2c1ce42a10]  1904 bytes
[Disassembling for mach='i386:x86-64']
[Entry Point]
[Verified Entry Point]
[Constants]
  # {method} {0x00007f2c052f5450} 'main' '([Ljava/lang/String;)V' in 'Test6397609'
  # parm0:    rsi:rsi   = '[Ljava/lang/String;'
  #           [sp+0x80]  (sp of caller)
 ;;  block B19 [0, 0]

  0x00007f2c1ce422a0: mov    DWORD PTR [rsp-0x16000],eax
  0x00007f2c1ce422a7: push   rbp
  0x00007f2c1ce422a8: sub    rsp,0x70
  0x00007f2c1ce422ac: movabs rdx,0x7f2c052f5748             ;   {metadata(method data for {method} {0x00007f2c052f5450} 'main' '([Ljava/lang/String;)V' in 'Test6397609')}
  0x00007f2c1ce422b6: mov    eax,DWORD PTR [rdx+0x12c]
  0x00007f2c1ce422bc: add    eax,0x8
  0x00007f2c1ce422bf: mov    DWORD PTR [rdx+0x12c],eax
 ;;   14 branch [AL] [CounterOverflowStub: 0x00007f2bec0199a0]
  0x00007f2c1ce422c5: jmp    0x00007f2c1ce42766
 ;;  block B20 [0, 0]

 ;;  block B0 [0, 2]

 ;;   26 move [metadata:0x0000000000000000|M] [rdx|M] [patch_normal] [bci:0]
  0x00007f2c1ce422ca: nop
  0x00007f2c1ce422cb: nop
  0x00007f2c1ce422cc: nop
  0x00007f2c1ce422cd: nop
  0x00007f2c1ce422ce: nop
  0x00007f2c1ce422cf: nop
  0x00007f2c1ce422d0: jmp    0x00007f2c1ce42796             ;   {no_reloc}
  0x00007f2c1ce422d5: add    BYTE PTR [rax],al
  0x00007f2c1ce422d7: add    BYTE PTR [rax],al
  0x00007f2c1ce422d9: add    cl,ch
  0x00007f2c1ce422db: rol    DWORD PTR [rax+rax*1],0x0      ;*new {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@0 (line 38)

  0x00007f2c1ce422df: mov    rsi,rax
  0x00007f2c1ce422e2: movabs rdi,0x7f2c052f5748             ;   {metadata(method data for {method} {0x00007f2c052f5450} 'main' '([Ljava/lang/String;)V' in 'Test6397609')}
  0x00007f2c1ce422ec: add    QWORD PTR [rdi+0x168],0x1
  0x00007f2c1ce422f4: mov    rsi,rax                        ;*invokespecial <init> {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@4 (line 38)

  0x00007f2c1ce422f7: mov    QWORD PTR [rsp+0x40],rax
  0x00007f2c1ce422fc: nop
  0x00007f2c1ce422fd: nop
  0x00007f2c1ce422fe: nop
  0x00007f2c1ce422ff: call   0x00007f2c1c900120             ; ImmutableOopMap{[64]=Oop }
                                                            ;*invokespecial <init> {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@4 (line 38)
                                                            ;   {optimized virtual_call}
  0x00007f2c1ce42304: movabs rdx,0x717f7ae20                ;   {oop("Editor"{0x0000000717f7ae20})}
  0x00007f2c1ce4230e: movabs rcx,0x717f7ae50                ;   {oop("public class Editor extends java.beans.PropertyEditorSupport {}"{0x0000000717f7ae50})}
  0x00007f2c1ce42318: mov    rsi,QWORD PTR [rsp+0x40]       ;*invokevirtual compile {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@15 (line 41)

  0x00007f2c1ce4231d: movabs rax,0xffffffffffffffff
  0x00007f2c1ce42327: call   0x00007f2c1c9003a0             ; ImmutableOopMap{}
                                                            ;*invokevirtual compile {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@15 (line 41)
                                                            ;   {virtual_call}
  0x00007f2c1ce4232c: movabs rsi,0x7ffe2dea8                ;   {oop(a 'java/lang/Class'{0x00000007ffe2dea8} = 'java/lang/Object')}
  0x00007f2c1ce42336: mov    rdx,rax                        ;*invokestatic registerEditor {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@18 (line 39)

  0x00007f2c1ce42339: nop
  0x00007f2c1ce4233a: nop
  0x00007f2c1ce4233b: nop
  0x00007f2c1ce4233c: nop
  0x00007f2c1ce4233d: nop
  0x00007f2c1ce4233e: nop
  0x00007f2c1ce4233f: call   0x00007f2c1c900620             ; ImmutableOopMap{}
                                                            ;*invokestatic registerEditor {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::main@18 (line 39)
                                                            ;   {static_call}
  0x00007f2c1ce42344: movabs rsi,0x7f2c052f5748             ;   {metadata(method data for {method} {0x00007f2c052f5450} 'main' '([Ljava/lang/String;)V' in 'Test6397609')}
  0x00007f2c1ce4234e: add    QWORD PTR [rsi+0x1c0],0x1
  0x00007f2c1ce42356: movabs rsi,0x7f2c052f59c8             ;   {metadata(method data for {method} {0x00007f2c052f5530} 'isEditorExist' '(Ljava/lang/Class;)Z' in 'Test6397609')}
  0x00007f2c1ce42360: mov    edi,DWORD PTR [rsi+0x12c]
  0x00007f2c1ce42366: add    edi,0x8
  0x00007f2c1ce42369: mov    DWORD PTR [rsi+0x12c],edi
  0x00007f2c1ce4236f: and    edi,0x7ffff8
  0x00007f2c1ce42375: cmp    edi,0x0
 ;;   74 branch [EQ] [CounterOverflowStub: 0x00007f2c343033d0]
  0x00007f2c1ce42378: je     0x00007f2c1ce427ad
  0x00007f2c1ce4237e: mov    esi,0x0
 ;;   80 branch [AL] [B7] 
  0x00007f2c1ce42383: jmp    0x00007f2c1ce42426             ;*iload_1 {reexecute=0 rethrow=0 return_oop=0}
                                                            ; - Test6397609::isEditorExist@2 (line 54)
                                                            ; - Test6397609::main@23 (line 44)

 ;;  block B8 [8, 16]

  ...

  The Following code was useless for analysis and was omitted here
```

## Conclusion

- test/jdk/java/beans/PropertyEditor/Test6397609.java failed in JITed code of Test6397609::main.
It is quite confusing that behaviors of the interpreter and JITs are different with the same test case.

- It seems that Test6397609.java is only suitable for testing interpreters which is not adapted to JITs at all. An OopMap item was always kept for the loader while interpretation. But it was optimized out after the invocation of loader.compile(...) immediately in JITed code due to the liveness analysis optimization. Lacking of OopMap item for the loader leads to the lost of editor in JITed Code.

- However, it can be used for JITs by a tiny change such as
```
diff -r b99b41325d89 test/jdk/java/beans/PropertyEditor/Test6397609.java
--- a/test/jdk/java/beans/PropertyEditor/Test6397609.java       Tue Jan 01 20:09:02 2019 -0500
+++ b/test/jdk/java/beans/PropertyEditor/Test6397609.java       Wed Jan 02 12:01:21 2019 +0800
@@ -44,6 +44,7 @@
         if (!isEditorExist(Object.class)) {
             throw new Error("the editor is lost");
         }
+        loader.hashCode(); // prevent being optimized out by JIT
         loader = null; // clean the reference
         if (isEditorExist(Object.class)) {
             throw new Error("unexpected editor is found");
```

### A Better Patch

- A Better Patch suggested by Alan.Bateman@oracle.com
```
diff -r b561ea19a7b9 test/jdk/java/beans/PropertyEditor/Test6397609.java
--- a/test/jdk/java/beans/PropertyEditor/Test6397609.java       Wed Jan 02 15:33:32 2019 -0800
+++ b/test/jdk/java/beans/PropertyEditor/Test6397609.java       Thu Jan 03 08:56:16 2019 +0800
@@ -32,6 +32,7 @@
  */
 
 import java.beans.PropertyEditorManager;
+import java.lang.ref.Reference;
 
 public class Test6397609 {
     public static void main(String[] args) throws Exception {
@@ -44,6 +45,7 @@
         if (!isEditorExist(Object.class)) {
             throw new Error("the editor is lost");
         }
+        Reference.reachabilityFence(loader);
         loader = null; // clean the reference
         if (isEditorExist(Object.class)) {
             throw new Error("unexpected editor is found");
```

## Appendix

### Make Sure All Weak References are Reclaimed 

- Alan's approach
```java
    private static boolean isEditorExist(Class type) {
        Object object = new Object();
        final WeakReference<Object> ref = new WeakReference<>(object);
        object = null;
        // clean all weak references
        while (ref.get() != null) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                // pass
            }
        }

        if (null == PropertyEditorManager.findEditor(type)) {
            return false;
        } else {
            return true;
        }
    }
```

- Suggested by Sergey.Bylokhov@oracle.com
```java
    private static boolean isEditorExist(Class type) {
        Vector<byte[]> garbage = new Vector<byte[]>();
        // clean all weak references
        while (true) {
            try {
                garbage.add(new byte[180306]);
            }
            catch (OutOfMemoryError e) {
                break;
            }
        }
        garbage = null;

        if (null == PropertyEditorManager.findEditor(type)) {
            return false;
        } else {
            return true;
        }
    }
```

### Make Sure a GC was Performed

Ref: http://www.javacreed.com/how-to-force-garbage-collection/

```java
    private static boolean isEditorExist(Class type) {
        Object object = new Object();
        final WeakReference<Object> ref = new WeakReference<>(object);
        object = null;
        // clean all weak references
        while (ref.get() != null) {
            System.gc();
        }

        if (null == PropertyEditorManager.findEditor(type)) {
            return false;
        } else {
            return true;
        }
    }
```

# OpenJDK Pre-build Failed with Netbeans

JBS: [NetBeans pre-build failed due to the incorrect configuration](https://bugs.openjdk.java.net/browse/JDK-8215952)

## Error: Can't open ../configure

```
cd '/home/fool/fujie/workspace/jdk'
sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info
sh: 0: Can't open ../configure

PRE-BUILD FAILED (exit value 127, total time: 57ms)
```

### Fixed Patch
```
diff -r 35530ca3e0b2 make/nb_native/nbproject/configurations.xml
--- a/make/nb_native/nbproject/configurations.xml       Wed Dec 26 19:24:00 2018 -0500
+++ b/make/nb_native/nbproject/configurations.xml       Thu Dec 27 16:25:14 2018 +0800
@@ -6751,7 +6751,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/variant-server/gensrc/adfiles/ad_x86_64.cpp"
@@ -15512,7 +15512,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/linux_amd64_compiler2/generated/adfiles/ad_x86_64.cpp"
@@ -15681,7 +15681,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/variant-server/gensrc/adfiles/ad_x86_64.cpp"
```

## Error: unrecognized options: --disable-zip-debug-info

```
cd '/home/fool/fujie/workspace/jdk'
sh configure --with-debug-level=slowdebug --disable-zip-debug-info
Runnable configure script is not present
Generating runnable configure script at /home/fool/fujie/workspace/jdk/build/.configure-support/generated-configure.sh
Using autoconf at /usr/bin/autoconf [autoconf (GNU Autoconf) 2.69]
configure: error: unrecognized options: --disable-zip-debug-info
configure exiting with result code 1

PRE-BUILD FAILED (exit value 1, total time: 1s)
```

### Fixed Patch
```
diff -r 35530ca3e0b2 make/nb_native/nbproject/configurations.xml
--- a/make/nb_native/nbproject/configurations.xml       Wed Dec 26 19:24:00 2018 -0500
+++ b/make/nb_native/nbproject/configurations.xml       Thu Dec 27 16:27:41 2018 +0800
@@ -6751,7 +6751,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/variant-server/gensrc/adfiles/ad_x86_64.cpp"
@@ -15512,7 +15512,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/linux_amd64_compiler2/generated/adfiles/ad_x86_64.cpp"
@@ -15681,7 +15681,7 @@
         </makeTool>
         <preBuild>
           <preBuildCommandWorkingDir>../..</preBuildCommandWorkingDir>
-          <preBuildCommand>sh ../configure --with-debug-level=slowdebug --disable-zip-debug-info</preBuildCommand>
+          <preBuildCommand>sh configure --with-debug-level=slowdebug</preBuildCommand>
         </preBuild>
       </makefileType>
       <item path="../../build/hotspot/variant-server/gensrc/adfiles/ad_x86_64.cpp"
```

## Effect

```
====================================================
A new configuration has been successfully created in
/home/fool/fujie/workspace/jdk/build/linux-x86_64-server-slowdebug
using configure arguments '--with-debug-level=slowdebug'.

Configuration summary:
* Debug level:    slowdebug
* HS debug level: debug
* JVM variants:   server
* JVM features:   server: 'aot cds cmsgc compiler1 compiler2 epsilongc g1gc graal jfr jni-check jvmci jvmti management nmt parallelgc serialgc services shenandoahgc vm-structs zgc' 
* OpenJDK target: OS: linux, CPU architecture: x86, address length: 64
* Version string: 13-internal+0-adhoc.fool.jdk (13-internal)

Tools summary:
* Boot JDK:       java version "11.0.1" 2018-10-16 LTS Java(TM) SE Runtime Environment 18.9 (build 11.0.1+13-LTS) Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.1+13-LTS, mixed mode)  (at /opt/jdk-11.0.1)
* Toolchain:      gcc (GNU Compiler Collection)
* C Compiler:     Version 7.3.0 (at /usr/bin/gcc)
* C++ Compiler:   Version 7.3.0 (at /usr/bin/g++)

Build performance summary:
* Cores to use:   12
* Memory limit:   15835 MB


PRE-BUILD SUCCESSFUL (total time: 4s)
```

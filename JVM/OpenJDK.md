# Steps to Build OpenJDK

Only three steps to go: fetch code -> configure -> make.

## 1: Fetch Code
- For OpenJDK8
```shell
hg clone http://hg.openjdk.org/jdk8u/jdk8u/
cd jdk8u
bash get_source.sh # may need a cup of coffee time to download it
```

- For Latest OpenJDK
```shell
hg clone http://hg.openjdk.org/jdk/jdk/ # Now is jdk12
```

## 2: Configure
- For OpenJDK8
```shell
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-debug-level=slowdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-debug-level=fastdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l`
```

- For OpenJDK12
```shell
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=slowdebug --with-native-debug-symbols=external
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=fastdebug --with-native-debug-symbols=external 
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=release

# It is recommended to build a 32-bit JVM on a 64-bit machine. 
bash configure --disable-warnings-as-errors --with-freetype=/cygdrive/c/freetype-i586 --with-target-bits=32

bash configure --disable-warnings-as-errors --enable-debug --with-jvm-variants=server --enable-dtrace

bash configure --disable-warnings-as-errors --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=slowdebug --with-jtreg=/home/fool/fujie/workspace/jtreg/build/images/jtreg
```

- OpenJDK for MIPS
```shell
hg clone http://hg.loongnix.org/jdk8-mips64-public
bash get_source.sh
```

## 3: Make
```shell
make CONF=slow images; make CONF=fast images; make CONF=rel images;

# make images CONF=slowdebug ZIP_DEBUGINFO_FILES=0

# For jdk12: build images twice, second time with newly built JDK
make bootcycle-images
make CONF=slow run-test-tier1 # configured --with-jtreg=<jtreg-path>
```

### cc1plus: all warnings being treated as errors
You may come across the following issue while making.
```
In file included from /home/fool/fujie/workspace/jdk8u/hotspot/src/os/linux/vm/jvm_linux.h:44:0,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/prims/jvm.h:30,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/utilities/debug.hpp:29,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/runtime/globals.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/memory/allocation.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/memory/iterator.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/memory/genOopClosures.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/oops/klass.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/runtime/handles.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/memory/universe.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/code/oopRecorder.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/asm/codeBuffer.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/asm/assembler.hpp:28,
                 from /home/fool/fujie/workspace/jdk8u/hotspot/src/share/vm/precompiled/precompiled.hpp:29:
/usr/include/dirent.h:183:12: note: declared here
 extern int readdir_r (DIR *__restrict __dirp,
            ^~~~~~~~~
cc1plus: all warnings being treated as errors
/home/fool/fujie/workspace/jdk8u/hotspot/make/linux/makefiles/vm.make:309: recipe for target 'precompiled.hpp.gch' failed
make[6]: *** [precompiled.hpp.gch] Error 1
/home/fool/fujie/workspace/jdk8u/hotspot/make/linux/makefiles/top.make:119: recipe for target 'the_vm' failed
make[5]: *** [the_vm] Error 2
/home/fool/fujie/workspace/jdk8u/hotspot/make/linux/Makefile:297: recipe for target 'debug' failed
make[4]: *** [debug] Error 2
Makefile:230: recipe for target 'generic_build2' failed
make[3]: *** [generic_build2] Error 2
Makefile:177: recipe for target 'debug' failed
make[2]: *** [debug] Error 2
HotspotWrapper.gmk:44: recipe for target '/home/fool/fujie/workspace/jdk8u/build/linux-x86_64-normal-server-slowdebug/hotspot/_hotspot.timestamp' failed
make[1]: *** [/home/fool/fujie/workspace/jdk8u/build/linux-x86_64-normal-server-slowdebug/hotspot/_hotspot.timestamp] Error 2
/home/fool/fujie/workspace/jdk8u//make/Main.gmk:109: recipe for target 'hotspot-only' failed
make: *** [hotspot-only] Error 2
```

- Patch to fix it
```cpp
diff -r 9ce27f0a4683 make/linux/makefiles/gcc.make
--- a/make/linux/makefiles/gcc.make     Mon Oct 29 05:48:53 2018 -0700
+++ b/make/linux/makefiles/gcc.make     Tue Dec 11 11:04:45 2018 +0800
@@ -201,7 +201,7 @@
 endif
 
 # Compiler warnings are treated as errors
-WARNINGS_ARE_ERRORS = -Werror
+WARNINGS_ARE_ERRORS = -Wall
 
 ifeq ($(USE_CLANG), true)
   # However we need to clean the code up before we can unrestrictedly enable this option with Clang
```
For jdk12, we can use a configuration option to avoid it.
```
bash configure --disable-warnings-as-errors
```

# Configure and Build OpenJDK Zero VM
- For OpenJDK8
```shel
# yum install libffi-dev
bash ./configure --with-jvm-variants=zero --with-jvm-interpreter=cpp
make CONF=zero DEBUG_BINARIES=true images
```

# Preparation for Debugging

## Build hsdis
### Download the GNU binutils
```shell
cd /home/fool/fujie/workspace/tools
wget https://ftp.gnu.org/gnu/binutils/binutils-2.30.tar.lz
sudo apt install lzip
lzip -d binutils-2.30.tar.lz
tar xvf binutils-2.30.tar
```

### make
```shell
export BINUTILS="/home/fool/fujie/workspace/tools/binutils-2.30"
make all64
```
This will generate hsdis-amd64.so

## Extract Symbol Tables for GDB
```
$ file libjvm.diz
libjvm.diz: Zip archive data, at least v2.0 to extract

$ unzip libjvm.diz
Archive:  libjvm.diz
  inflating: libjvm.debuginfo        

$ unzip libjsig.diz
Archive:  libjsig.diz
    linking: libjsig.debuginfo       -> ../libjsig.debuginfo 
finishing deferred symbolic links:
  libjsig.debuginfo      -> ../libjsig.debuginfo
```


# Other Useful Scripts

## Get Versions of OpenJDK8
```shell
#!/bin/bash

REP=". corba jaxp jaxws langtools nashorn jdk hotspot"

for rep in ${REP}; do

    echo -e "\n\n----------- version for $rep -----------" 
    cd $rep
    hg summary
    cd -

done
```

## Export Patches
```shell
#!/bin/bash

ID="253"
END="275"

while [ ${ID} -lt ${END} ] ;
do
   echo ${ID}
   hg export $ID | tee fu-${ID}.diff
   let ID++
done

tar cvzf  fu-${ID}-${END}.tgz  fu-*.diff
```

## Upgradation for OpenJDK8
```shell
#!/bin/bash

# jdk8u60-b32  jdk8u77-b03
OLD="jdk8u60-b32"
NEW="jdk8u77-b03"
DST="/home/fool/upgrade/77"

SRC="/home/fool/upgrade/jdk8-mips"
REP=". corba jaxp jaxws langtools nashorn jdk hotspot"

for rep in ${REP}; do

    echo "process $rep"
    cd $rep
    hg diff -r $OLD:$NEW > ${DST}/upgrade-${rep}.diff
    cd -

    cd ${SRC}/${rep}
    patch -p1 < ${DST}/upgrade-${rep}.diff
    cd -

done
```

# Reference

[OpenJDK](http://openjdk.java.net/)

[mail.openjdk.java.net Mailing Lists](http://mail.openjdk.java.net/mailman/listinfo)

[OpenJDK Projects](http://hg.openjdk.org/)

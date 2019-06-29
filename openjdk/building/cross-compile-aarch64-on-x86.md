# Cross-compiling AARCH64 on X86

## make in devkit

```shell
cd make/devkit
make TARGETS="aarch64-linux-gnu" BASE_OS=Fedora BASE_OS_VERSION=21
```

### Errors

#### wget: unable to resolve host address ‘ftp.gnu.org’

- Symptom
```
fool@fool-OptiPlex-7060:~/jdk/make/devkit$ make TARGETS="aarch64-linux-gnu" BASE_OS=Fedora BASE_OS_VERSION=21
Building on platform x86_64-linux-gnu
host_platforms x86_64-linux-gnu
target_platforms aarch64-linux-gnu
find: ‘/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-aarch64-linux-gnu’: No such file or directory
Building compilers for x86_64-linux-gnu
Targets: aarch64-linux-gnu
for p in  aarch64-linux-gnu; do \
  make -f Tools.gmk download-rpms HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk all HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk ccache HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=x86_64-linux-gnu PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p BUILDDIR=/home/fool/fujie/workspace/jdk/build/devkit/x86_64-linux-gnu/$p || exit 1 ; \
done
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
mkdir -p /home/fool/fujie/workspace/jdk/build/devkit/download/rpms/aarch64-linux-gnu-Fedora_21
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
wget -P /home/fool/fujie/workspace/jdk/build/devkit/download http://ftp.gnu.org/pub/gnu/binutils/binutils-2.30.tar.xz
URL transformed to HTTPS due to an HSTS policy
--2019-02-27 11:54:13--  https://ftp.gnu.org/pub/gnu/binutils/binutils-2.30.tar.xz
Resolving ftp.gnu.org (ftp.gnu.org)... failed: Name or service not known.
wget: unable to resolve host address ‘ftp.gnu.org’
Tools.gmk:195: recipe for target '/home/fool/fujie/workspace/jdk/build/devkit/download/binutils-2.30.tar.xz' failed
make[1]: *** [/home/fool/fujie/workspace/jdk/build/devkit/download/binutils-2.30.tar.xz] Error 4
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
Makefile:88: recipe for target 'x86_64-linux-gnu' failed
make: *** [x86_64-linux-gnu] Error 1
```

- Fix
```
sudo vim /etc/resolv.conf
add:  nameserver 8.8.8.8
```

#### rpm2cpio: not found
 
- Symptom
```
fool@fool-OptiPlex-7060:~/jdk/make/devkit$ make TARGETS="aarch64-linux-gnu" BASE_OS=Fedora BASE_OS_VERSION=21
Building on platform x86_64-linux-gnu
host_platforms x86_64-linux-gnu
target_platforms aarch64-linux-gnu
Building compilers for x86_64-linux-gnu
Targets: aarch64-linux-gnu
for p in  aarch64-linux-gnu; do \
  make -f Tools.gmk download-rpms HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk all HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk ccache HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=x86_64-linux-gnu PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p BUILDDIR=/home/fool/fujie/workspace/jdk/build/devkit/x86_64-linux-gnu/$p || exit 1 ; \
done
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
mkdir -p /home/fool/fujie/workspace/jdk/build/devkit/download/rpms/aarch64-linux-gnu-Fedora_21
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
Unpacking target rpms and libraries from /home/fool/fujie/workspace/jdk/build/devkit/download/rpms/aarch64-linux-gnu-Fedora_21/alsa-lib-1.0.28-2.fc21.aarch64.rpm
/bin/sh: 3: rpm2cpio: not found
cpio: premature end of archive
/bin/sh: 10: die: not found
Tools.gmk:225: recipe for target '/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-aarch64-linux-gnu/aarch64-linux-gnu/sysroot/alsa-lib-1.0.28-2.fc21.aarch64.rpm.unpacked' failed
make[1]: *** [/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-aarch64-linux-gnu/aarch64-linux-gnu/sysroot/alsa-lib-1.0.28-2.fc21.aarch64.rpm.unpacked] Error 127
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
Makefile:88: recipe for target 'x86_64-linux-gnu' failed
make: *** [x86_64-linux-gnu] Error 1
```

- Fix
```
$ sudo apt install rpm2cpio
```

### Success

```
$ make TARGETS="aarch64-linux-gnu" BASE_OS=Fedora BASE_OS_VERSION=21
Building on platform x86_64-linux-gnu
host_platforms x86_64-linux-gnu
target_platforms aarch64-linux-gnu
Building compilers for x86_64-linux-gnu
Targets: aarch64-linux-gnu
for p in  aarch64-linux-gnu; do \
  make -f Tools.gmk download-rpms HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk all HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=$p PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p && \
  make -f Tools.gmk ccache HOST=x86_64-linux-gnu BUILD=x86_64-linux-gnu RESULT=/home/fool/fujie/workspace/jdk/build/devkit/result OUTPUT_ROOT=/home/fool/fujie/workspace/jdk/build/devkit \
              TARGET=x86_64-linux-gnu PREFIX=/home/fool/fujie/workspace/jdk/build/devkit/result/x86_64-linux-gnu-to-$p BUILDDIR=/home/fool/fujie/workspace/jdk/build/devkit/x86_64-linux-gnu/$p || exit 1 ; \
done
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
mkdir -p /home/fool/fujie/workspace/jdk/build/devkit/download/rpms/aarch64-linux-gnu-Fedora_21
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=aarch64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=aarch64
Libs for lib
Patching libc and pthreads
done
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
make[1]: Entering directory '/home/fool/fujie/workspace/jdk/make/devkit'
TARGET=x86_64-linux-gnu
HOST=x86_64-linux-gnu
BUILD=x86_64-linux-gnu
ARCH=x86_64
Libs for lib64
Libs for lib
make[1]: Nothing to be done for 'ccache'.
make[1]: Leaving directory '/home/fool/fujie/workspace/jdk/make/devkit'
All done"
```

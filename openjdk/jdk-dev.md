# Building

## Fetch Code
```shell
hg clone http://hg.openjdk.java.net/jdk/jdk
```

- For slow or unstable network
```shell
#!/bin/bash

hg clone http://hg.openjdk.java.net/jdk/jdk/

while [ $? -ne 0 ]
do

  hg clone http://hg.openjdk.java.net/jdk/jdk/

done
```

## Configure

- my shell script
```shell
JDK=/home/fool/fujie/workspace/jdk-dev
JTREG=/home/fool/fujie/workspace/jtreg/build/images/jtreg
BOOTJDK=/opt/jdk-12.0.1

rm ${JDK}/build -rf

cd ${JDK}

bash ${JDK}/make/devkit/createJMHBundle.sh
bash test/hotspot/jtreg/compiler/graalunit/downloadLibs.sh  ${JDK}/build/graal-unit-lib

COMMON="--with-boot-jdk=${BOOTJDK} --disable-warnings-as-errors --with-jmh=build/jmh/jars --with-jtreg=${JTREG} --with-graalunit-lib=${JDK}/build/graal-unit-lib"

bash ${JDK}/configure ${COMMON}
bash ${JDK}/configure ${COMMON} --with-debug-level=slowdebug
bash ${JDK}/configure ${COMMON} --with-debug-level=fastdebug
```

- more configurations
```shell
bash configure --disable-warnings-as-errors --enable-debug --with-jvm-variants=server --enable-dtrace

# Run jtreg tests and microbenchmarks
bash configure --disable-warnings-as-errors --with-debug-level=fastdebug --with-boot-jdk=/opt/jdk-11.0.1 --with-jmh=build/jmh/jars --with-jtreg=/home/fool/fujie/workspace/jtreg/build/images/jtreg
```

- Disable C1
```
--with-jvm-features=-compiler1
```

Try `-XX:+NeverActAsServerClassMachine` flag which sets configuration similar to old Client VM (C1 JIT + SerialGC)

### For JDK8
```shell
bash ./configure --with-boot-jdk=/opt/jdk1.8.0_191 --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-extra-cflags=-Wno-error --with-debug-level=slowdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk=/opt/jdk1.8.0_191 --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-extra-cflags=-Wno-error --with-debug-level=fastdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk=/opt/jdk1.8.0_191 --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-extra-cflags=-Wno-error
```

#### Common errors when building jdk8
- cc1plus: all warnings being treated as errors
```
configure --with-extra-cflags=-Wno-error
```

- jdk8u60/hotspot/make/linux/Makefile:238: recipe for target 'check_os_version' failed
```
$ make CONF=re images DISABLE_HOTSPOT_OS_VERSION_CHECK=ok
```

## Make
```shell
make CONF=slow images; make CONF=fast images; make CONF=rel images;

# make images CONF=slowdebug ZIP_DEBUGINFO_FILES=0

# For jdk12: build images twice, second time with newly built JDK
make bootcycle-images

make CONF=slow run-test-tier1 # configured --with-jtreg=<jtreg-path>
```

# Build zero
```
bash ./configure --with-jvm-variants=zero
```

## For JDK8

```shel
# yum install libffi-dev
bash ./configure --with-jvm-variants=zero --with-jvm-interpreter=cpp
make CONF=zero DEBUG_BINARIES=true images
```

# OpenJDK on Mac

## --with-sysroot

```
checking for sdk name...
configure: error: No xcodebuild tool and no system framework headers found, use --with-sysroot or --with-sdk-name to provide a path to a valid SDK
/Users/fool/workspace/open/jdk/build/.configure-support/generated-configure.sh: line 82: 5: Bad file descriptor
configure exiting with result code 1
```

```
$ xcrun --sdk macosx --show-sdk-path
/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk
```

## --with-libffi

- For zero build
```
bash ${JDK}/configure ${COMMON} --with-jvm-variants=zero --with-libffi=/usr/local/Cellar/libffi/3.2.1/lib/libffi-3.2.1
```

# Update jdk doc

- pandoc 2.3.1 or newer is recommended
```
make update-build-docs
```

# Testing

```shell
# export LC_ALL=C
make test TEST="tier1 tier2 tier3" CONF=rel  JTREG="JOBS=4"
make test TEST="tier1 tier2 tier3" CONF=fast JTREG="JOBS=4"
```

## How to refuse jtreg java options

```
opts.appendTestJavaOptions = false;
```

## docker tests 

- Install on Ubuntu 18.04
```
$ sudo apt install docker.io
$ sudo systemctl start docker
$ sudo systemctl enable docker
```

- Installatin Verify
```
$ docker -v
$ docker ps
```

```
$ docker ps
Got permission denied while trying to connect to the Docker daemon socket at unix:///var/run/docker.sock: Get http://%2Fvar%2Frun%2Fdocker.sock/v1.39/containers/json: dial unix /var/run/docker.sock: connect: permission denied
$ ll /var/run/docker.sock
srw-rw---- 1 root docker 0 3月  21 10:29 /var/run/docker.sock=
```

https://www.jianshu.com/p/95e397570896
```
$ sudo gpasswd -a fool docker
[sudo] password for fool: 
Adding user fool to group docker

$ sudo service docker restart
$ newgrp - docker
```

Fix network
http://www.linuxdiyf.com/linux/28252.html
```
 问题
在ubuntu16.04上安装完docker后，发现电脑无法上网了。
由于是在公司网络，使用了代理上网（代理地址：172.17.18.88）。安装docker（1.7.5）后docker创建了一个虚拟网络桥连，恰好也使用了172.17.1.0/16的网段，导致电脑配置的代理服务器172.17.18.88直接找到了docker的网段IP(172.17.0.1)，从而导致无法上网。
 
解答
找到了原因，我们就让docker避开172.17.18.0/24网段。所幸改成192.168.7.1/24，也避免与一般的家用路由器IP段相撞。
局域网保留地址：
A类：10.0.0.0/8 10.0.0.0-10.255.255.255
B类：172.16.0.0/12 172.16.0.0-172.31.255.255
C类：192.168.0.0/16 192.168.0.0～192.168.255.255
下面修改docker配置，以ubuntu16.04为例。
修改之前先停止docker服务，然后删除docker0的网络配置。
sudo vim /etc/default/docker 
#添加1行：
DOCKER_OPTS="--bip=192.168.7.1/24"
sudo vim /etc/systemd/system/docker.service 
#如果docker.service文件不存在，则看/lib/systemd/system/docker.service文件
#添加：
[Service]
EnvironmentFile=-/etc/default/docker
#修改
ExecStart=/usr/bin/dockerd -H fd:// $DOCKER_OPTS
配置完成后重新docker服务即可
```

## jtreg

```
export LC_ALL=C
```

```
make CONF=release test TEST="jtreg:test/hotspot:hotspot_compiler"
make CONF=release test TEST="test/hotspot/jtreg/compiler/floatingpoint/TestFloatJNIArgs.java"
make CONF=release test TEST="compiler/floatingpoint/TestFloatJNIArgs.java"
```

- https://bugs.openjdk.java.net/browse/JDK-8223537
```
Tier6 run tests with next 2 combinations of flags (Tiered on/off):
-Xcomp -ea -esa -XX:CompileThreshold=100 -XX:+TieredCompilation
-Xcomp -ea -esa -XX:CompileThreshold=100 -XX:-TieredCompilation

You can run jtreg tests with -javaoptions:'<flags>' to have the same testing. 
```

### Specify my own ProblemList.txt

```
Ver="server-release"
ARGS="VM_OPTIONS=;OPTIONS=-exclude:/XXX/workspace/jdk/mylist.txt"

Ben="tier1 tier2 tier3"
make test TEST="${Ben}" JTREG="JOBS=5;${ARGS}" CONF=${Ver}
```

### graal-unit

```
make test TEST="compiler/graalunit/" CONF=release JTREG="VERBOSE=all;VM_OPTIONS=-XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI"

-XX:+CreateCoredumpOnCrash -ea -esa -server -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -Djvmci.Compiler=graal -XX:+TieredCompilation
```

### jcstress

```
make test TEST="applications/jcstress/" CONF=release JTREG="JAVA_OPTIONS=-Djdk.test.lib.artifacts.jcstress-tests-all=/PATH-TO-YOUR/jcstress.jar"
```

### SciMark

```
make test TEST="applications/scimark/Scimark.java" CONF=release JTREG="JAVA_OPTIONS=-Djdk.test.lib.artifacts.scimark=/home/fool/jdk-dev/build/scimark2lib.jar"
```

## gtest

## Java Microbenchmark Harness

```shell
sh make/devkit/createJMHBundle.sh
```

```
commons-math3-3.2.jar
jmh-core-1.21.jar
jmh-generator-annprocess-1.21.jar
jopt-simple-4.6.jar
Created /home/fool/jdk12/make/devkit/../../build/jmh/jmh-1.21.tar.gz

~/jdk12/build$ tree jmh
jmh
├── jars
│   ├── commons-math3-3.2.jar
│   ├── jmh-core-1.21.jar
│   ├── jmh-generator-annprocess-1.21.jar
│   └── jopt-simple-4.6.jar
└── jmh-1.21.tar.gz

1 directory, 5 files
```
Then configure --with-jmh=build/jmh/jars

- Running
```
make test TEST="micro"
```

- More info.
http://openjdk.java.net/projects/code-tools/jmh/

## Native Methods

```shell
JAVA_HOME="/home/loongson/fujie/jdk-mips/build/linux-mips64el-normal-server-release/images/jdk"
JAVA_HOME="/home/loongson/fujie/jdk-mips/build/linux-mips64el-normal-server-slowdebug/images/jdk"
DST="/home/loongson/fujie/jdk-mips/test/hotspot/jtreg/compiler/floatingpoint"

# OpenJDK 64-Bit Server VM warning: You have loaded library /home/loongson/fujie/jdk-mips/test/hotspot/jtreg/compiler/floatingpoint/libTestFloatJNIArgs.so which might have disabled stack guard. The VM will try to fix the stack guard now.
# It's highly recommended that you fix the library with 'execstack -c <libfile>', or link it with '-z noexecstack'.
# https://sourceware.org/ml/libc-alpha/2016-01/msg00567.html

#cd $DST
#gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -fPIC -shared -o libTestFloatJNIArgs.so libTestFloatJNIArgs.c
#gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -z noexecstack -fPIC -shared -o libTestFloatJNIArgs.so libTestFloatJNIArgs.c
#cd -

#${JAVA_HOME}/bin/javac  compiler/floatingpoint/TestFloatJNIArgs.java

${JAVA_HOME}/bin/java -Djava.library.path="${DST}"  compiler.floatingpoint.TestFloatJNIArgs
```

# Debugging

- Decode assembly instructions
https://www.onlinedisassembler.com/odaweb/

## Build hsdis
- Download the GNU binutils
```shell
cd /home/fool/fujie/workspace/tools
wget https://ftp.gnu.org/gnu/binutils/binutils-2.30.tar.lz
sudo apt install lzip
lzip -d binutils-2.30.tar.lz
tar xvf binutils-2.30.tar
```

- make
```shell
export BINUTILS="/home/fool/fujie/workspace/tools/binutils-2.30"
make all64
# make BINUTILS=binutils-2.24 ARCH=amd64 CFLAGS=-Wno-error
```
This will generate hsdis-amd64.so on x86_64 machines.

- cp hsdis
```shell
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-slowdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-fastdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-release/images/jdk/lib/server/
```

## PrintAssembly

```
-XX:+PrintAssembly
-XX:PrintAssemblyOptions=intel
```
```
-XX:PrintAssemblyOptions=hsdis-print-bytes
```

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

## Others

```
jdk-dev/build/linux-x64-debug/images/jdk/bin/java -cp classes_7 -Xlog:vtables*=trace p2.D
jtreg -jdk:/oracle/valhallaL/build/linux-x86_64-server-fastdebug/jdk/ -va -javaoptions:"-Xint -DVerifyVM=true" -J-Djavatest.maxOutputSize=10000000 test/hotspot/jtreg/compiler/valhalla/valuetypes/TestLWorld.java
```

# Other Useful Scripts

## OpenJDK8

### Get Versions
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

### Export Patches
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

### Upgradation
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

# References

[OpenJDK](http://openjdk.java.net/)

[OpenJDK Projects](http://hg.openjdk.java.net/)

[OpenJDK wiki](https://wiki.openjdk.java.net/)

[idealgraphvisualizer](https://lafo.ssw.uni-linz.ac.at/pub/idealgraphvisualizer/)

[c1visualizer](https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/c1visualizer-1.7.zip)

[graal-external-deps](https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/)

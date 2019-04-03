# OpenJDK Community

## Useful Websites 

- OpenJDK homepage
http://openjdk.java.net/

- OpenJDK wiki
https://wiki.openjdk.java.net/

- OpenJDK Main-line code repository
http://hg.openjdk.java.net/jdk/jdk/

- JBS
https://bugs.openjdk.java.net

- Webrev
http://cr.openjdk.java.net
http://cr.openjdk.java.net/~jiefu/

- OpenJDK Web Site Terms of Use
http://openjdk.java.net/legal/tou/

## How to Use Webrev

```
wget http://hg.openjdk.java.net/code-tools/webrev/raw-file/tip/webrev.ksh

ksh webrev.ksh  -N -r 53868
ksh webrev.ksh -u jiefu -N -r 53947

mkdir 8219519

mv webrev 8219519/webrev.00

scp -r 8219519 jiefu@cr.openjdk.java.net:
```

```
$ hg summary
parent: 53868:1bd7233074c1 tip
 8219486: Missing reg_mask_init() breaks x86_32 build
branch: default
commit: 2 removed, 5 unknown
update: (current)
```

## Submit Repo

https://wiki.openjdk.java.net/display/Build/Submit+Repo

jdk/submit only runs tier1 testing

## FAQ

- How can I tell if I, or someone else, has signed the OCA?
A list of OCA signatories is available [here](www.oracle.com/technetwork/community/oca-486395.html)

- How do I change my OpenJDK e-mail address of record?
Send a message to registrar@openjdk.java.net

## TODO

[JBS Overview](https://wiki.openjdk.java.net/display/general/JBS+Overview)
[Community Code Review](http://openjdk.java.net/guide/codeReview.html)

# Building

## Build OpenJDK

### Fetch Code
```shell
hg clone http://hg.openjdk.org/jdk/jdk/ # Now is jdk12
```

### Configure

- my shell script
```shell
JDK=/home/fool/jdk/
JTREG=/home/fool/fujie/workspace/jtreg/build/images/jtreg

rm ${JDK}/build -rf

cd ${JDK}

bash ${JDK}/make/devkit/createJMHBundle.sh

bash ${JDK}/configure --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=slowdebug --disable-warnings-as-errors --with-jmh=build/jmh/jars --with-jtreg=${JTREG}
bash ${JDK}/configure --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=fastdebug --disable-warnings-as-errors --with-jmh=build/jmh/jars --with-jtreg=${JTREG}
bash ${JDK}/configure --with-boot-jdk='/opt/jdk-11.0.1'                              --disable-warnings-as-errors --with-jmh=build/jmh/jars --with-jtreg=${JTREG}

exit 0

make CONF=slow images
make CONF=rel  images
make CONF=fast images
```

- more configurations
```shell
bash configure --with-boot-jdk=/opt/jdk-11.0.1 --disable-warnings-as-errors --with-debug-level=slowdebug --with-native-debug-symbols=external
bash configure --with-boot-jdk=/opt/jdk-11.0.1 --disable-warnings-as-errors --with-debug-level=fastdebug --with-native-debug-symbols=external
bash configure --with-boot-jdk=/opt/jdk-11.0.1 --disable-warnings-as-errors --with-debug-level=release

# It is recommended to build a 32-bit JVM on a 64-bit machine.
bash configure --disable-warnings-as-errors --with-freetype=/cygdrive/c/freetype-i586 --with-target-bits=32

bash configure --disable-warnings-as-errors --enable-debug --with-jvm-variants=server --enable-dtrace

# To be able to run the jtreg tests and microbenchmarks
bash configure --disable-warnings-as-errors --with-debug-level=fastdebug --with-boot-jdk=/opt/jdk-11.0.1 --with-jmh=build/jmh/jars --with-jtreg=/home/fool/fujie/workspace/jtreg/build/images/jtreg
```

- Disable C1
```
--with-jvm-features=-compiler1
```

### Make
```shell
make CONF=slow images; make CONF=fast images; make CONF=rel images;

# make images CONF=slowdebug ZIP_DEBUGINFO_FILES=0

# For jdk12: build images twice, second time with newly built JDK
make bootcycle-images
make CONF=slow run-test-tier1 # configured --with-jtreg=<jtreg-path>
```

## Build Failed with Netbeans

### Failed Log

```
Creating support/demos/image/jfc/CodePointIM/CodePointIM.jar
Creating support/demos/image/jfc/SwingSet2/SwingSet2.jar
Creating support/demos/image/jfc/Font2DTest/Font2DTest.jar
Creating support/demos/image/jfc/FileChooserDemo/FileChooserDemo.jar
Note: /home/fool/fujie/workspace/jdk/src/demo/share/jfc/Stylepad/Stylepad.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
Creating support/demos/image/jfc/Metalworks/Metalworks.jar
Creating support/demos/image/jfc/Notepad/Notepad.jar
Creating support/demos/image/jfc/SampleTree/SampleTree.jar
Creating support/demos/image/jfc/Stylepad/Stylepad.jar
Note: Some input files use or override a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
Note: /home/fool/fujie/workspace/jdk/src/demo/share/jfc/TableExample/TableExample4.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
Creating support/demos/image/jfc/TableExample/TableExample.jar
Creating support/demos/image/jfc/TransparentRuler/TransparentRuler.jar
Note: Some input files use or override a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
Note: Some input files use unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
Creating support/demos/image/jfc/J2Ddemo/J2Ddemo.jar
Compiling 1 files for CLASSLIST_JAR
Creating support/classlist.jar
GenerateLinkOptData.gmk:63: recipe for target '/home/fool/fujie/workspace/jdk/build/linux-x86_64-server-slowdebug/support/link_opt/classlist' failed
make[3]: *** [/home/fool/fujie/workspace/jdk/build/linux-x86_64-server-slowdebug/support/link_opt/classlist] Error 1
make/Main.gmk:471: recipe for target 'generate-link-opt-data' failed
make[2]: *** [generate-link-opt-data] Error 2

ERROR: Build failed for target 'images' in configuration 'linux-x86_64-server-slowdebug' (exit code 2) 
Stopping sjavac server

=== Make failed targets repeated here ===
GenerateLinkOptData.gmk:63: recipe for target '/home/fool/fujie/workspace/jdk/build/linux-x86_64-server-slowdebug/support/link_opt/classlist' failed
make/Main.gmk:471: recipe for target 'generate-link-opt-data' failed
=== End of repeated output ===

Hint: Try searching the build log for the name of the first failed target.
Hint: See doc/building.html#troubleshooting for assistance.

/home/fool/fujie/workspace/jdk/make/Init.gmk:305: recipe for target 'main' failed
make[1]: *** [main] Error 2
/home/fool/fujie/workspace/jdk/make/Init.gmk:186: recipe for target 'images' failed
make: *** [images] Error 2

BUILD FAILED (exit value 2, total time: 3m 59s)
```

### Fail Reason

- Left: failed; Right: passed
```
  configure: Configuration created at Thu Dec 27 14:17:13 CST 2018.                                   |  configure: Configuration created at Thu Dec 27 14:11:44 CST 2018.                                  
  checking for basename... /usr/bin/basename                                                          |  checking for basename... /usr/bin/basename
  checking for bash... /bin/bash                                                                      |  checking for bash... /bin/bash
  checking for cat... /bin/cat                                                                        |  checking for cat... /bin/cat
  checking for chmod... /bin/chmod                                                                    |  checking for chmod... /bin/chmod
  checking for cmp... /usr/bin/cmp                                                                    |  checking for cmp... /usr/bin/cmp
  checking for comm... /usr/bin/comm                                                                  |  checking for comm... /usr/bin/comm
+ +-- 98 lines: checking for cp... /bin/cp------------------------------------------------------------|+ +-- 98 lines: checking for cp... /bin/cp-----------------------------------------------------------
  checking full docs... no, missing dependencies                                                      |  checking full docs... no, missing dependencies
  checking for cacerts file... default                                                                |  checking for cacerts file... default
  checking for jni library path... default                                                            |  checking for jni library path... default
  checking if packaged modules are kept... yes (default)                                              |  checking if packaged modules are kept... yes (default)
  checking for version string... 13-internal+0-adhoc.fool.jdk                                         |  checking for version string... 13-internal+0-adhoc.fool.jdk
  checking for javac... /opt/jdk-11.0.1/bin/javac                                                     |  checking for javac... /opt/jdk-11.0.1/bin/javac
  checking for java... /usr/bin/java                                                                  |  checking for java... /opt/jdk-11.0.1/bin/java                                                      
  configure: Found potential Boot JDK using java(c) in PATH                                           |  configure: Found potential Boot JDK using java(c) in PATH
  checking for Boot JDK... /opt/jdk-11.0.1                                                            |  checking for Boot JDK... /opt/jdk-11.0.1
  checking Boot JDK version... java version "11.0.1" 2018-10-16 LTS Java(TM) SE Runtime Environment 18|  checking Boot JDK version... java version "11.0.1" 2018-10-16 LTS Java(TM) SE Runtime Environment 1
  checking for java in Boot JDK... ok                                                                 |  checking for java in Boot JDK... ok
  checking for javac in Boot JDK... ok                                                                |  checking for javac in Boot JDK... ok
  checking for javadoc in Boot JDK... ok                                                              |  checking for javadoc in Boot JDK... ok
+ +--171 lines: checking for jar in Boot JDK... ok----------------------------------------------------|+ +--171 lines: checking for jar in Boot JDK... ok---------------------------------------------------
```

- Difference: checking for java...
```
$ /usr/bin/java -version
openjdk version "10.0.2" 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4)
OpenJDK 64-Bit Server VM (build 10.0.2+13-Ubuntu-1ubuntu0.18.04.4, mixed mode)
```

### Try to Fix

```shell
$ sudo mv /usr/bin/java /usr/bin/java-old
$ sudo ln -s /opt/jdk-11.0.1/bin/java /usr/bin/java
```
Not work.

### More Experiments

|Settings|Results|
|-|-|
|netbeans8.2 running on jdk8, sh configure --with-debug-level=slowdebug --with-boot-jdk=/opt/jdk-11.0.1|passed|
|netbeans8.2 running on jdk8, sh configure --with-debug-level=slowdebug|failed|
|netbeans9.0 running on jdk11, sh configure --with-debug-level=slowdebug|passed|
|netbeans8.2 running on jdk11, sh configure --with-debug-level=slowdebug|netbeans didn't work|

## Build zero
```
bash ./configure --with-jvm-variants=zero
```

## Update jdk doc

- pandoc 2.3.1 or newer is recommended
```
make update-build-docs
```

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
# make BINUTILS=binutils-2.24 ARCH=amd64 CFLAGS=-Wno-error
```
This will generate hsdis-amd64.so on x86_64 machines.

### cp hsdis

```shell
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-slowdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-fastdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-release/images/jdk/lib/server/
```

# Testing

```
export LC_ALL=C
make test TEST="tier1 tier2 tier3" JTREG="JOBS=4"
-------------------------------------------------

Running tests using JTREG control variable 'JOBS=4'
Test selection 'tier1 tier2 tier3', will run:
* jtreg:test/hotspot/jtreg:tier1
* jtreg:test/jdk:tier1
* jtreg:test/langtools:tier1
* jtreg:test/nashorn:tier1
* jtreg:test/jaxp:tier1
* jtreg:test/jdk:tier2
* jtreg:test/langtools:tier2
* jtreg:test/nashorn:tier2
* jtreg:test/jaxp:tier2
* jtreg:test/jdk:tier3
* jtreg:test/langtools:tier3
* jtreg:test/nashorn:tier3
* jtreg:test/jaxp:tier3
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

# References

[JEP 3: JDK Release Process](http://openjdk.java.net/jeps/3)

[JDK 12](http://openjdk.java.net/projects/jdk/12/)

[OpenJDK](http://openjdk.java.net/)

[OpenJDK Projects](http://hg.openjdk.org/)

[OpenJDK wiki](https://wiki.openjdk.java.net/)

[idealgraphvisualizer](https://lafo.ssw.uni-linz.ac.at/pub/idealgraphvisualizer/)

[c1visualizer](https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/c1visualizer-1.7.zip)

[graal-external-deps](https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/)

[Becoming an Author](http://openjdk.java.net/projects/)

[OpenJDK people](http://db.openjdk.java.net/people/)

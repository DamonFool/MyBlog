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

## jtreg

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

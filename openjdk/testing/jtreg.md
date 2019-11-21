# Build jtreg

## Download the Source Code

```shell
hg clone http://hg.openjdk.java.net/code-tools/jtreg
```

## Build

```shell
$ cd jtreg/
$ sh ./make/build-all.sh /opt/jdk1.8.0_191
```

If the build succeeds,
```shell
$ cd build/images/jtreg/bin
$ ls
jtdiff  jtreg
$ ./jtreg -version
jtreg, version 4.2 dev b13
Installed in /home/fool/fujie/workspace/jtreg/build/images/jtreg/lib/jtreg.jar
Running on platform version 1.8.0_191 from /opt/jdk1.äºæ 17, 2018.
Copyright (c) 1999, 2018, Oracle and/or its affiliates. All rights reserved.
Use is subject to license terms.
JTHarness: version 6.0
JCov 3.0-2
TestNG (testng.jar): version unknown
TestNG (jcommander.jar): version 1.72
AsmTools: version 7.0
```

# Testing Using jtreg

## Environment Variables of jtreg

|EnvVars|Decription|
|-|-|
|JT_JAVA|JDK to run the jtreg|

## Flags of jtreg
|Flags|Decription|
|-|-|
|jtreg -jdk:test-jdk ...|-jdk specify the JDK to be tested|
|jtreg -w build-dir/jtreg/work -r build-dir/jtreg/report ...||
|-dir:dir-base|specify a base directory for test files and directories|
|-verbose:arg|vary the amount of output|
|-exclude:file|avoid running tests listed in that file|
|-manual, -m|run tests which require manual interaction|
|-automatic, -a|run tests without manual interaction|
|-ignore:quiet|avoid running tests labeled with @ignore|
|-jdk:jdk-image|specify the jdk to be tested|
|-agentvm||
|-conc:value|set the maximum tests running concurrently|
|jtreg -jdk:jdk -status:fail,error test-or-folder...|rerun some tests with specified status|
|-timeout:number|scaling factor to extend the default timeout of all tests|
|-vmoption:jvm-args|set the JVM flags to be tested|

## Examples

```shell
export JT_JAVA=/opt/jdk/1.6.0
cd /w/jjg/work/tl/jdk
/opt/jtreg/4.1/linux/bin/jtreg \
    -jdk:/opt/jdk/1.8.0 -agentvm -verbose:summary \
    -w build/jtreg/work -r build/jtreg/report \
    test/java/lang/Class
```

# Useful Scripts

```shell
#!/bin/bash

#export JT_JAVA=""

JT_HOME=/home/fool/fujie/workspace/jtreg/build/images/jtreg

JDK_SRC=/home/fool/fujie/workspace/jdk8u
TEST_JDK=${JDK_SRC}/build/linux-x86_64-normal-server-release/images/j2sdk-image

LOGS=${JDK_SRC}/build/jtreg


${JT_HOME}/bin/jtreg \
  -jdk:${TEST_JDK} \
  -w ${LOGS}/work \
  -r ${LOGS}/report \
  -dir:${JDK_SRC} \
  -agentvm -va -ignore:quiet \
  -timeout:1 -conc:1 \
  -vmoptions:'-Xmx512M -XX:-TieredCompilation' \
  ${JDK_SRC}/jdk/test/java/lang/Class

# for rerunm only
# -status:fail,error,notRun \
```

## Examples by Igor Veresov

https://bugs.openjdk.java.net/browse/JDK-8227003

```shell
time ~/work/jtreg/bin/jtreg -timeout:10 -jdk:/Users/iggy/work/jdk/build/macosx-x86_64-server-fastdebug/images/jdk -vmoptions:'-XX:+UnlockExperimentalVMOptions -XX:-EnableJVMCI -XX:-UseJVMCICompiler' java/lang/invoke/VarHandles/VarHandleTestByteArrayAsInt.java 
```

```
time ~/work/jtreg/bin/jtreg -timeout:10 -jdk:/Users/iggy/work/jdk/build/macosx-x86_64-server-fastdebug/images/jdk -vmoptions:'-ea -esa -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler' java/lang/invoke/VarHandles/VarHandleTestByteArrayAsInt.java 
```

```
time ~/work/jtreg/bin/jtreg -timeout:10 -jdk:/Users/iggy/work/jdk/build/macosx-x86_64-server-fastdebug/images/jdk -vmoptions:'-ea -esa -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -XX:CompileThresholdScaling=0.1' java/lang/invoke/VarHandles/VarHandleTestByteArrayAsInt.java 
```

# jtdiff

```shell
$ ./jtdiff  /home/fool/fujie/workspace/jdk8u/build/jtreg/report /home/fool/fujie/workspace/jdk8u/build/jtreg-old/report
0: /home/fool/fujie/workspace/jdk8u/build/jtreg/report  pass: 33; not run: 6,198
1: /home/fool/fujie/workspace/jdk8u/build/jtreg-old/report  pass: 33; not run: 6,198
```

```shell
$ ./jtdiff -format:html -o:fu.html /home/fool/fujie/workspace/jdk8u/build/jtreg/report /home/fool/fujie/workspace/jdk8u/build/jtreg-old/report
$ ./jtdiff -format:text -o:fu.txt /home/fool/fujie/workspace/jdk8u/build/jtreg/report /home/fool/fujie/workspace/jdk8u/build/jtreg-old/report
```

# References

[Regression Test Harness for the JDK: jtreg](http://openjdk.java.net/jtreg/index.html)

[Building jtreg](http://openjdk.java.net/jtreg/build.html)

[Running tests using jtreg](http://openjdk.java.net/jtreg/runtests.html)

[Using JVM and javac options with jtreg](http://openjdk.java.net/jtreg/vmoptions.html)

# Building & Running 

```shell
# Building the suite
tools/sbt/bin/sbt assembly

# Running 
# java -jar '<renaissance-home>/target/renaissance-0.9.0.jar' <benchmarks>
```

# List of Benchmarks

- group list 
```shell
$ java -jar renaissance-0.9.0.jar  --group-list
actors
apache-spark
database
dummy
jdk-concurrent
jdk-streams
neo4j
rx
scala-dotty
scala-stdlib
scala-stm
twitter-finagle
```

- raw list
```shell
$ java -jar renaissance-0.9.0.jar  --raw-list
akka-uct
als
chi-square
db-shootout
dec-tree
dotty
dummy
finagle-chirper
finagle-http
fj-kmeans
future-genetic
gauss-mix
log-regression
mnemonics
movie-lens
naive-bayes
neo4j-analytics
page-rank
par-mnemonics
philosophers
reactors
rx-scrabble
scala-kmeans
scala-stm-bench7
scrabble
```

# Help Info

```shell
$ java -jar renaissance-0.9.0.jar  --help
Renaissance Benchmark Suite, version 0.9.0
Usage: renaissance [options] [benchmark-specification]

  -h, --help               Prints this usage text.
  -r, --repetitions <value>
                           Number of repetitions used with the fixed-iterations policy.
  -w, --warmup-seconds <value>
                           Number of warmup seconds, when using time-based policies.
  -t, --run-seconds <value>
                           Number of seconds to run after the warmup, when using time-based policies.
  --policy <value>         Execution policy, one of: fixed-warmup, fixed-iterations
  --plugins <value>        Comma-separated list of class names of plugin implementations.
  --csv <value>            Output results to CSV file.
  --json <value>           Output results to JSON file.
  --readme                 Regenerates the README file, and does not run anything.
  --functional-test        Reduce iteration times significantly for testing purposes.
  --list                   Print list of benchmarks with their description.
  --raw-list               Print list of benchmarks (each benchmark name on separate line).
  --group-list             Print list of benchmark groups (each group name on separate line).
  benchmark-specification  Comma-separated list of benchmarks (or groups) that must be executed (or all).
```

# Configuration

## --functional-test

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  --functional-test          \
  -r 1                       \
  all
```

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  --functional-test          \
  -r 3                       \
  dotty,scala-kmeans
```

## startup

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  -r 1                       \
  all
```

## --fixed-iterations

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  -r 4                       \
  all
```

## --fixed-warmup

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-warmup      \
  -w 120                     \
  -t 240                     \
  all
```

# Running Script

## Startup Performance

```shell
num=0

while(true);
do
      let num++
      echo "Round: ${num}"

      JDK=/home/loongson/fujie/jdk8-mips/build/linux-mips64-normal-server-release/images/j2sdk-image
      ${JDK}/bin/java \
        -jar renaissance-0.9.0.jar \
        --policy fixed-iterations \
        -r 1 \
        all  | tee jdk8-${num}.log

      sleep 3s  # s

      JDK=/home/loongson/fujie/jdk-mips/build/linux-mips64el-normal-server-release/images/jdk
      ${JDK}/bin/java \
        -jar renaissance-0.9.0.jar \
        --policy fixed-iterations \
        -r 1 \
        all  | tee jdk12-${num}.log
done
```

## Peak Performance

```shell
num=0

while(true);
do
      let num++
      echo "Round: ${num}"

      JDK=/home/loongson/fujie/jdk8-mips/build/linux-mips64-normal-server-release/images/j2sdk-image
      ${JDK}/bin/java \
        -jar renaissance-0.9.0.jar \
        --policy fixed-warmup \
        -w 120 \
        -t 240 \
        all  | tee jdk8-${num}.log

      sleep 3s  # s

      JDK=/home/loongson/fujie/jdk-mips/build/linux-mips64el-normal-server-release/images/jdk
      ${JDK}/bin/java \
        -jar renaissance-0.9.0.jar \
        --policy fixed-warmup \
        -w 120 \
        -t 240 \
        all  | tee jdk12-${num}.log
done
```

# Failure

## MIPS

```
Error during tear-down: null
java.lang.NullPointerException
	at org.lmdbjava.bench.LevelDb$CommonLevelDb.teardown(LevelDb.java:128)
	at org.lmdbjava.bench.LevelDb$Reader.teardown(LevelDb.java:233)
	at org.renaissance.database.DbShootout.tearDownAfterAll(DbShootout.scala:98)
	at org.renaissance.RenaissanceBenchmark.runBenchmark(RenaissanceBenchmark.java:102)
	at org.renaissance.RenaissanceSuite$.$anonfun$main$2(renaissance-suite.scala:298)
	at org.renaissance.RenaissanceSuite$.$anonfun$main$2$adapted(renaissance-suite.scala:296)
	at scala.collection.mutable.ResizableArray.foreach(ResizableArray.scala:62)
	at scala.collection.mutable.ResizableArray.foreach$(ResizableArray.scala:55)
	at scala.collection.mutable.ArrayBuffer.foreach(ArrayBuffer.scala:49)
	at org.renaissance.RenaissanceSuite$.main(renaissance-suite.scala:296)
	at org.renaissance.RenaissanceSuite.main(renaissance-suite.scala)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.renaissance.Launcher.main(Launcher.java:21)
Exception occurred in org.renaissance.database.DbShootout@56113384: Could not load library. Reasons: [no leveldbjni64-1.8 in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], no leveldbjni-1.8 in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], no leveldbjni in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], /tmp/libleveldbjni-64-1-297601802768107824.8: /tmp/libleveldbjni-64-1-297601802768107824.8: cannot open shared object file: No such file or directory (Possible cause: architecture word width mismatch)]
java.lang.UnsatisfiedLinkError: Could not load library. Reasons: [no leveldbjni64-1.8 in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], no leveldbjni-1.8 in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], no leveldbjni in java.library.path: [/usr/java/packages/lib, /lib, /usr/lib], /tmp/libleveldbjni-64-1-297601802768107824.8: /tmp/libleveldbjni-64-1-297601802768107824.8: cannot open shared object file: No such file or directory (Possible cause: architecture word width mismatch)]
	at org.fusesource.hawtjni.runtime.Library.doLoad(Library.java:187)
	at org.fusesource.hawtjni.runtime.Library.load(Library.java:143)
	at org.fusesource.leveldbjni.JniDBFactory.<clinit>(JniDBFactory.java:48)
	at org.lmdbjava.bench.LevelDb$CommonLevelDb.setup(LevelDb.java:118)
	at org.lmdbjava.bench.LevelDb$Reader.setup(LevelDb.java:227)
	at org.renaissance.database.DbShootout.setUpBeforeAll(DbShootout.scala:80)
	at org.renaissance.RenaissanceBenchmark.runBenchmark(RenaissanceBenchmark.java:79)
	at org.renaissance.RenaissanceSuite$.$anonfun$main$2(renaissance-suite.scala:298)
	at org.renaissance.RenaissanceSuite$.$anonfun$main$2$adapted(renaissance-suite.scala:296)
	at scala.collection.mutable.ResizableArray.foreach(ResizableArray.scala:62)
	at scala.collection.mutable.ResizableArray.foreach$(ResizableArray.scala:55)
	at scala.collection.mutable.ArrayBuffer.foreach(ArrayBuffer.scala:49)
	at org.renaissance.RenaissanceSuite$.main(renaissance-suite.scala:296)
	at org.renaissance.RenaissanceSuite.main(renaissance-suite.scala)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.renaissance.Launcher.main(Launcher.java:21)
The following benchmarks failed: db-shootout
```

# Result Parser

```python
# -*- coding: utf-8 -*-

import sys

benchmarksList = "akka-uct als chi-square db-shootout dec-tree dotty dummy finagle-chirper finagle-http fj-kmeans future-genetic gauss-mix log-regression mnemonics movie-lens naive-bayes neo4j-analytics page-rank par-mnemonics philosophers reactors rx-scrabble scala-kmeans scala-stm-bench7 scrabble".strip().split()

if len(sys.argv) < 2:
    print('At least one Log file is needed ...')
    exit(-1)

resultDict = {}

for fNum in xrange(1, len(sys.argv)):

    try:
        logFile = open(sys.argv[fNum], 'r')
    except:
        print('Open file %s error!' % sys.argv[fNum])
        exit(-1)

    resultDict[sys.argv[fNum]] = {}
    curDict = resultDict[sys.argv[fNum]]
    for item in benchmarksList:
        curDict[item] = ''

    for line in logFile:

        if '======' in line and 'final iteration completed' in line:
            #====== akka-uct (actors), final iteration completed (10997.509 ms) ======
            tmp = line.strip().split()
            benName = tmp[1]
            score   = tmp[6][1:]
            if curDict.has_key(benName):
                curDict[benName] = score
            else:
                print('Unregistered benchmark: %s' % benName)
        else:
            continue

print('\n\nNow please copy the following output and paste them in your Redmine wiki page ...\n\n')

print('\n|Renaissance Benchmarks'),
for i in xrange(len(sys.argv) - 1):
    print('|'),
print('|\n'),

for ben in benchmarksList:
    print('|%s' % ben),
    for f in sys.argv[1:]:
        curDict = {}
        if resultDict.has_key(f):
            curDict = resultDict[f]
        else:
            print('\n\nNo result dict for file: %s' % f)
            exit(-1)

        if curDict.has_key(ben):
            print('|%s' % curDict[ben]),
        else:
            print('|-'),
    print('|\n'),
```

# Ref

[Renaissance Benchmark Suite](https://github.com/renaissance-benchmarks/renaissance)

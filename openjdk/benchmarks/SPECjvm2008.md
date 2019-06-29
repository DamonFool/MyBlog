# Running Script

```shell
#!/bin/bash

# The whole benchmarks
Ben="startup.helloworld startup.compiler.compiler startup.compiler.sunflow startup.compress startup.crypto.aes startup.crypto.rsa startup.crypto.signverify startup.mpegaudio startup.scimark.fft startup.scimark.lu startup.scimark.monte_carlo startup.scimark.sor startup.scimark.sparse startup.serial startup.sunflow startup.xml.transform startup.xml.validation compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation"

# The startup benchmarks
Ben="startup.helloworld startup.compiler.compiler startup.compiler.sunflow startup.compress startup.crypto.aes startup.crypto.rsa startup.crypto.signverify startup.mpegaudio startup.scimark.fft startup.scimark.lu startup.scimark.monte_carlo startup.scimark.sor startup.scimark.sparse startup.serial startup.sunflow startup.xml.transform startup.xml.validation"

# The non-startup benchmarks
Ben="compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation"

# frequent tested benchmarks
Ben="compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.sor.large scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation"

# remove startup.compiler.sunflow which is blocked on X86
Ben="startup.helloworld startup.compiler.compiler startup.compress startup.crypto.aes startup.crypto.rsa startup.crypto.signverify startup.mpegaudio startup.scimark.fft startup.scimark.lu startup.scimark.monte_carlo startup.scimark.sor startup.scimark.sparse startup.serial startup.sunflow startup.xml.transform startup.xml.validation compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation"

# for recent jdk on x86
Ben="startup.helloworld startup.compress startup.crypto.aes startup.crypto.rsa startup.crypto.signverify startup.mpegaudio startup.scimark.fft startup.scimark.lu startup.scimark.monte_carlo startup.scimark.sor startup.scimark.sparse startup.serial startup.sunflow startup.xml.transform startup.xml.validation compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation"

Ben=""

JDK="/home/fool/fujie/jdk8u/build/linux-x86_64-normal-server-release/images/j2sdk-image"

Threads=""

JVM_ARGS=""

num=0

ver='jdk-11.0.1'


while(true);
do
      let num++
      echo "Round: ${num}"

#      echo 3 > /proc/sys/vm/drop_caches
      JVM_ARGS="-XX:+TieredCompilation "
      Log="fu-per-${ver}-${num}.log"
      date 1>${Log}
      cat /proc/cpuinfo 1>>${Log}
      free -m 1>>${Log}
      cat /etc/issue 1>>${Log}
      uname -a 1>>${Log}
      echo ${JVM_ARGS} 1>>${Log}
      ${JDK}/bin/java \
        ${JVM_ARGS} -jar SPECjvm2008.jar -ikv -coe -ict  -ja "${JVM_ARGS}" \
        ${Ben} 1>>${Log}  2>/dev/null

      sleep 2s   # 57s

done
```

# Result Parser 

```python
# -*- coding: utf-8 -*-

import sys

benchmarksList = "compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation GEO".strip().split()

benchmarksList = "startup.helloworld startup.compiler.compiler startup.compiler.sunflow startup.compress startup.crypto.aes startup.crypto.rsa startup.crypto.signverify startup.mpegaudio startup.scimark.fft startup.scimark.lu startup.scimark.monte_carlo startup.scimark.sor startup.scimark.sparse startup.serial startup.sunflow startup.xml.transform startup.xml.validation compiler.compiler compiler.sunflow compress crypto.aes crypto.rsa crypto.signverify derby mpegaudio scimark.fft.large scimark.lu.large scimark.sor.large scimark.sparse.large scimark.fft.small scimark.lu.small scimark.sor.small scimark.sparse.small scimark.monte_carlo serial sunflow xml.transform xml.validation GEO".strip().split()

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

        if 'Score on ' in line and ' ops/m' in line:
            #Score on xml.transform: 24.71 ops/m
            tmp = line.strip().split()
            benName = tmp[2][:-1]
            score   = tmp[3]
            if curDict.has_key(benName):
                curDict[benName] = score
            else:
                print('Unregistered benchmark: %s' % benName)

        elif 'composite result:' in line and 'ops/m' in line:
            #Noncompliant composite result: 17.43 ops/m
            tmp = line.strip().split()
            score   = tmp[3]
            curDict['GEO'] = score
        else:
            continue

print('\n\nNow please copy the following output and paste them in your Redmine wiki page ...\n\n')

print('\n|SPECjvm2008 Benchmarks'),
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

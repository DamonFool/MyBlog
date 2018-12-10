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

# Data Processing

[A python script for Redmine's wiki format](https://github.com/DamonFool/MyWorkspace/blob/master/script/python/parseSPECjvm2008.py)

# Script

```
Ben="avrora batik eclipse fop h2 jython luindex lusearch lusearch-fix pmd sunflow tomcat tradebeans tradesoap xalan"
Ben="avrora               fop h2 jython luindex lusearch lusearch-fix pmd sunflow        tradebeans tradesoap xalan"

num=0

while(true);
do
      let num++
      echo "Round: ${num}"

      Log="fu-11-${num}.log"
      date 1>${Log}
      cat /proc/cpuinfo 1>>${Log}
      free -m 1>>${Log}
      cat /etc/issue 1>>${Log}
      uname -a 1>>${Log}
      echo ${JVM_ARGS} 1>>${Log}
      for ben in ${Ben}; do
        /home/jiefu/ws/images/jdk11/jdk/bin/java -jar dacapo-9.12-MR1-bach.jar ${ben} 1>>${Log}  2>>${Log}
      done

      sleep 3s   # 57s

#      echo 3 > /proc/sys/vm/drop_caches
      Log="fu-8-${num}.log"
      date 1>${Log}
      cat /proc/cpuinfo 1>>${Log}
      free -m 1>>${Log}
      cat /etc/issue 1>>${Log}
      uname -a 1>>${Log}
      echo ${JVM_ARGS} 1>>${Log}
      for ben in ${Ben}; do
        /home/jiefu/ws/images/jdk8/j2sdk-image/bin/java -jar dacapo-9.12-MR1-bach.jar ${ben} 1>>${Log}  2>>${Log}
      done

      sleep 3s   # 57s

done
```

# Failed Cases

## jdk8

```
batik
tomcat
```

## jdk11

```
batik
eclipse
tomcat
```



# Useful Commands

```shell
tree -L 1 jdk -alh
```

## Debug

```shell
# check for debug version
$ file libjvm.so
libjvm.so: ELF 64-bit LSB shared object, x86-64, version 1 (GNU/Linux), dynamically linked, BuildID[sha1]=55217d0ae8be32c509367e0c7a8d9ac88ec85213, not stripped
$ file libjvm.so
libjvm.so: ELF 64-bit LSB shared object, x86-64, version 1 (GNU/Linux), dynamically linked, BuildID[sha1]=84e129a4db975e78e61a32f40dfb9a42e7d6b707, with debug_info, not stripped

# check for symbols
$ nm libjvm.so
```

- How to Upate *.jar
```shell
#jar xvf rt.jar
#cp /mnt/openjdk/langtools/src/share/classes/com/sun/tools/javac/util/JavacFileManager.java    com/sun/tools/javac/util/
cp /mnt/openjdk/jdk/src/share/classes/java/util/ComparableTimSort.java   java/util/
javac  \
     -classpath  /mnt/j2sdk-image/lib/tools.jar \
      java/util/ComparableTimSort.java
jar uvf rt.jar java/util/ComparableTimSort*.class
```

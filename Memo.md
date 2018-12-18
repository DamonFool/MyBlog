# Useful Commands

```shell
tree -L 1 jdk -alh
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

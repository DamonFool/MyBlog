# Linux Commands

## Help Info

```
whatis command  # 关于command命令的简要说明
info   command  # 关于command命令的详细说明
man    command  # man 3 printf
man -k keyword  # 查询关键字 根据命令中部分关键字来查询命令

which command   # 查看程序的binary文件所在路径
whereis command # 查看程序的搜索路径，当系统中安装了同一软件的多个版本时，不确定使用的是哪个版本时，这个命令就能派上用场
```

## Useful Commands

```shell
/var/log/messages   <--- system log

sudo mount foo.iso /media
tree -L 1 jdk -alh

df -h      # 查看磁盘空间利用大小
du -sh     # 查看当前目录所占空间大小  (-s 递归整个目录的大小)
```

- 补上依赖项
```
$ sudo apt install -f
```

## Debug

```shell
# 查看系统动态库的配置关系
ldconfig -v

# 查看生成二进制文件的gcc版本
readelf -p .comment  vmlinux
objdump -s --section .comment  vmlinux

# check for debug version
$ file libjvm.so
libjvm.so: ELF 64-bit LSB shared object, x86-64, version 1 (GNU/Linux), dynamically linked, BuildID[sha1]=55217d0ae8be32c509367e0c7a8d9ac88ec85213, not stripped
$ file libjvm.so
libjvm.so: ELF 64-bit LSB shared object, x86-64, version 1 (GNU/Linux), dynamically linked, BuildID[sha1]=84e129a4db975e78e61a32f40dfb9a42e7d6b707, with debug_info, not stripped

# check for symbols
$ nm libjvm.so
```

# How to Upate *.jar
```shell
#jar xvf rt.jar
#cp /mnt/openjdk/langtools/src/share/classes/com/sun/tools/javac/util/JavacFileManager.java    com/sun/tools/javac/util/
cp /mnt/openjdk/jdk/src/share/classes/java/util/ComparableTimSort.java   java/util/
javac  \
     -classpath  /mnt/j2sdk-image/lib/tools.jar \
      java/util/ComparableTimSort.java
jar uvf rt.jar java/util/ComparableTimSort*.class
```

# Eclipse

|Command|Description|
|-|-|
|ctrl+shift+T|find a class|
|ctrl+shift+R|find a file|

# Netbeans

|Command|Description|
|-|-|
|ctrl + o|go to type|
|alt + shift + o|go to file|

# VirtualBox

```
Right Ctrl + F    -- 切换到全屏模式
```

# git

- https://stackoverflow.com/questions/24543372/git-cannot-clone-or-push-failed-to-connect-connection-refused
```
$ git pull
fatal: unable to access 'https://github.com/DamonFool/MyWorkspace.git/': Failed to connect to 127.0.0.1 port 53368: Connection refused

$ env|grep -i proxy
NO_PROXY=localhost,127.0.0.0/8,::1
http_proxy=http://127.0.0.1:53368/
UBUNTU_MENUPROXY=1
https_proxy=http://127.0.0.1:53368/
HTTPS_PROXY=http://127.0.0.1:53368/
no_proxy=localhost,127.0.0.0/8,::1
HTTP_PROXY=http://127.0.0.1:53368/

$ unset http_proxy https_proxy HTTPS_PROXY HTTP_PROXY
$ env|grep -i proxy
NO_PROXY=localhost,127.0.0.0/8,::1
UBUNTU_MENUPROXY=1
no_proxy=localhost,127.0.0.0/8,::1
```

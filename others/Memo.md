# 升级 centos 的 GCC 编译器版本

ref: https://blog.ruoyun.vip/post/centosgccupdate/

```
# 安装源 scl
yum install centos-release-scl
# 安装 gcc
yum install devtoolset-8-gcc devtoolset-8-gcc-c++
# 切换 gcc 版本 (仅对当前shell生效)
scl enable devtoolset-8 -- bash
```

```
source /opt/rh/devtoolset-8/enable
```

- For OpenJDK
```
export CC=/opt/rh/devtoolset-6/root/usr/bin/gcc
export CXX=/opt/rh/devtoolset-6/root/usr/bin/g++
```

- For CMake
```
-DCMAKE_C_COMPILER=/opt/rh/devtoolset-8/root/usr/bin/gcc -DCMAKE_CXX_COMPILER=/opt/rh/devtoolset-8/root/usr/bin/g++
```

ref: https://blog.csdn.net/lianshaohua/article/details/90205986

# Basic Operations for Mac

```
fn键+左方向键是HOME
fn键+右方向键是END

fn+上方向键是page up
fn+下方向键是page down
```

## 截图

shift + command + 4

## 面板操作

鼠标右键：二个手指头按压
切换workspace: 三个手指左右滑
网页浏览前后翻页：二个手指左右滑

## terminal

```
create new terminal: command + n

open new tag: command + t
change tag:  command + 1/2/3
              command + shift + t
```

字体缩放：command +/-/0

### bash和zsh切换方法

```
切换到bash
 chsh -s /bin/bash

切换到zsh
 chsh -s /bin/zsh
```

### 修改执行exit的行为

[解决Mac终端exit退出不爽](https://blog.csdn.net/u010164190/article/details/60772827)

## vim

### Support copy & paste 

~/.vimrc文件中插入如下配置：
```
vmap "+y :w !pbcopy<CR><CR>
nmap "+p :r !pbpaste<CR><CR>
```
command＋c 和command ＋ v

### 语法高亮

~/.vimrc文件中插入如下配置：
```
syntax on
```

## Others

```
open *.html
```

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

# crontab

- crontab -e
```
# Edit this file to introduce tasks to be run by cron.
# 
# Each task to run has to be defined through a single line
# indicating with different fields when the task will be run
# and what command to run for the task
# 
# To define the time you can provide concrete values for
# minute (m), hour (h), day of month (dom), month (mon),
# and day of week (dow) or use '*' in these fields (for 'any').# 
# Notice that tasks will be started based on the cron's system
# daemon's notion of time and timezones.
# 
# Output of the crontab jobs (including errors) is sent through
# email to the user the crontab file belongs to (unless redirected).
# 
# For example, you can run a backup of all your user accounts
# at 5 a.m every week with:
# 0 5 * * 1 tar -zcf /var/backups/home.tgz /home/
# 
# For more information see the manual pages of crontab(5) and cron(8)
# 
# m h  dom mon dow   command

# 在凌晨00:01运行
1 0 * * * X.sh

# 每个工作日23:59都进行备份作业
59 23 * * 1,2,3,4,5 X.sh 
59 23 * * 1-5 X.sh 

# 每分钟运行一次
*/1 * * * * XXXX.sh
```

```
crontab –e : 修改 crontab 文件
crontab –l : 显示 crontab 文件
crontab -r : 删除 crontab 文件
crontab -ir : 删除 crontab 文件前提醒用户
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

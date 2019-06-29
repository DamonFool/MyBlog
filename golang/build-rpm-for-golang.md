# 制作RPM包

切记！不要使用 root 用户来执行打包操作。因为这十分危险，所有二进制文件都会在打包前安装至系统中，因此您应该以普通用户身份打包，以防止系统被破坏。

## RPM 基础知识 

若要构建一个标准的 RPM 包，您需要创建 .spec 文件，其中包含软件打包的全部信息。
然后，对此文件执行 rpmbuild 命令，经过这一步，系统会按照步骤生成最终的 RPM 包。
一般情况，您应该把源代码包，比如由开发者发布的以 .tar.gz 结尾的文件，放入 ~/rpmbuild/SOURCES 目录。
将.spec 文件放入 ~/rpmbuild/SPECS 目录，并命名为 "软件包名.spec" 。
当然， 软件包名 就是最终 RPM 包的名字。

同时创建二进制（Binary RPM）和源码软件包（SRPM）
```
$ rpmbuild -ba NAME.spec
```

## RPM常用命令

```
rpm -ivh *.rpm # 安装一个rpm包
rpm -ivh 1.rpm 2.rpm --nodeps # 忽略依赖，强制安装

rpm -qa | grep eclipse
rpm -qa | grep golang     # 查看系统中已经装了些什么

rpm --showrc | grep mips  # 查看内部预定义的量
```

与之配套的有
```
yum search golang # 查看都有些什么包
yum install *  # 安装某个包
yum remove *   # 删除某个包
```

# golang在龙芯上的RPM包制作

20160630

## 下载SRPM包

https://kojipkgs.fedoraproject.org//packages/golang/1.6.2/1.fc24/src/golang-1.6.2-1.fc24.src.rpm  （不带noarch字样）

## 安装SRPM包

```
rpm -ivh golang-1.6.2-1.fc24.src.rpm
```

然后在~/rpmbuild/目录下的SOURCE（go1.6.2.src.tar.gz）和SPECS（golang.spec）找到对应的文件

## 安装生成的RPM包

```
[root@localhost mips64el]# rpm -ivh golang-1.6.2-1.fc21.loongson.mips64el.rpm
error: Failed dependencies:
	go-srpm-macros is needed by golang-1.6.2-1.fc21.loongson.mips64el
	golang-bin = 1.6.2-1.fc21.loongson is needed by golang-1.6.2-1.fc21.loongson.mips64el
	golang-src = 1.6.2-1.fc21.loongson is needed by golang-1.6.2-1.fc21.loongson.mips64el

[root@localhost mips64el]# pwd
/root/rpmbuild/RPMS/mips64el

[root@localhost mips64el]# ls
golang-1.6.2-1.fc21.loongson.mips64el.rpm  golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm

[root@localhost mips64el]# ls
golang-1.6.2-1.fc21.loongson.mips64el.rpm  golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm

[root@localhost mips64el]# cd ..

[root@localhost RPMS]# ls
mips64el  noarch

[root@localhost RPMS]# ls
mips64el  noarch

[root@localhost RPMS]# tree
.
├── mips64el
│   ├── golang-1.6.2-1.fc21.loongson.mips64el.rpm
│   └── golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm
└── noarch
    ├── golang-docs-1.6.2-1.fc21.loongson.noarch.rpm
    ├── golang-misc-1.6.2-1.fc21.loongson.noarch.rpm
    ├── golang-src-1.6.2-1.fc21.loongson.noarch.rpm
    └── golang-tests-1.6.2-1.fc21.loongson.noarch.rpm

2 directories, 6 files

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm 
error: Failed dependencies:
	go-srpm-macros is needed by golang-1.6.2-1.fc21.loongson.mips64el
	golang-bin = 1.6.2-1.fc21.loongson is needed by golang-1.6.2-1.fc21.loongson.mips64el
	golang-src = 1.6.2-1.fc21.loongson is needed by golang-1.6.2-1.fc21.loongson.mips64el

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-
golang-1.6.2-1.fc21.loongson.mips64el.rpm      golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm  

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-
golang-docs-1.6.2-1.fc21.loongson.noarch.rpm   golang-misc-1.6.2-1.fc21.loongson.noarch.rpm   golang-src-1.6.2-1.fc21.loongson.noarch.rpm    golang-tests-1.6.2-1.fc21.loongson.noarch.rpm

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-
golang-docs-1.6.2-1.fc21.loongson.noarch.rpm   golang-misc-1.6.2-1.fc21.loongson.noarch.rpm   golang-src-1.6.2-1.fc21.loongson.noarch.rpm    golang-tests-1.6.2-1.fc21.loongson.noarch.rpm

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm 
error: Failed dependencies:
	go-srpm-macros is needed by golang-1.6.2-1.fc21.loongson.mips64el

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-
golang-docs-1.6.2-1.fc21.loongson.noarch.rpm   golang-misc-1.6.2-1.fc21.loongson.noarch.rpm   golang-src-1.6.2-1.fc21.loongson.noarch.rpm    golang-tests-1.6.2-1.fc21.loongson.noarch.rpm

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-misc-1.6.2-1.fc21.loongson.noarch.rpm 
error: Failed dependencies:
	go-srpm-macros is needed by golang-1.6.2-1.fc21.loongson.mips64el

[root@localhost RPMS]# for file in `find . -name \*.rpm`; do rpm -qp --provides $file |grep "go-srpm" && echo $file ; done

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-misc-1.6.2-1.fc21.loongson.noarch.rpm 
error: Failed dependencies:
	go-srpm-macros is needed by golang-1.6.2-1.fc21.loongson.mips64el

[root@localhost RPMS]# rpm -ivh mips64el/golang-1.6.2-1.fc21.loongson.mips64el.rpm mips64el/golang-bin-1.6.2-1.fc21.loongson.mips64el.rpm noarch/golang-src-1.6.2-1.fc21.loongson.noarch.rpm noarch/golang-misc-1.6.2-1.fc21.loongson.noarch.rpm --nodeps
Preparing...                          ################################# [100%]
Updating / installing...
   1:golang-src-1.6.2-1.fc21.loongson ################################# [ 25%]
   2:golang-bin-1.6.2-1.fc21.loongson ################################# [ 50%]
   3:golang-1.6.2-1.fc21.loongson     ################################# [ 75%]
   4:golang-misc-1.6.2-1.fc21.loongson################################# [100%]

[root@localhost RPMS]# rpm -qa | grep golang
golang-1.6.2-1.fc21.loongson.mips64el
golang-misc-1.6.2-1.fc21.loongson.noarch
golang-src-1.6.2-1.fc21.loongson.noarch
golang-bin-1.6.2-1.fc21.loongson.mips64el
```

# 重要的网址

[Fedora 官方wiki](https://fedoraproject.org/wiki/How_to_create_an_RPM_package/zh-cn)

[下载已发行的对应版本的RPM包](https://dl.fedoraproject.org/pub/archive/fedora/linux/releases/21/Everything/source/SRPMS/g/)

[包含最新的RPM包(可能尚为发布)](http://koji.fedoraproject.org/koji)

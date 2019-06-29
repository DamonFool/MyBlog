# How to build flex and bison on Ubuntu14.04

[Flex on github](https://github.com/westes/flex)

[Download bison](http://mirrors.ustc.edu.cn/gnu/bison/bison-3.1.tar.gz)

## Prepare software dependencies

```shell
sudo apt-get install libtool autopoint Texinfo Help2man
```

```shell
wget https://mirrors.tuna.tsinghua.edu.cn/gnu/gettext/gettext-latest.tar.gz
tar xvzf gettext-latest.tar.gz 
./configure
make -j4
sudo make install
```

```shell
wget https://mirrors.tuna.tsinghua.edu.cn/gnu/automake/automake-1.16.1.tar.gz
tar xvzf
./configure
make -j4
sudo make install
```

## Build and install

```shell
$ ./autogen.sh
$ make -j4
$ make install

$ make pdf # generate the pdf doc
```

# How to disable webkitgtk in Eclipse
```
# added in eclipse.ini
-Dorg.eclipse.swt.browser.UseWebKitGTK=false
-Dorg.eclipse.swt.browser.DefaultType=mozilla
```

# How to install Remarkable on Loongson

## Download the rpm
~~~
wget https://remarkableapp.github.io/files/remarkable-1.87-1.rpm --no-check-certificate
~~~

## Install the rpm
~~~
[root@localhost Remarkable]# rpm -ivh remarkable-1.87-1.rpm
错误：依赖检测失败：
        python3-beautifulsoup4 被 remarkable-1.87-1.noarch 需要
        python3-markdown 被 remarkable-1.87-1.noarch 需要
        wkhtmltopdf 被 remarkable-1.87-1.noarch 需要
~~~

### Install the dependencies
~~~
[root@localhost Remarkable]# yum install python3-beautifulsoup4
[root@localhost Remarkable]# yum install python3-markdown
[root@localhost Remarkable]# yum install wkhtmltopdf
~~~

## Try to run
~~~
[root@localhost Remarkable]# remarkable
** (remarkable:12126): WARNING **: Couldn't connect to accessibility bus: Failed to connect to socket /tmp/dbus-wHbsePYzWB: 拒绝连接
Traceback (most recent call last):
  File "/usr/bin/remarkable", line 65, in <module>
    import remarkable
  File "/usr/lib/python3/dist-packages/remarkable/__init__.py", line 30, in <module>
    from remarkable import RemarkableWindow
  File "/usr/lib/python3/dist-packages/remarkable/RemarkableWindow.py", line 26, in <module>
    gi.require_version('GtkSource', '3.0')
  File "/usr/lib64/python3.4/site-packages/gi/__init__.py", line 104, in require_version
    (namespace, version))
ValueError: Namespace GtkSource not available for version 3.0
~~~

### Install the runtime dependencies
~~~
[root@localhost Remarkable]# yum install gtksourceview3*
~~~

# Tethering

- USB Tethering
- Bluetooth Tethering
- Wifi Tethering

## USB Tethering

首先，务必确保手机端开启了USB Tethering功能。随后，在PC端:

- 检查usbnet模块是否已经加载 (cat /proc/module | grep usbnet)，若没有则加载 (modprobe usbnet)
- 观察usb0 interface是否生成 (ifconfig -a)
- 启用usb0并获取IP (ifconfig usb0 up; dhcpcd usb0)

### How to fix "RTNETLINK answers: File exists" error
```shell
dhclient -r
dhclient usb0
```
If it's not enough, then try
```shell
sudo rm /var/lib/dhcp/dhclient.reases
sudo dhclient usb0
```

### How to configure it?

Append this in /etc/network/interfaces
```shell
auto usb0
allow-hotplug usb0
iface usb0 inet dhcp
```

### References

[Android Tethering: Using Android to access internet on your Linux machine ](http://www.linuxstall.com/android-tethering-using-android-to-access-internet-on-your-linux-machine/)

[dhclient: What does “RTNETLINK answers: File exists” Mean?](https://serverfault.com/questions/601450/dhclient-what-does-rtnetlink-answers-file-exists-mean)

[Get USB tethering from Android device to work on Debian 8](https://superuser.com/questions/909237/get-usb-tethering-from-android-device-to-work-on-debian-8)

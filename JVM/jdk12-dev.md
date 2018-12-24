# Building

## Build OpenJDK

### Fetch Code
```shell
hg clone http://hg.openjdk.org/jdk/jdk/ # Now is jdk12
```

### Configure
```shell
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=slowdebug --with-native-debug-symbols=external
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=fastdebug --with-native-debug-symbols=external
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --disable-warnings-as-errors --with-debug-level=release

# It is recommended to build a 32-bit JVM on a 64-bit machine.
bash configure --disable-warnings-as-errors --with-freetype=/cygdrive/c/freetype-i586 --with-target-bits=32

bash configure --disable-warnings-as-errors --enable-debug --with-jvm-variants=server --enable-dtrace

# To be able to run the jtreg tests and microbenchmarks
bash configure --disable-warnings-as-errors --with-debug-level=fastdebug --with-boot-jdk='/opt/jdk-11.0.1' --with-jmh=build/jmh/jars --with-jtreg=/home/fool/fujie/workspace/jtreg/build/images/jtreg
```

### Make
```shell
make CONF=slow images; make CONF=fast images; make CONF=rel images;

# make images CONF=slowdebug ZIP_DEBUGINFO_FILES=0

# For jdk12: build images twice, second time with newly built JDK
make bootcycle-images
make CONF=slow run-test-tier1 # configured --with-jtreg=<jtreg-path>
```

## Build hsdis
### Download the GNU binutils
```shell
cd /home/fool/fujie/workspace/tools
wget https://ftp.gnu.org/gnu/binutils/binutils-2.30.tar.lz
sudo apt install lzip
lzip -d binutils-2.30.tar.lz
tar xvf binutils-2.30.tar
```

### make
```shell
export BINUTILS="/home/fool/fujie/workspace/tools/binutils-2.30"
make all64
```
This will generate hsdis-amd64.so on x86_64 machines.

### cp hsdis

```shell
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-slowdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-fastdebug/images/jdk/lib/server/
cp ./src/utils/hsdis/build/linux-amd64/hsdis-amd64.so ./build/linux-x86_64-server-release/images/jdk/lib/server/
```

# Testing

## jtreg

## gtest

## Java Microbenchmark Harness

```shell
sh make/devkit/createJMHBundle.sh
```

```
commons-math3-3.2.jar
jmh-core-1.21.jar
jmh-generator-annprocess-1.21.jar
jopt-simple-4.6.jar
Created /home/fool/jdk12/make/devkit/../../build/jmh/jmh-1.21.tar.gz

~/jdk12/build$ tree jmh
jmh
├── jars
│   ├── commons-math3-3.2.jar
│   ├── jmh-core-1.21.jar
│   ├── jmh-generator-annprocess-1.21.jar
│   └── jopt-simple-4.6.jar
└── jmh-1.21.tar.gz

1 directory, 5 files
```
Then configure --with-jmh=build/jmh/jars

# References

[JEP 3: JDK Release Process](http://openjdk.java.net/jeps/3)

[JDK 12](http://openjdk.java.net/projects/jdk/12/)

[OpenJDK](http://openjdk.java.net/)

[OpenJDK Projects](http://hg.openjdk.org/)

[OpenJDK wiki](https://wiki.openjdk.java.net/)

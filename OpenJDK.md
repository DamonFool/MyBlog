# Steps to Build OpenJDK

Only three steps to go: fetch code -> configure -> make.

## 1: Fetch Code
- For OpenJDK8
```shell
hg clone http://hg.openjdk.org/jdk8u/jdk8u/
cd jdk8u
bash get_source.sh # may need a cup of coffee time to download it
```

- For Latest OpenJDK
```shell
hg clone http://hg.openjdk.org/jdk/jdk/ # Now is jdk12
```

## 2: Configure
- For OpenJDK8
```shell
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-debug-level=slowdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l` --with-debug-level=fastdebug --enable-debug-symbols ZIP_DEBUGINFO_FILES=0
bash ./configure --with-boot-jdk='/opt/jdk1.8.0_191' --with-jobs=`cat /proc/cpuinfo  | grep processor | wc -l`
```

- For OpenJDK12
```shell
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=slowdebug --with-native-debug-symbols=internal 
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=fastdebug --with-native-debug-symbols=internal 
bash configure --with-boot-jdk='/opt/jdk-11.0.1' --with-debug-level=release
```

- OpenJDK for MIPS
```shell
hg clone http://hg.loongnix.org/jdk8-mips64-public
bash get_source.sh
```

## 3: Make
```shell
make CONF=slow images; make CONF=fast images; make CONF=rel images;

# make images CONF=slowdebug ZIP_DEBUGINFO_FILES=0
```

# Configure and Build OpenJDK Zero VM
- For OpenJDK8
```shel
# yum install libffi-dev
bash ./configure --with-jvm-variants=zero --with-jvm-interpreter=cpp
make CONF=zero DEBUG_BINARIES=true images
```

# Useful Scripts

## Get Versions of OpenJDK8
```shell
#!/bin/bash

REP=". corba jaxp jaxws langtools nashorn jdk hotspot"

for rep in ${REP}; do

    echo -e "\n\n----------- version for $rep -----------" 
    cd $rep
    hg summary
    cd -

done
```

## Export Patches
```shell
#!/bin/bash

ID="253"
END="275"

while [ ${ID} -lt ${END} ] ;
do
   echo ${ID}
   hg export $ID | tee fu-${ID}.diff
   let ID++
done

tar cvzf  fu-${ID}-${END}.tgz  fu-*.diff
```

## Upgradation for OpenJDK8
```shell
#!/bin/bash

# jdk8u60-b32  jdk8u77-b03
OLD="jdk8u60-b32"
NEW="jdk8u77-b03"
DST="/home/fool/upgrade/77"

SRC="/home/fool/upgrade/jdk8-mips"
REP=". corba jaxp jaxws langtools nashorn jdk hotspot"

for rep in ${REP}; do

    echo "process $rep"
    cd $rep
    hg diff -r $OLD:$NEW > ${DST}/upgrade-${rep}.diff
    cd -

    cd ${SRC}/${rep}
    patch -p1 < ${DST}/upgrade-${rep}.diff
    cd -

done
```

# Reference

[OpenJDK Projects](http://hg.openjdk.org/)

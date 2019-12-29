JCP: Java Community Process (https://www.jcp.org)
JSR: Java Specification Requests
TCK: Technology Compatibility Kit

## Useful Links 

[List of OCA Signatories](www.oracle.com/technetwork/community/oca-486395.html)

[Becoming an Author](http://openjdk.java.net/projects/) && [OpenJDK people](http://db.openjdk.java.net/people/)

[Community Code Review](http://openjdk.java.net/guide/codeReview.html)

[JBS](https://bugs.openjdk.java.net) && [JBS Overview](https://wiki.openjdk.java.net/display/general/JBS+Overview)

[OpenJDK Main Repository](http://hg.openjdk.java.net/jdk/jdk/)

[Submit Repo](https://wiki.openjdk.java.net/display/Build/Submit+Repo) jdk/submit only runs tier1 testing

[Java Development Kit Builds, from Oracle](https://jdk.java.net/)

## How to Use Webrev

[Webrev](http://cr.openjdk.java.net) && [My Webrev](http://cr.openjdk.java.net/~jiefu/) && [OpenJDK Web Site Terms of Use](http://openjdk.java.net/legal/tou/)

### How to prepare the patch?
```shell
# First, just commit your patch with the correct format commit message, including reviewers.
# Then, generate the patch using `ksh webrev.ksh -c 8225648`
```

```
wget http://hg.openjdk.java.net/code-tools/webrev/raw-file/tip/webrev.ksh

ksh webrev.ksh  -N -r 53868
ksh webrev.ksh -u jiefu -N -r 53947

mkdir 8219519

mv webrev 8219519/webrev.00

scp -r 8219519 jiefu@cr.openjdk.java.net:
```

```
$ hg summary
parent: 53868:1bd7233074c1 tip
 8219486: Missing reg_mask_init() breaks x86_32 build
branch: default
commit: 2 removed, 5 unknown
update: (current)
```

## FAQ

### How do I change my OpenJDK e-mail address of record?
Send a message to registrar@openjdk.java.net

## Reference

[OpenJDK](http://openjdk.java.net/) && [OpenJDK wiki](https://wiki.openjdk.java.net/)

[JEP 3: JDK Release Process](http://openjdk.java.net/jeps/3)

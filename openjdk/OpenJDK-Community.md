# OpenJDK Community

## Useful Websites 

- OpenJDK homepage
http://openjdk.java.net/

- OpenJDK wiki
https://wiki.openjdk.java.net/

- OpenJDK Main-line code repository
http://hg.openjdk.java.net/jdk/jdk/

- Java Development Kit builds, from Oracle
https://jdk.java.net/

- JBS
https://bugs.openjdk.java.net

- Webrev
http://cr.openjdk.java.net
http://cr.openjdk.java.net/~jiefu/

- OpenJDK Web Site Terms of Use
http://openjdk.java.net/legal/tou/

## How to Use Webrev

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

## Submit Repo

https://wiki.openjdk.java.net/display/Build/Submit+Repo

jdk/submit only runs tier1 testing

## FAQ

- How can I tell if I, or someone else, has signed the OCA?
A list of OCA signatories is available [here](www.oracle.com/technetwork/community/oca-486395.html)

- How do I change my OpenJDK e-mail address of record?
Send a message to registrar@openjdk.java.net


# References

[OpenJDK](http://openjdk.java.net/)

[Becoming an Author](http://openjdk.java.net/projects/)

[OpenJDK people](http://db.openjdk.java.net/people/)

[JBS Overview](https://wiki.openjdk.java.net/display/general/JBS+Overview)

[Community Code Review](http://openjdk.java.net/guide/codeReview.html)

[JEP 3: JDK Release Process](http://openjdk.java.net/jeps/3)

[OpenJDK wiki](https://wiki.openjdk.java.net/)

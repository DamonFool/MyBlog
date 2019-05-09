# GC Overview 

## Code Lines Statistics

### OpenJDK Version

```
changeset:   54776:7eb3d3ec9b36
tag:         tip
user:        jcbeyler
date:        Wed May 08 20:28:56 2019 -0700
files:       test/hotspot/jtreg/serviceability/jvmti/HeapMonitor/MyPackage/HeapMonitorStatArrayCorrectnessTest.java
description:
8223441: HeapMonitorStatArrayCorrectnessTest fails due to sampling determinism
Summary: Added an error loop to help with convergence
Reviewed-by: cjplummer, sspitsyn
```

### src/hotspot/share/gc/

|GC Component|Lines(*.hpp)|Lines(*.cpp)|Total Lines|
|-|-|-|-|
|shared|19465|21144|40609|
|cms|6682|15657|22339|
|epsilon|546|637|1183|
|g1|17744|28405|46149|
|parallel|8357|15877|24234|
|serial|1346|2045|3391|
|shenandoah|10044|19492|29536|
|z|10582|12646|23228|
|Total|74766|115903|190669|

### src/hotspot/cpu/x86/gc

|GC Component|Lines(*.hpp)|Lines(*.cpp)|Total Lines|
|-|-|-|-|
|shared|184|745|929|
|g1|70|596|666|
|shenandoah|105|1058|1163|
|z|91|480|571|
|Total|450|2879|3329|

### GCs on x86

|GC|Code Lines|
|-|-|
|cms|63877|
|epsilon|42721|
|g1|88353|
|parallel|65772|
|serial|44929|
|shenandoah|72237|
|z|65337|

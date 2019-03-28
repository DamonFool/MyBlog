# ~15% performance degradation due to less optimized inline decision

## Symptom
~15% performance degradation (from 700 ops/m to 600 ops/m) was observed randomly on x86 while running SPECjvm2008's scimark.monte_carlo with -XX:-TieredCompilation.

## Reproduce
It can be always reproduced with the script[1] in less than 5 minutes.

## Reason
The drop was caused by a not-inline decisiion on spec.benchmarks.scimark.utils.Random::<init> in spec.benchmarks.scimark.monte_carlo.MonteCarlo::integrate.

If performance drop occurs:
```
336   71             spec.benchmarks.scimark.monte_carlo.MonteCarlo::integrate (68 bytes)
                        @ 6   spec.benchmarks.scimark.utils.Random::<init> (53 bytes)   call site not reached
          s             @ 22   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
          s             @ 28   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
```

If no performance drop:
```
368   71             spec.benchmarks.scimark.monte_carlo.MonteCarlo::integrate (68 bytes)
                        @ 6   spec.benchmarks.scimark.utils.Random::<init> (53 bytes)   inline (hot)
                          @ 1   java.lang.Object::<init> (1 bytes)   inline (hot)
                          @ 49   spec.benchmarks.scimark.utils.Random::initialize (125 bytes)   inline (hot)
                            @ 14   java.lang.Math::abs (11 bytes)   executed < MinInliningThreshold times
                            @ 19   java.lang.Math::min (11 bytes)   (intrinsic)
          s             @ 22   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
          s             @ 28   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
```

The not-inline decisiion was made by a heuristic here[2].
It was designed not to inline unreached callsites based on profile.count=0 only.

For callers with loops, the profile.count=0 for the callsite may be incorrect and misleading.
Inline decisions based on misleading profile info only may lead to unoptimized compile code.
Actually, the preformance drop of scimark.monte_carlo was just in that case.

The code of spec.benchmarks.scimark.monte_carlo.MonteCarlo::integrate
```
    public final double integrate(int numSamples) {

        Random R = new Random(SEED);

        int underCurve = 0;
        for (int count = 0; count < numSamples; count++) {

            double x = R.nextDouble();
            double y = R.nextDouble();

            if ( x*x + y*y <= 1.0) {
                underCurve ++;
            }
        }
        return ((double) underCurve / numSamples) * 4.0;
    }
```

The profile info when performance drop happened
```
0 new 7 <spec/benchmarks/scimark/utils/Random>
3 dup
4 bipush 113
6 invokespecial 8 <spec/benchmarks/scimark/utils/Random.<init>(I)V>
  0   bci: 6    CounterData         count(0)
9 astore_2
10 iconst_0
11 istore_3
12 iconst_0
13 istore #4
15 fast_iload #4
17 iload_1
18 if_icmpge 58
  16  bci: 18   BranchData          trap(intrinsic_or_type_checked_inlining recompiled) taken(1) displacement(200)
                                    not taken(57586)
21 aload_2
22 invokevirtual 9 <spec/benchmarks/scimark/utils/Random.nextDouble()D>
  48  bci: 22   VirtualCallData     count(60212) nonprofiled_count(0) entries(0)
                                    method_entries(0)
25 dstore #5
27 aload_2
28 invokevirtual 9 <spec/benchmarks/scimark/utils/Random.nextDouble()D>
  104 bci: 28   VirtualCallData     count(54941) nonprofiled_count(0) entries(0)
                                    method_entries(0)
31 dstore #7
33 dload #5
35 dload #5
37 dmul
38 dload #7
40 dload #7
42 dmul
43 dadd
44 dconst_1
45 dcmpg
46 ifgt 52
  160 bci: 46   BranchData          taken(16747) displacement(32)
                                    not taken(46866)
49 iinc #3 1
52 iinc #4 1
55 goto 15
  192 bci: 55   JumpData            taken(58368) displacement(-176)
58 iload_3
59 i2d
60 iload_1
61 i2d
62 ddiv
63 ldc2_w 4.000000
66 dmul
67 dreturn
method data for {method} {0x00007f3881132558} 'integrate' '(I)D' in 'spec/benchmarks/scimark/monte_carlo/MonteCarlo'
0     bci: 6    CounterData         count(0)
16    bci: 18   BranchData          trap(intrinsic_or_type_checked_inlining recompiled) taken(1) displacement(200)
                                    not taken(57586)
48    bci: 22   VirtualCallData     count(60212) nonprofiled_count(0) entries(0)
                                    method_entries(0)
104   bci: 28   VirtualCallData     count(54941) nonprofiled_count(0) entries(0)
                                    method_entries(0)
160   bci: 46   BranchData          taken(16747) displacement(32)
                                    not taken(46866)
192   bci: 55   JumpData            taken(58368) displacement(-176)
--- Extra data:
264   bci: 0    ArgInfoData           0x0  0x0
                            @ 6   spec.benchmarks.scimark.utils.Random::<init> (53 bytes)   call site not reached
              s             @ 22   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
              s             @ 28   spec.benchmarks.scimark.utils.Random::nextDouble (124 bytes)   inline (hot)
```

Obviously, the profile.count=0 (at bci:6) was incorrect, since the callsite was always reached in the caller.
The profile process was started in the loop of the caller, and the callsite(at bci:6, which is outside of the loop) had no chance to be profiled at all when the compilation is triggered.
The callsite just kept the initial status with profile.count=0, which shouldn't be regarded as unreached at all.

So for callers with loops, it may be misleading to make inline decisions based on profile.count=0 only.

## Fix
It might be better to make a little change to the inline heuristic[2].

For callers without loops, the original heuristic works fine.
But for callers with loops, it would be better to make a not-inline decision more conservatively.

To fix this issue, a patch has been proposed: http://cr.openjdk.java.net/~jiefu/monte_carlo-perf-drop/webrev.00/
```
--- old/src/hotspot/share/opto/bytecodeInfo.cpp	2019-03-27 17:48:20.100393526 +0800
+++ new/src/hotspot/share/opto/bytecodeInfo.cpp	2019-03-27 17:48:19.908393402 +0800
@@ -1,5 +1,5 @@
 /*
- * Copyright (c) 1998, 2018, Oracle and/or its affiliates. All rights reserved.
+ * Copyright (c) 1998, 2019, Oracle and/or its affiliates. All rights reserved.
  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
  *
  * This code is free software; you can redistribute it and/or modify it
@@ -374,8 +374,11 @@
       // Inlining was forced by CompilerOracle, ciReplay or annotation
     } else if (profile.count() == 0) {
       // don't inline unreached call sites
-       set_msg("call site not reached");
-       return false;
+      // make a not-inline decision more conservatively for callers with loops
+      if (!caller_method->has_loops() || caller_method->interpreter_invocation_count() > Tier3MinInvocationThreshold) {
+        set_msg("call site not reached");
+        return false;
+      }
     }
   }
```

## Testing
- Running scimark.monte_carlo on jdk/x64 with -XX:-TieredCompilation for about 5000 times, no performance drop
  Also on jdk8u/mips64 with -XX:-TieredCompilation, no performance drop
- Running make test TEST="micro" on jdk/x64, no performance regression
- Running SPECjvm2008 on jdk8u/x64 with -XX:-TieredCompilation, no performance regression

[1] http://cr.openjdk.java.net/~jiefu/monte_carlo-perf-drop/reproduce.sh
```shell
#!/bin/bash

SPECJVM2008="/home/fool/fujie/workspace/jdk-test/SPECjvm2008"
JDK="/home/fool/fujie/workspace/jdk-dev/build/linux-x86_64-server-release/images/jdk"

cd ${SPECJVM2008}

Ben="scimark.monte_carlo"
JVM_ARGS="-XX:-TieredCompilation -XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining"

num=0

while true; do

   let num++
   LOG=monte_carlo-${num}.log

   echo ${num}
   ${JDK}/bin/java \
      ${JVM_ARGS} \
      -jar SPECjvm2008.jar -ikv -coe -ict \
      ${Ben} | tee ${LOG}  &

   while true; do

      sleep 2s

      # detect inline
      result=`grep "@ 6   spec.benchmarks.scimark.utils.Random::<init> (53 bytes)   inline (hot)" ${LOG}`
      if [ "${result}" ];  then
         rm ${LOG}
         killall -9 java
         break
      fi

      # detect not inline
      result=`grep "Score on " ${LOG}`
      if [ "${result}" ];  then
         echo "Find a not inlined case"
         exit 0
      fi

   done

done
```
[2] http://hg.openjdk.java.net/jdk/jdk/file/0a2d73e02076/src/hotspot/share/opto/bytecodeInfo.cpp#l375

# The Graal Compiler

This goal has been achieved as evidenced by the inclusion of the Graal compiler in JDK 9 as the basis for jaotc and in JDK 10 as an experimental tier 4 just-in-time compiler.
To use the latter, simply add these VM options to the java command line:
```shell
-XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler
```

## Key Features of Graal

- Designed for speculative optimizations and deoptimization
- Designed for exact garbage collection
- Aggressive high-level optimizations
- Modular architecture
- Written in Java to lower the entry barrier

## The GraalVM Ecosystem

# Introduction

## Setting up Graal

### Get source code
```shell
git clone https://github.com/oracle/graal.git
git clone https://github.com/graalvm/mx.git
```
### External deps

- C1/C2 IR visualizer
```shell
# running with jdk8
wget https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/c1visualizer-1.7.zip
wget https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/idealgraphvisualizer/idealgraphvisualizer_latest.zip
```
You'd better delete ~/.c1visualizer if you come across some problem.
It seems that both visualizers are dependent on the version of netbeans.
I choose netbeans8.2.

- Eclipse
4.7.3a

- More deps
[Graal external deps](https://lafo.ssw.uni-linz.ac.at/pub/graal-external-deps/)

### Set env
```shell
MX_DIR="/home/fool/fujie/workspace/graal-project/mx"

export JAVA_HOME=/opt/jdk-11.0.1
export PATH=${JAVA_HOME}/bin:$PATH
export PATH=${MX_DIR}/:$PATH
```

## Running
```shell
Graal=/home/fool/fujie/workspace/graal-project/graal

java \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+EnableJVMCI \
  -XX:+UseJVMCICompiler \
  -XX:-TieredCompilation \
  -XX:+PrintCompilation \
  -XX:CompileOnly=Demo::workload \
  -XX:CompileCommand=quiet \
  --module-path=${Graal}/sdk/mxbuild/dists/jdk11/graal-sdk.jar:${Graal}/truffle/mxbuild/dists/jdk11/truffle-api.jar \
  --upgrade-module-path=${Graal}/compiler/mxbuild/dists/jdk11/graal.jar \
  Demo
```

```java
class Demo {
  public static void main(String[] args) {
    while (true) {
      workload(14, 2);
    }
  }

  private static int workload(int a, int b) {
    return a + b;
  }
}
```

## Modify the Graal
```java
--- a/compiler/src/org.graalvm.compiler.hotspot/src/org/graalvm/compiler/hotspot/HotSpotGraalCompiler.java
+++ b/compiler/src/org.graalvm.compiler.hotspot/src/org/graalvm/compiler/hotspot/HotSpotGraalCompiler.java
@@ -102,6 +102,7 @@ public class HotSpotGraalCompiler implements GraalJVMCICompiler {
 
     @Override
     public CompilationRequestResult compileMethod(CompilationRequest request) {
+        System.err.println("Going to compile " + request.getMethod().getName());
         return compileMethod(request, true, graalRuntime.getOptions());
     }
```

# JVMCI interface

## Design of the interfaces
- Abstract
```java
interface JVMCICompiler {
    byte[] compileMethod(byte[] bytecode);
}
```

- Refinement interfaces
```java
interface JVMCICompiler {
  void compileMethod(CompilationRequest request);
}

interface CompilationRequest {
    JavaMethod getMethod();
}

interface JavaMethod {
    byte[] getCode();
    int getMaxLocals();
    int getMaxStackSize();
    ProfilingInfo getProfilingInfo();
    ...
}

HotSpot.installCode(targetCode);
```

- Combined structure
```java
class GraalCompiler implements JVMCICompiler {
  void compileMethod(CompilationRequest request) {
    HotSpot.installCode(...);
  }
}
```

## Related Code

```java
public class HotSpotGraalCompiler implements GraalJVMCICompiler {
    // ...

    @Override
    public CompilationRequestResult compileMethod(CompilationRequest request) {
        return compileMethod(request, true, graalRuntime.getOptions());
    }

    // ...
}
```

# The Graal graph

It is a graph-based IR that models both control-flow and data-flow dependencies between nodes.
The new design simplifies the implementation of standard compiler optimizations as well as aggressive speculative optimizations.

Sometimes called a program-dependence-graph.
The data structure is sometimes called a sea-of-nodes, or a soup-of-nodes.

Run the JVM with -Dgraal.Dump. Then view the graph with igv.

# From Bytecode to machine code

- Bytecode in

```
--- a/compiler/src/org.graalvm.compiler.hotspot/src/org/graalvm/compiler/hotspot/HotSpotGraalCompiler.java
+++ b/compiler/src/org.graalvm.compiler.hotspot/src/org/graalvm/compiler/hotspot/HotSpotGraalCompiler.java
@@ -102,6 +102,8 @@ public class HotSpotGraalCompiler implements GraalJVMCICompiler {
 
     @Override
     public CompilationRequestResult compileMethod(CompilationRequest request) {
+        System.err.println(request.getMethod().getName() + " bytecode: "
+          + Arrays.toString(request.getMethod().getCode()));
         return compileMethod(request, true, graalRuntime.getOptions());
     }
 
```

## The bytecode parser and graph builder

This array of bytes is parsed as JVM bytecode into a Graal graph by the graph builder. This is a kind of abstract interpretation. 

## Emitting assembly

```java
@NodeInfo(shortName = "+")
public class AddNode extends BinaryArithmeticNode<Add> implements NarrowableArithmeticNode, BinaryCommutative<ValueNode> {
    // ...
    public void generate(NodeLIRBuilderTool nodeValueMap, ArithmeticLIRGeneratorTool gen) {
        Value op1 = nodeValueMap.operand(getX());
        assert op1 != null : getX() + ", this=" + this;
        Value op2 = nodeValueMap.operand(getY());
        if (shouldSwapInputs(nodeValueMap)) {
            Value tmp = op1;
            op1 = op2;
            op2 = tmp;
        }
        nodeValueMap.setResult(this, gen.emitAdd(op1, op2, false));
    }
    // ...
}
```

### Assembly out
```java
class HotSpotGraalCompiler implements JVMCICompiler {
  CompilationResult compileHelper(...) {
    ...
    System.err.println(method.getName() + " machine code: "
      + Arrays.toString(result.getTargetCode()));
    ...
  }
}
```

## Optimisations
An optimisation phase is just a method that has the opportunity to modify the graph. You write phases by implementing an interface.
```java
interface Phase {
  void run(Graph graph);
}
```

### Canonicalisation
```java
interface Node {
  Node canonical();
}
```

```java
class NegateNode implements Node {
  Node canonical() {
    if (value instanceof NegateNode) {
      return ((NegateNode) value).getValue();
    } else {
      return this;
    }
  }
}
```

### Global value numbering
Global value numbering is a technique to remove code that is redundant because it appears more than once.
```java
public class CanonicalizerPhase extends BasePhase<PhaseContext> {
        // ...
        public boolean tryGlobalValueNumbering(Node node, NodeClass<?> nodeClass) {
            if (nodeClass.valueNumberable()) {
                Node newNode = node.graph().findDuplicate(node);
                if (newNode != null) {
                    assert !(node instanceof FixedNode || newNode instanceof FixedNode);
                    node.replaceAtUsagesAndDelete(newNode);
                    COUNTER_GLOBAL_VALUE_NUMBERING_HITS.increment(debug);
                    debug.log("GVN applied and new node is %1s", newNode);
                    return true;
                }
            }
            return false;
        }
        // ...
}
```

### Lock coarsening
```java
public class LockEliminationPhase extends Phase {

    @Override
    protected void run(StructuredGraph graph) {
        for (MonitorExitNode monitorExitNode : graph.getNodes(MonitorExitNode.TYPE)) {
            FixedNode next = monitorExitNode.next();
            if ((next instanceof MonitorEnterNode || next instanceof RawMonitorEnterNode)) {
                // should never happen, osr monitor enters are always direct successors of the graph
                // start
                assert !(next instanceof OSRMonitorEnterNode);
                AccessMonitorNode monitorEnterNode = (AccessMonitorNode) next;
                if (isCompatibleLock(monitorEnterNode, monitorExitNode)) {
                    /*
                     * We've coarsened the lock so use the same monitor id for the whole region,
                     * otherwise the monitor operations appear to be unrelated.
                     */
                    MonitorIdNode enterId = monitorEnterNode.getMonitorId();
                    MonitorIdNode exitId = monitorExitNode.getMonitorId();
                    if (enterId != exitId) {
                        enterId.replaceAndDelete(exitId);
                    }
                    GraphUtil.removeFixedWithUnusedInputs(monitorEnterNode);
                    GraphUtil.removeFixedWithUnusedInputs(monitorExitNode);
                }
            }
        }
    }
    // ...
}
```

### Register allocation

Deciding which registers to use for each edge is a problem called register allocation. Graal uses similar register allocation algorithms to other JIT-compilers (it’s the linear scan algorithm).

### Scheduling
We have a graph of nodes without any clear order to run them in, but the processor needs a linear sequence of instructions in a definite order.

This problem is called graph scheduling. The scheduler needs to decide what order to run all the nodes in. It decides when to run code based on the constraints that all values need to have been computed by the time that they’re needed for a computation. You could produce a schedule which just works, but you can improve the performance of code for example by not running a computation until the value is definitely needed.

You can get even more sophisticated by applying your understanding of what resources your processor has and giving it work in an order which means those resources are used as efficiently as possible.


# Reference

[Understanding How Graal Works - a Java JIT Compiler Written in Java](https://chrisseaton.com/truffleruby/jokerconf17/) by Chris Seaton, 3 November 2017

[JVM Languages Reference for Graal](https://www.graalvm.org/docs/reference-manual/languages/jvm/)

[Call for Discussion: New Project: Metropolis](http://cr.openjdk.java.net/~jrose/metropolis/Metropolis-Proposal.html)

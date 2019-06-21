# Building & Running 

```shell
# Building the suite
tools/sbt/bin/sbt assembly

# Running 
# java -jar '<renaissance-home>/target/renaissance-0.9.0.jar' <benchmarks>
```

# List of Benchmarks

- group list 
```shell
$ java -jar renaissance-0.9.0.jar  --group-list
actors
apache-spark
database
dummy
jdk-concurrent
jdk-streams
neo4j
rx
scala-dotty
scala-stdlib
scala-stm
twitter-finagle
```

- raw list
```shell
$ java -jar renaissance-0.9.0.jar  --raw-list
akka-uct
als
chi-square
db-shootout
dec-tree
dotty
dummy
finagle-chirper
finagle-http
fj-kmeans
future-genetic
gauss-mix
log-regression
mnemonics
movie-lens
naive-bayes
neo4j-analytics
page-rank
par-mnemonics
philosophers
reactors
rx-scrabble
scala-kmeans
scala-stm-bench7
scrabble
```

# Help Info

```shell
$ java -jar renaissance-0.9.0.jar  --help
Renaissance Benchmark Suite, version 0.9.0
Usage: renaissance [options] [benchmark-specification]

  -h, --help               Prints this usage text.
  -r, --repetitions <value>
                           Number of repetitions used with the fixed-iterations policy.
  -w, --warmup-seconds <value>
                           Number of warmup seconds, when using time-based policies.
  -t, --run-seconds <value>
                           Number of seconds to run after the warmup, when using time-based policies.
  --policy <value>         Execution policy, one of: fixed-warmup, fixed-iterations
  --plugins <value>        Comma-separated list of class names of plugin implementations.
  --csv <value>            Output results to CSV file.
  --json <value>           Output results to JSON file.
  --readme                 Regenerates the README file, and does not run anything.
  --functional-test        Reduce iteration times significantly for testing purposes.
  --list                   Print list of benchmarks with their description.
  --raw-list               Print list of benchmarks (each benchmark name on separate line).
  --group-list             Print list of benchmark groups (each group name on separate line).
  benchmark-specification  Comma-separated list of benchmarks (or groups) that must be executed (or all).
```

# Configuration

## --functional-test

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  --functional-test          \
  -r 1                       \
  all
```

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  --functional-test          \
  -r 3                       \
  dotty,scala-kmeans
```

## startup

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  -r 1                       \
  all
```

## --fixed-iterations

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-iterations  \
  -r 4                       \
  all
```

## --fixed-warmup

```shell
${JDK}/bin/java \
  -jar renaissance-0.9.0.jar \
  --policy fixed-warmup      \
  -w 120                     \
  -t 240                     \
  all
```

# Ref

[Renaissance Benchmark Suite](https://github.com/renaissance-benchmarks/renaissance)

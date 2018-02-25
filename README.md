# sparkSimpleProjectTemplate
A [Giter8][g8] template for multi-project Scala Spark Projects.

## What this gives you

A sbt muti project spark template including tests.

To encourage good software development practice, this starts with a project at 100% code coverage (e.g. one test :p), while its expected for this to decrease, we hope you use the provided [spark-testing-base][stb] library or similar option.

## Creating a new project from this template

Have g8 installed? You can run it with:

```bash
g8 geoHeil/sparkMultiProjectTemplate.g8 --name=projectname --organization=com.my.org --sparkVersion=2.2.1
```

Using sbt (0.13.13+) just do
```bash
sbt new geoHeil/sparkMultiProjectTemplate.g8

```bash
sbt new geoHeil/sparkMultiProjectTemplate.g8
```

## Executing the created project

First go to the project you created: 

```bash
cd projectname
```

You can test locally the example spark job included in this template directly from sbt: 

```bash 
sbt "run inputFile.txt outputFile.txt"
```

then choose `CountingLocalApp` when prompted.

You can also assemble a fat jar (see [sbt-assembly](https://github.com/sbt/sbt-assembly) for configuration details): 

```bash
sbt assembly
```

then [submit as usual](https://spark.apache.org/docs/latest/submitting-applications.html) to your spark cluster :

```bash
/path/to/spark-home/bin/spark-submit \
  --class <package-name>.CountingApp \
  --name the_awesome_app \
  --master <master url> \
  ./target/scala-2.11/<jar name> \
  <input file> <output file>
```


## License

This project is available under your choice of Apache 2 or CC0 1.0.
See <https://www.apache.org/licenses/LICENSE-2.0> or <https://creativecommons.org/publicdomain/zero/1.0/> respectively.
This template is distributed without any warranty and based on
- https://github.com/holdenk/sparkProjectTemplate.g8
- https://github.com/foundweekends/giter8.g8
[g8]: http://www.foundweekends.org/giter8/
[stb]: https://github.com/holdenk/spark-testing-base
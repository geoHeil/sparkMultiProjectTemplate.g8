# $library$
Short description of your project

## developing the project
TODO
- development
```bash
sbt console
```

- compilation
```bash
sbt compile
```
- testing
```bash
sbt test
```
- creating the final artifact
```bash
sbt assembly
```
which will output fat jars. These jars need to be referenced when running the project.

to release: `sbt release` to release a new version to the artifact store.

## deployment process

- execute `sbt release` to release a new version which will also verify the tests, also specify the version number
- artifact will be published to company artifactory at (TODO URL)
- clone git (runner scripts) to cluster
- pull artifact from artifact store
- run the job

## upgrading the project

some utility methods exist helping upgrading this project
```bash
sbt dependencyUpdates
```
will output all outdated dependencies
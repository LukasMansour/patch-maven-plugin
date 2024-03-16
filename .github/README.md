# Maven Patch Plugin

[![license](https://img.shields.io/github/license/LukasMansour/patch-maven-plugin.svg?style=for-the-badge)](../LICENSE)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/LukasMansour/patch-maven-plugin?style=for-the-badge)](https://github.com/LukasMansour/patch-maven-plugin/releases)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=for-the-badge)](https://github.com/RichardLitt/standard-readme)

This new Maven Patch Plugin is a simple to use plugin for maven, that allows you to use patch files
(e.g. created by git) in your building process. Importantly, this plugin does not use GNU Patch and
therefore it can be used on Windows machines and does not require any container configurations in
your builds.

This plugin was created by Lukas Mansour in 2024 primarily for
the [dCache](https://github.com/dCache/dcache) project. It primarily uses a java native patching
library called [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils).

## Table of Contents

- [Install](#install)
- [Usage](#usage)
- [Maintainers](#maintainers)
- [Acknowledgements](#acknowledgements)
- [Contributing](#contributing)
- [License](#license)

## Install

### Maven

This plugin is only available for Maven and can be installed with the following plugin snippet:

```xml

<plugin>
  <groupId>io.github.lukasmansour</groupId>
  <artifactId>patch-maven-plugin</artifactId>
  <version>1.1.0</version>
  <configuration>
    <!-- Root directory where the patches are applied.-->
    <targetDirectory>${project.basedir}</targetDirectory>
    <!-- Directory in which the patches themselves are -->
    <patchDirectory>${project.basedir}\src\main\patches</patchDirectory>
    <!-- Should the plugin fail the build if a patch is not applied successfully? -->
    <failOnFailedPatch>false</failOnFailedPatch>
  </configuration>

  <!-- And add the execution to the building phase-->
  <executions>
    <execution>
      <goals>
        <goal>apply</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

## Usage

In the plugin you can configure a ```targetDirectory``` and a ```patchDirectory```.
the ```targetDirectory``` is the root in which patches will be applied. The ```patchDirectory``` is
the location of the patches. 

Furthermore you can set the ```failOnFailedPatch``` setting, which will fail the build if a patch
is not applied successfully. Here it may be necessary to add an instance of the ```maven-clean-plugin```
to your execution lifestyle. This is because usually an already patched file cannot be successfully 
patched (by the same patch) again.

There is also an example in the repository [here](../test-project).

## Maintainers

[@Lukas Mansour](https://www.github.com/LukasMansour/)

## Acknowledgements

[Community project for Java Diff Utils](https://github.com/java-diff-utils/java-diff-utils).

## Contributing

See [the contributing file](CONTRIBUTING.md)!

## License

[Apache 2.0 © Lukas Mansour ](../LICENSE)
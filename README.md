# JMarkPad

JMarkPad is a minimalistic text editor that shows a real-time preview of how your markdown text would look like on your blog, github page or your own website.

![](otherResources/readmeGif.gif)

## General information 

### Installing

To use JMarkPad you only need to download and double click on the executable:

* Download "JMarkPad.jar" if you already have Java 8 installed.
* Download "JMarkPad.zip" if you don't have Java 8 installed. 

[Download the latest version here](https://github.com/mayuso/JMarkPad/releases)

### Roadmap

We are constantly trying to improve JMarkPad, you can see the known bugs and planned enhancements on our [issues list](https://github.com/mayuso/JMarkPad/issues).

### Other useful information

[![Build Status](https://travis-ci.org/mayuso/JMarkPad.svg?branch=develop)](https://travis-ci.org/mayuso/JMarkPad)

## Development

### How to build

* Download and install [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (Latest version tested: 8u181).
* Download and install [Gradle](https://gradle.org/releases/) (Latest version tested: 4.9).

To create a java executable (`.jar`) run `gradle build` -> Go to build/libs to find `JMarkPad.jar`.

To create a windows executable (`.exe`) run `gradle jfxNative` -> Go to build/jfx/native to find the JMarkPad folder, this folder will include `JMarkPad.exe` as well as a Java Runtime Environment to be able to run JMarkPad without Java installed.

### Versioning

We use [Semantic Versioning 2.0.0](http://semver.org/) for versioning. For the versions available, see the [releases on this repository](https://github.com/mayuso/JMarkPad/releases). 

### Authors

* **Mikel Ayuso** - *Initial work* - [Mayuso](https://github.com/mayuso)

See also the list of [contributors](https://github.com/mayuso/JMarkPad/graphs/contributors) who participated in this project.

### License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

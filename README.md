# JMarkPad

JMarkPad is a minimalistic text editor that shows a real-time preview of how your markdown text would look like on your blog, github page or your own website.

![](otherResources/readmeGif.gif)

## General information 

### Installing

To use JMarkPad you only need to download and double click on the executable:

* Download "JMarkPad_win.jar" or "JMarkPad_linux.jar" if you already have Java installed.
* Download "JMarkPad.zip" if you don't have Java installed. 

[Download the latest version here](https://github.com/mayuso/JMarkPad/releases)

### Roadmap

We are constantly trying to improve JMarkPad, you can see the known bugs and planned enhancements on our [issues list](https://github.com/mayuso/JMarkPad/issues).

## Development

### How to build

Latest versions tested:

1. JDK 24
2. Apache Maven 3.9.11

* To run JMarkPad run `mvn clean javafx:run`
* To create an executable package run `mvn clean compile package`

    The file will be located in `target/JMarkPad.jar`


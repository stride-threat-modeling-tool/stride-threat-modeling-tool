# Setup with  IntelliJ
1. Clone the repository with `git clone`.

2. For the GitHub-based maven repository `https://maven.pkg.github.com/eckig/graph-editor/` pom.xml, do the following:

To be able to download the "Graph Editor" dependency via maven, you need to add a new server in your settings.xml file in your `${user.home}/.m2/` folder.
If you don't already have a settings.xml file create a new one in your `${user.home}/.m2/` folder with the following structure:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository/>
    <interactiveMode/>
    <offline/>
    <pluginGroups/>
    <servers>
        <server>
            <id>github</id>
            <username>your_github_username</username>
            <password>your_github_token</password>
        </server>
    </servers>
    <mirrors/>
    <proxies/>
    <profiles/>
    <activeProfiles/>
</settings>
```
Source: https://github.com/eckig/graph-editor/issues/41#issuecomment-706487899

If you don't do this, maven will not be able to download the graph-editor dependencies  and instead return a "401 - Unauthorized" error.

3. Import the project with IntelliJ IDEA or a different IDE.

## How to run
1. View -> Tool Windows -> Maven
2. Lifecycle -> clean
3. Plugins -> javafx -> javafx:run

## How to package
- Requires JDK 16 and Maven
- Refer to the `MAVEN JPACKAGE PLUGIN` extract further down the README.
  (README excerpt from https://github.com/wiverson/maven-jpackage-template)

### Packaging under Windows:
- run lifecycle install, navigate to target folder and start the msi.
- free [WiX Toolset](https://wixtoolset.org/) is required.

### Packaging under Linux:
- requires maven >=3.8.1 in our experience

# Known Issues

## Debian
-  E: Package 'libffi7' has no installation candidate
    -  https://packages.debian.org/sid/amd64/libffi7/download download from here
    -  sudo dpkg -i <deb file>

- No protocol or cannot access Display
    - do not launch as root



# README OF MAVEN JPACKAGE PLUGIN


# JavaFX + Maven = Native Desktop Apps

[JavaFX](https://openjfx.io) + [jpackage](https://docs.oracle.com/en/java/javase/15/docs/specs/man/jpackage.html) +
[Maven](http://maven.apache.org) template project for generating native desktop applications.

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/maven-jpackage-template/community)

# Goal

1. Build nice, small cross-platform [JavaFX](https://openjfx.io)-based desktop apps with native installers
    - Apx 30-40mb .dmg, .msi and .deb installers - check out the example builds in
      [releases](https://github.com/wiverson/maven-jpackage-template/releases).
2. Just use Maven - no shell scripts required.
    - Use standard Maven dependency system to manage dependencies
3. Generate [macOS (.dmg), Windows (.msi) and Unix (e.g. deb/rpm)](https://github.com/wiverson/maven-jpackage-template/releases)
   installers/packages automatically
   with [GitHub Actions](https://github.com/wiverson/maven-jpackage-template/tree/main/.github/workflows)

## Overview

This template uses a [Maven plugin](https://github.com/wiverson/jtoolprovider-plugin) to generate a custom JVM and
installer package for a JavaFX application.

The basic requirements are just Java 16 and Maven. [Java 15 will work](docs/java-15-jpackage.md), although it requires a
bit of setup.

- On macOS XCode is required.
- On Windows the free [WiX Toolset](https://wixtoolset.org/) is required.

The project includes [GitHub Actions](https://github.com/wiverson/maven-jpackage-template/tree/main/.github/workflows)
which automatically generate macOS, Windows, and Linux installers.

The generated installers come in at around 30-40mb. The example source in the project includes demonstrations of several
native desktop features - for example, drag-and-drop from the Finder/Explorer, as well as a few macOS Dock integration
examples. Removing the code and the demonstration dependendencies gets a "Hello World" build size closer to 30mb than
40mb.

## Key Features

Here are few cool things in this template:

- Only uses Java and Maven. No shell scripts required.
- Includes sample [GitHub Actions](https://github.com/wiverson/maven-jpackage-template/tree/main/.github/workflows) to
  build macOS, Windows and Linux installers
- Demonstrates setting the application icon
- Builds a .dmg on macOS, .msi on Windows, and .deb on Linux
- Bundles the JavaFX SDK & modules to simplify getting started.
- Template includes several examples of JavaFX / native desktop integration
    - Drag & drop with Finder / Explorer
    - Change the Dock icon dynamically on macOS
    - Menu on the top for macOS, in the window itself on Windows
    - Request user attention (bouncing dock icon) on macOS

Once you get started, you might find these lists of tutorials, tools, libraries for
[JavaFX](https://gist.github.com/wiverson/6c7f49819016cece906f0e8cea195ea2)
and general [Java desktop integration](https://gist.github.com/wiverson/e9dfd73ca9a9a222b2d0a3d68ae3f129) helpful.

# Usage

Once everything is installed (see below) it's really easy to use:

To generate an installer, just run...

`mvn clean install`

To do everything up until the actual installer generation (including generating the custom JVM)...

`mvn clean package`

# Installation

1. Install [OpenJDK Java 16](https://adoptopenjdk.net/) or
   [Oracle Java 16](https://www.oracle.com/java/technologies/javase-downloads.html).
    - Verify by opening a fresh Terminal/Command Prompt and typing `java --version`.
2. Install [Apache Maven 3.6.3](http://maven.apache.org/install.html) or later and make sure it's on your path.
    - Verify this by opening a fresh Terminal/Command Prompt and typing `mvn --version`.
3. macOS: verify XCode is installed and needed agreements accepted.
    - Launch XCode and accept the license, or verify in Terminal with the command `sudo xcodebuild -license`.
5. Windows: install [Wix 3 binaries](https://github.com/wixtoolset/wix3/releases/).
    - Installing Wix via the installer should be sufficient for jpackage to find it.
3. Clone/download this project.
6. Final step: run `mvn clean install` from the root of the project to generate the `target\TestApp.dmg`
   or `target\TestApp.msi` (installer).
    - Note that the actual generated installer will include a version number in the file name
    - For reference, here is a complete run log for [a successful run](docs/sample-run.md).

Because these builds use stripped down JVM images, the
[generated installers are in the 30-40mb range](https://github.com/wiverson/maven-jpackage-template/releases).

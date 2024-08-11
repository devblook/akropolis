![Akropolis banner](https://user-images.githubusercontent.com/56933557/188349705-b1f1eb56-8e4b-42d2-b99d-f21552ec84c2.png)

Akropolis is a modern Minecraft hub server solution that is based on DeluxeHub by ItsLewizzz.
It contains almost all of its features and configuration files are almost the same, so you can just
drop your configuration into the plugin's directory, make a few modifications and use it.

The main difference between Akropolis and DeluxeHub is that Akropolis uses more modern technologies, like MiniMessage,
the Paper API and updated Java versions. While this give us some performance and usability benefits, it also means
that we won't be giving support to older versions of Minecraft and other Minecraft server software that isn't derivated
from Paper, which is not the case of DeluxeHub.
Simply use what you feel meets your needs.

## How to

### Install

To use this plugin just a grab a binary from the [releases page](https://github.com/devblook/akropolis/releases)
or [compile it](#compile) yourself and drop it into your `plugins/` directory. Take in mind that you will need to be
running Paper 1.21+ so Akropolis can run properly. You can download Paper from [here](https://papermc.io/downloads).

### Compile

Compiling Akropolis is pretty simple, just one command, and you're ready to go:

**Linux (and other UNIX derivatives):**

```bash
./gradlew shadowJar
```

**Windows:**

```batch
gradlew.bat shadowJar
```

Then you will find the binary under the `build/libs/` directory.

### Report bugs or request features

Reporting a bug or requesting a feature can be useful for further development of the plugin. To do that you just need
to fill one of the issue templates we made for you:
[Click here to report a bug](https://github.com/devblook/akropolis/issues/new?assignees=zetastormy&labels=bug&template=bug_report.yml&title=A+brief+description+of+your+report)
or [click here to request a feature](https://github.com/devblook/akropolis/issues/new?assignees=zetastormy&labels=enhancement&template=feature_request.yml&title=A+brief+description+of+your+request).

### Contribute

At the moment we don't have a lot of requirements to contribute, just make sure to clarify
the features or fixes that you introduce in your pull request and try to follow the
[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for
details.

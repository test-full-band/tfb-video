# testing-video

The project is aimed to create a set of videos usable for testing
media playback for correctness and evaluating quality of decoding
and/or processing video.
There are also files useful for calibrating TV sets.

For user documentations and downloading compiled test video files
see our [Wiki](https://github.com/testing-av/testing-video/wiki).

## Development environment

The test generator is written in Java 10 and you need Java Development Kit
(JDK) installed to build the project. You can get Java for your platform
from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk10-downloads-4416644.html).

Also you will need Apache Maven 3.5+ to build the project. x264, x265,
MP4Box and ffmpeg executables need to be installed and available in the
system PATH to encode videos.

We use Eclipse as an IDE. If you are using some other IDE and want to
contribute you are welcome to do it but the job to get preferences for
code style, formatting etc. is on you. The checked in eclipse preference
is the single source of truth about code style and formatting and you
have to follow it when sending pull requests.

### Windows

Required executables can be installed from following sources: 

 * [x264](http://download.videolan.org/pub/x264/binaries/)
 * [x265 multilib](http://msystem.waw.pl/x265/)
 * [MP4Box](https://www.videohelp.com/software/MP4Box/old-versions) -
   NB! Use gpac-0.7.0-rev27 version or newer.
 * [ffmpeg](http://ffmpeg.zeranoe.com/builds/)

### macOS

Required executables can be installed with [Homebrew](https://brew.sh/): 

```sh
brew install maven
brew install x264
brew install mp4box
brew install ffmpeg
```

Homebrew doesn't allow you to install _multilib_ x265 executable that
supports multiple bitdepths so you have to compile it yourself so all HEVC
bitdepths (8, 10, 12 bit) are supported by single executable. Build it with
(or see [x265 Wiki](https://bitbucket.org/multicoreware/x265/wiki/Home) for
details):

```sh
brew install hg
brew install cmake
hg clone https://bitbucket.org/multicoreware/x265
cd x265/build/linux
./multilib.sh
cd 8bit/
make install
```

If you want to install multiple bitdepth versions of x264 read
[this](https://github.com/Homebrew/legacy-homebrew/issues/48902).
Although we do not plan to use anything but 8 bit with x264.

You need to start Eclipse from terminal window for it to get the same
PATH environment variable. If started from Dock the PATH will be from
the Dock process and will not contain /usr/local/bin that contains
x264, x265, MP4Box and ffmpeg executables. You can do it with the command:

```sh
open -a Eclipse
```

## Building the project and encoding test videos

We use _JUnit_ together with _maven-failsafe-plugin_ as a framework to
generate test videos.

To build all test videos in the project run:

```sh
mvn clean verify
```

# testing-video

The project is aimed to create a set of videos usable for testing
media playback for correctness and evaluating quality of decoding
and/or processing video.
There are also files useful for calibrating TV sets.

For user documentations and downloading compiled test video files
see our [Wiki](https://github.com/testing-av/testing-video/wiki)

## Development environment

You need x264, x265 and ffmpeg executables installed and available in
the PATH to encode videos.

### macOS

Required executables can be installed with [Homebrew](https://brew.sh/): 

```sh
brew install x264
brew install x265 --with-16-bit
brew install ffmpeg --with-x265
```

The issue with the above method is that the x265 executable can either
encode 8bit or 10bit but both cannot be supported and 12 bit encode is
also cannot be supported by this method of installation. Compiling from
the source is the only method for _multilib_ support in a single
executable. Build it with (or see
[x265 Wiki](https://bitbucket.org/multicoreware/x265/wiki/Home)
for details):

```sh
brew install hg
brew install cmake
hg clone https://bitbucket.org/multicoreware/x265
cd x265/build/xcode
./multilib.sh
cd 8bit/
make install
```


If you want to install multiple versions read [this](https://github.com/Homebrew/legacy-homebrew/issues/48902)

You need to start Eclipse from terminal window for it to get the same
PATH environment variable. If started from Dock the PATH will be from
the Dock process and will not contain /usr/local/bin that contains
x265 and ffmpeg executables. You can do it with the command:

```sh
open -a Eclipse
```

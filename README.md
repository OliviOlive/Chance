# Chance

Application-Specific Video Generator

## Usage

Script files are JSON files with a currently undocumented syntax. Refer to `bbp_script.json` for example, or look in the source code at the `ScriptReader` class.

It is highly possible that documentation will never be published for this software. Therefore, at this time, the best course of action would be to either find another piece of software or read into the source code, as aforementioned.

## Limitations

- All audio files must have the same sample rate (this program features an 'in-house' audio mixer, which is very primitive).
- All audio files must be supported by the standard javax.sound library, through built-in support or otherwise, plugins.

## Dependencies

#### To be found yourself:
- avconv (like ffmpeg) if you want the video to automatically be generated

#### Included in this repository:
- WavFiles from labbookpages.co.uk (included within).
- Java JSON library (included within).

## Recommended

If you have a ramdisk facility (/dev/shm on Linux, maybe others too), I advise using a subdirectory in one for the export directory to prevent slow disk IO or wearing away flash cells...

You can change the output framerate using a VM argument: -Dolibbp.framerate=60

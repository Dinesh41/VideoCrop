# VideoCrop
This project contains example code used to crop a video file using FFmpeg and FFprobe library in Android.

To use this code you should follow the below steps

1. Add dependencies for FFmpeg

    implementation 'nl.bravobit:android-ffmpeg:1.1.5'

2. Before you start cropping, you should initialize FFmpeg and FFprobe using loadFFMpegBinary() and loadFFprobeBinary() methods

3. Then you can execute commands whatever you want

In this example I used this library to cut last 3sec from the video file


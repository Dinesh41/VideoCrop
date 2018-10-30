
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import nl.bravobit.ffmpeg.FFprobe;

/**
 * Created by Dinesh on 25-10-2018.
 */

public class VideoHandler
{
    private static final String TAG = "VideoHandler";
    private static FFmpeg ffmpeg;
    private static FFprobe ffprobe;

    //Used to initialize FFMpeg
    public static void loadFFMpegBinary(Context context)
    {
        if (FFmpeg.getInstance(context).isSupported())
        {
            // ffmpeg is supported
            Log.i(TAG, "ffmpeg supported");
            ffmpeg = FFmpeg.getInstance(context);
        }
        else
        {
            // ffmpeg is not supported
            Log.i(TAG, "ffmpeg not supported");
        }
    }
    //Used to initialize FFprobe
    public static void loadFFprobeBinary(Context context)
    {
        if (FFprobe.getInstance(context).isSupported())
        {
            // ffmpeg is supported
            Log.i(TAG, "ffprobe supported");
            ffprobe = FFprobe.getInstance(context);
        }
        else
        {
            // ffmpeg is not supported
            Log.i(TAG, "ffprobe not supported");
        }
    }

    //use to execute get duration of video using ffprobe commands
    public static void executeGetVideoDurationCommand(final File file)
    {
    
        final String[] command={"-i",file.getAbsolutePath(),"-hide_banner"};
        ffprobe.execute(command, new ExecuteBinaryResponseHandler()
        {
            @Override
            public void onFailure(String s)
            {
                Log.d(TAG, "FAILED with output : " + s);
            }

            @Override
            public void onSuccess(String s)
            {
                Log.d(TAG, "SUCCESS with output : " + s);
                String[] dur=parseDuration(s).split(" ");
                int totalMilliseconds=getMilliSeconds(dur[1]);
                executeTrimCommand(file,0,totalMilliseconds-3000);
            }

            @Override
            public void onProgress(String s)
            {
                Log.d(TAG, "Started command");
                Log.d(TAG, "progress : " + s);
            }

            @Override
            public void onStart()
            {
                Log.d(TAG, "Started command");
            }

            @Override
            public void onFinish()
            {
                Log.d(TAG, "Finished command");
            }
        });
    }

    //Used to execute TrimCommands using FFMpeg
    public static void executeTrimCommand(File file, int startMs, int endMs)
    {
        try
        {
            File src = file;
            File dest = file;
            /*if (dest.exists())
            {
                dest.delete();
            }*/
            Log.d(TAG, "startTrim: src: " + src.getAbsolutePath());
            Log.d(TAG, "startTrim: dest: " + dest.getAbsolutePath());
            Log.d(TAG, "startTrim: startMs: " + startMs);
            Log.d(TAG, "startTrim: endMs: " + endMs);
            final String[] command=new String[]{"-i",src.getAbsolutePath(),"-ss",(startMs / 1000)+"","-to",
                    (endMs / 1000)+"", "-strict","-2","-async","1",dest.getAbsolutePath()+"FF.mp4"};
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler()
            {
                @Override
                public void onFailure(String s)
                {
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s)
                {
                    Log.d(TAG, "SUCCESS with output : " + s);

                }

                @Override
                public void onProgress(String s)
                {
                    Log.d(TAG, "Started command");
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart()
                {
                    Log.d(TAG, "Started command");
                }

                @Override
                public void onFinish()
                {
                    Log.d(TAG, "Finished command ");
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //Used to parse the information of video and to get only the duration of the video
    private static String parseDuration(String s)
    {
        Pattern pattern = Pattern.compile("Duration:\\s\\d\\d:\\d\\d:\\d\\d", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);
        // using Matcher find(), group(), start() and end() methods
        if(matcher.find()) {
            System.out.println("Found the text \"" + matcher.group()
                    + "\" starting at " + matcher.start()
                    + " index and ending at index " + matcher.end());
            return matcher.group();
        }
        return null;
    }
    //Used to convert HH:MM:SS to milliseconds
    private static int getMilliSeconds(String duration)
    {
        System.out.println(duration);
        String[] hhmmss=duration.split(":");
        int hh=Integer.parseInt(hhmmss[0]);
        int mm=Integer.parseInt(hhmmss[1]);
        int ss=Integer.parseInt(hhmmss[2]);
        int milliseconds=(hh*60*60*1000)+(mm*60*1000)+ss*1000;
        System.out.println(milliseconds);
        return milliseconds;
    }
}

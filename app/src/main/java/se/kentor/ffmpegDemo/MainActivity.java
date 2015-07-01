package se.kentor.ffmpegDemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;

/**
 * Created by Ilya Klyukin.
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "se.kentor.ffmpegDemo";
    private static final String FRAME_DUMP_FOLDER_PATH = Environment.getExternalStorageDirectory()
            + File.separator + "ffmpegDemo";

    private static final String videoFileName = "1.mp4";

    private SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        //create directory
        File dumpFolder = new File(FRAME_DUMP_FOLDER_PATH);
        if (!dumpFolder.exists()) {
            dumpFolder.mkdirs();
        }
        //copy input video file from assets folder to directory
        Utils.copyAssets(this, videoFileName, FRAME_DUMP_FOLDER_PATH);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mSurfaceView.getHolder().addCallback(this);

        stopPlayer(mSurfaceView);
    }

    public void startPlayer(View view) {
        disableVideoControls(true);

        naInit(FRAME_DUMP_FOLDER_PATH + File.separator + videoFileName);

        int[] resolution = naGetVideoRes();
        naSetup(resolution[0], resolution[1]);

        //start playing
        naPlay();
    }

    public void stopPlayer(View view) {
        disableVideoControls(false);
        naStop();
    }

    private void disableVideoControls(boolean isStarted) {
        Button btnStart = (Button) findViewById(R.id.start_button);
        Button btnStop = (Button) findViewById(R.id.stop_button);
        btnStart.setEnabled(!isStarted);
        btnStop.setEnabled(isStarted);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        naStop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(TAG, "Surface changed: " + width + ":" + height);
        naSetSurface(holder.getSurface());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        naSetSurface(null);
    }

    //native methods section
    private static native int naInit(String pFileName);

    private static native int[] naGetVideoRes();

    private static native void naSetSurface(Surface pSurface);

    private static native int naSetup(int pWidth, int pHeight);

    private static native void naPlay();

    private static native void naStop();

    //import native libraries
    static {
        System.loadLibrary("avutil-54");
        //need for android versions < 17 SDK
        System.loadLibrary("swresample-1");

        System.loadLibrary("avcodec-56");
        System.loadLibrary("avformat-56");
        System.loadLibrary("swscale-3");
        System.loadLibrary("ffmpeg");
    }
}

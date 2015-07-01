package se.kentor.ffmpegDemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ilya Klyukin.
 */
public class Utils {
    private static final String TAG = "se.kentor.ffmpegDemo";

    public static void copyAssets(Context pContext, String pAssetFilePath, String pDestDirPath) {
        AssetManager assetManager = pContext.getAssets();
        try {
            InputStream in = assetManager.open(pAssetFilePath);
            File outFile = new File(pDestDirPath, pAssetFilePath);
            OutputStream out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset file: " + pAssetFilePath, e);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024 * 16];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}

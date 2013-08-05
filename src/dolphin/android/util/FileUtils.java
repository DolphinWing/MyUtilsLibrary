/**
 * //[Android] Copy File
 * //http://fecbob.pixnet.net/blog/post/36060051-%5Bandroid%5D-copy-file
 */
package dolphin.android.util;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    public static boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    // WARNING ! Inefficient if source and dest are on the same filesystem !
    public static boolean moveFile(File source, File dest) {
        return copyFile(source, dest) && source.delete();
    }

    // Returns true if the sdcard is mounted rw
    public static boolean isSDMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * write string content to a file
     * @param dst
     * @param content
     * @return
     */
    public static boolean writeStringToFile(File dst, String content) {
        try {//http://stackoverflow.com/a/1053474
            BufferedWriter writer = new BufferedWriter(new FileWriter(dst));
            writer.write(content, 0, content.length());
            writer.close();
            writer = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * read the file content to string
     * @param src
     * @return
     */
    public static String readFileToString(File src) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader sr = new FileReader(src);
            int len = 0;
            while (true) {//read from buffer
                char[] buffer = new char[1024];
                len = sr.read(buffer);//, size, 512);
                //Log.d(TAG, String.format("%d", len));
                if (len > 0) {
                    sb.append(buffer);
                } else {
                    break;
                }
            }
            //Log.i(TAG, String.format("  length = %d", sb.length()));
            sr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }
}

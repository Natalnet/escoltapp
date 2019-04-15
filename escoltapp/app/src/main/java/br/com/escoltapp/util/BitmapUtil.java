package br.com.escoltapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class BitmapUtil {
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void downloadImage(@NonNull String url, @NonNull OnFinishDownloadListener onFinishDownloadListener) {
        DownloadImageTask downloadImageTask = new DownloadImageTask(onFinishDownloadListener);
        downloadImageTask.execute(url);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        OnFinishDownloadListener onFinishDownloadListener;

        DownloadImageTask(OnFinishDownloadListener onFinishDownloadListener) {
            this.onFinishDownloadListener = onFinishDownloadListener;
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            onFinishDownloadListener.onFinishDownload(bitmap);
        }
    }

    public interface OnFinishDownloadListener {
        void onFinishDownload(Bitmap bitmap);
    }
}

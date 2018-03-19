package com.glennio.storyboard;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public class SimpleWebImageApplier implements StoryboardImageApplier {

    public interface Callback {
        String getImageUriForProgress(int progress);
    }

    private Drawable placeholderDrawable;
    private int pendingProgress;
    private Worker worker;
    private WeakReference<ImageView> imageViewWeakReference;
    private WeakReference<Callback> callbackWeakReference;
    private Handler handler;
    private Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;

    public SimpleWebImageApplier(Callback callback) {
        this.handler = new Handler(Looper.getMainLooper());
        this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
    }


    @Override
    public void applyImageForProgress(ImageView imageView, int progress) {
        this.imageViewWeakReference = imageView == null ? null : new WeakReference<>(imageView);
        if (worker != null && worker.isRunning())
            pendingProgress = progress;
        else {
            if (placeholderDrawable != null && imageView != null)
                imageView.setImageDrawable(placeholderDrawable);
            this.pendingProgress = -1;
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            worker = new Worker(bitmapConfig, workerCallback, callback == null ? null : callback.getImageUriForProgress(progress), imageView);
            worker.start();
        }
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    private Worker.Callback workerCallback = new Worker.Callback() {
        @Override
        public void onFetch(final Drawable drawable) {
            final ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
            if (imageView != null) {
                if (Looper.getMainLooper() == Looper.myLooper())
                    imageView.setImageDrawable(drawable);
                else
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageDrawable(drawable);
                        }
                    });
            }
        }

        @Override
        public void onComplete() {
            if (pendingProgress >= 0) {
                ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
                if (imageView != null) {
                    applyImageForProgress(imageView, pendingProgress);
                }
            }
        }
    };


    public void setPlaceholderDrawable(Drawable placeholderDrawable) {
        this.placeholderDrawable = placeholderDrawable;
    }

    private static class Worker extends Thread {

        public interface Callback {
            void onFetch(Drawable drawable);

            void onComplete();
        }

        private boolean running;
        private WeakReference<Callback> callbackWeakReference;
        private WeakReference<ImageView> imageViewWeakReference;
        private Bitmap.Config bitmapConfig;
        private String imageUri;

        public Worker(Bitmap.Config bitmapConfig, Callback callback, String imageUri, ImageView imageView) {
            this.bitmapConfig = bitmapConfig;
            this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
            this.imageViewWeakReference = imageView == null ? null : new WeakReference<>(imageView);
            this.imageUri = imageUri;
        }

        @Override
        public void run() {
            running = true;
            try {
                ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
                if (imageView != null && !TextUtils.isEmpty(imageUri)) {
                    DisplayImageOptions displayImageOptions = createDisplayImageOptions();
                    ImageLoader.getInstance().loadImageSync(imageUri, new ImageSize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight()), displayImageOptions);
                }
            } finally {
                running = false;
            }

            callComplete();

        }

        private DisplayImageOptions createDisplayImageOptions() {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            builder.bitmapConfig(bitmapConfig);
            builder.cacheOnDisk(true);
            builder.cacheInMemory(true);
            return builder.build();
        }

        private void callComplete() {
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            if (callback != null)
                callback.onComplete();
        }

        private void callFetch(Drawable drawable) {
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            if (callback != null)
                callback.onFetch(drawable);
        }

        public boolean isRunning() {
            return running;
        }
    }

}

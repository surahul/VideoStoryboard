package com.glennio.storyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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

    public SimpleWebImageApplier(Callback callback, Context context) {
        checkAndInitImageLoader(context);
        this.handler = new Handler(Looper.getMainLooper());
        this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
    }

    private static void checkAndInitImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .diskCacheFileCount(100)
                    .diskCacheSize(5 * 1024 * 1024)
                    .memoryCacheSizePercentage(10)
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }


    private void applyPlaceHolder() {
        handler.removeCallbacks(applyPlaceHolderRunnable);
        handler.postDelayed(applyPlaceHolderRunnable, 100);
    }

    private Runnable applyPlaceHolderRunnable = new Runnable() {
        @Override
        public void run() {
            ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
            if (imageView != null && placeholderDrawable != null && !placeholderDrawable.equals(imageView.getDrawable()))
                imageView.setImageDrawable(placeholderDrawable);
        }
    };

    @Override
    public void applyImageForProgress(ImageView imageView, int progress) {
        this.imageViewWeakReference = imageView == null ? null : new WeakReference<>(imageView);
        if (worker != null && worker.isRunning())
            pendingProgress = progress;
        else {
            applyPlaceHolder();
            this.pendingProgress = -1;
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            ImageSize imageSize = imageView == null ? null : new ImageSize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            worker = new Worker(bitmapConfig, workerCallback, callback == null ? null : callback.getImageUriForProgress(progress), imageSize);
            worker.start();
        }
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    private Worker.Callback workerCallback = new Worker.Callback() {
        @Override
        public void onFetch(final Bitmap bitmap) {
            final ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
            if (imageView != null) {
                if (Looper.getMainLooper() == Looper.myLooper())
                    imageView.setImageBitmap(bitmap);
                else
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacks(applyPlaceHolderRunnable);
                            imageView.setImageBitmap(bitmap);
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
            void onFetch(Bitmap bitmap);

            void onComplete();
        }

        private boolean running;
        private WeakReference<Callback> callbackWeakReference;
        private ImageSize imageSize;
        private Bitmap.Config bitmapConfig;
        private String imageUri;

        public Worker(Bitmap.Config bitmapConfig, Callback callback, String imageUri, ImageSize imageSize) {
            this.bitmapConfig = bitmapConfig;
            this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
            this.imageSize = imageSize;
            this.imageUri = imageUri;
        }

        @Override
        public void run() {
            running = true;
            try {
                if (imageSize != null && !TextUtils.isEmpty(imageUri)) {
                    DisplayImageOptions displayImageOptions = createDisplayImageOptions();
                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imageUri, imageSize, displayImageOptions);
                    if (bitmap != null)
                        callFetch(bitmap);

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

        private void callFetch(Bitmap bitmap) {
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            if (callback != null)
                callback.onFetch(bitmap);
        }

        public boolean isRunning() {
            return running;
        }
    }

}

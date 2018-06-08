package com.glennio.storyboard.image_applier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public abstract class BaseWebImageApplier implements StoryboardImageApplier {

    protected Handler handler;
    protected Drawable placeholderDrawable;
    protected int pendingProgress;
    protected Worker worker;
    protected WeakReference<ImageView> imageViewWeakReference;
    protected Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;
    private Runnable applyPlaceHolderRunnable = new Runnable() {
        @Override
        public void run() {
            ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
            if (imageView != null && placeholderDrawable != null && !placeholderDrawable.equals(imageView.getDrawable()))
                imageView.setImageDrawable(placeholderDrawable);
        }
    };
    protected Worker.Callback workerCallback = new Worker.Callback() {
        @Override
        public void onFetch(final Bitmap bitmap, final int progress) {
            if (Looper.getMainLooper() == Looper.myLooper())
                displayBitmap(bitmap, progress);
            else
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeCallbacks(applyPlaceHolderRunnable);
                        displayBitmap(bitmap, progress);
                    }
                });
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

    public BaseWebImageApplier(Context context) {
        checkAndInitImageLoader(context);
        this.handler = new Handler(Looper.getMainLooper());

    }

    private void checkAndInitImageLoader(Context context) {
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .diskCacheFileCount(100)
                    .diskCacheSize(5 * 1024 * 1024)
                    .memoryCacheSizePercentage(10)
                    .build();
            ImageLoader.getInstance().init(config);
        }
    }

    protected void applyPlaceHolder() {
        handler.removeCallbacks(applyPlaceHolderRunnable);
        handler.postDelayed(applyPlaceHolderRunnable, 100);
    }

    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        this.bitmapConfig = bitmapConfig;
    }

    public void setPlaceholderDrawable(Drawable placeholderDrawable) {
        this.placeholderDrawable = placeholderDrawable;
    }

    protected abstract void displayBitmap(Bitmap bitmap, int progress);

    protected void startWorker(int progress, ImageSize imageSize, String imageUri) {
        worker = new Worker(bitmapConfig, progress, workerCallback, imageUri, imageSize);
        worker.start();
    }

}

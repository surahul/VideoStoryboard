package com.glennio.storyboard.image_applier;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.ref.WeakReference;

public class Worker extends Thread {

        public interface Callback {
            void onFetch(Bitmap bitmap, int progress);

            void onComplete();
        }

        private boolean running;
        private WeakReference<Callback> callbackWeakReference;
        private ImageSize imageSize;
        private Bitmap.Config bitmapConfig;
        private String imageUri;
        private int progress;

        public Worker(Bitmap.Config bitmapConfig, int progress, Callback callback, String imageUri, ImageSize imageSize) {
            this.bitmapConfig = bitmapConfig;
            this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
            this.progress = progress;
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
                callback.onFetch(bitmap,progress);
        }

        public boolean isRunning() {
            return running;
        }
    }
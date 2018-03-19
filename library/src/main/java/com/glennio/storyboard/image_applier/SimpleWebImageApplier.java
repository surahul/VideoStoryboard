package com.glennio.storyboard.image_applier;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public class SimpleWebImageApplier extends BaseWebImageApplier {


    public interface Callback {
        String getImageUriForProgress(int progress);
    }

    private WeakReference<Callback> callbackWeakReference;

    public SimpleWebImageApplier(Callback callback, Context context) {
        super(context);
        this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
    }


    @Override
    public void applyImageForProgress(ImageView imageView, int progress) {
        this.imageViewWeakReference = imageView == null ? null : new WeakReference<>(imageView);
        if (worker != null && worker.isRunning())
            pendingProgress = progress;
        else {
            applyPlaceHolder();
            this.pendingProgress = -1;
            ImageSize imageSize = imageView == null ? null : new ImageSize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
            SimpleWebImageApplier.Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            startWorker(progress, imageSize, callback == null ? null : callback.getImageUriForProgress(progress));
        }
    }

    @Override
    protected void displayBitmap(Bitmap bitmap, int progress) {
        ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
        if (imageView != null)
            imageView.setImageBitmap(bitmap);
    }


}

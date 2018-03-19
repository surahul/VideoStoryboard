package com.glennio.storyboard.image_applier;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public class StitchedWebImageApplier extends BaseWebImageApplier {


    public static class Configuration {
        private int pageCount;
        private int pageWidth;
        private int pageHeight;
        private int imagePerRow;
        private int rowCount;
        private int progressPerImage;


        public Configuration( int pageCount, int pageWidth, int pageHeight, int imagePerRow, int rowCount, int progressPerImage) {
            this.pageCount = pageCount;
            this.pageWidth = pageWidth;
            this.pageHeight = pageHeight;
            this.imagePerRow = imagePerRow;
            this.rowCount = rowCount;
            this.progressPerImage = progressPerImage;
        }

        public int getPageCount() {
            return pageCount;
        }

        public int getPageWidth() {
            return pageWidth;
        }

        public int getPageHeight() {
            return pageHeight;
        }

        public int getImagePerRow() {
            return imagePerRow;
        }

        public int getRowCount() {
            return rowCount;
        }

        public int getProgressPerImage() {
            return progressPerImage;
        }
    }

    public interface Callback {
        String getImageUriForPage(int page);
    }

    private WeakReference<Callback> callbackWeakReference;
    private Configuration configuration;

    public StitchedWebImageApplier(Context context, Callback callback, Configuration configuration) {
        super(context);
        this.callbackWeakReference = callback == null ? null : new WeakReference<>(callback);
        this.configuration = configuration;
    }


    @Override
    public void applyImageForProgress(ImageView imageView, int progress) {
        this.imageViewWeakReference = imageView == null ? null : new WeakReference<>(imageView);
        if (worker != null && worker.isRunning())
            pendingProgress = progress;
        else {
            applyPlaceHolder();
            this.pendingProgress = -1;
            int imageWidth = imageView == null ? 0 : imageView.getMeasuredWidth();
            int imageHeight = imageView == null ? 0 : imageView.getMeasuredHeight();
            int xFactor = configuration.getImagePerRow();
            int yFractor = configuration.getRowCount();
            imageWidth = imageWidth * xFactor;
            imageHeight = imageHeight * yFractor;
            ImageSize imageSize = imageView == null ? null : new ImageSize(imageWidth, imageHeight);
            Callback callback = callbackWeakReference == null ? null : callbackWeakReference.get();
            int pageCount = configuration.getPageCount();
            int imagesPerPage = configuration.getImagePerRow() * configuration.getRowCount();
            int progressPerImage = configuration.getProgressPerImage();
            int globalImageIndex = (int) Math.ceil(progress / ((float) progressPerImage));
            int pageIndex = (int) Math.ceil(globalImageIndex / ((float) imagesPerPage));
            startWorker(progress, imageSize, callback == null ? null : callback.getImageUriForPage(Math.min(pageCount - 1, (pageIndex - 1))));
        }
    }

    @Override
    protected void displayBitmap(Bitmap bitmap, int progress) {
        ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
        if (imageView != null) {
            int imagesPerPage = configuration.getImagePerRow() * configuration.getRowCount();
            int progressPerImage = configuration.getProgressPerImage();
            int globalImageIndex = (int) Math.ceil(progress / ((float) progressPerImage));
            int pageIndex = (int) Math.ceil(globalImageIndex / ((float) imagesPerPage));
            pageIndex -= 1;
            globalImageIndex -= 1;
            int localIndex = globalImageIndex - (pageIndex * imagesPerPage);
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (int) (bitmap.getWidth() / ((float) (localIndex % configuration.getImagePerRow()))), (int) (bitmap.getHeight() / ((float) (localIndex % configuration.getRowCount()))), (int) (bitmap.getWidth() / (float) configuration.getImagePerRow()), (int) (bitmap.getHeight() / (float) configuration.getRowCount()));
            imageView.setImageBitmap(croppedBitmap);
        }

    }
}

package com.glennio.storyboard;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.glennio.storyboard.image_applier.StoryboardImageApplier;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public class VideoStoryboard {

    float padLeft, padRight;
    private WeakReference<View> viewToTranslateWeakReference;
    private WeakReference<SeekBarInterface> seekBarWeakReference;
    private WeakReference<ImageView> imageViewWeakReference;
    private boolean active;
    private float _16dp;
    private StoryboardImageApplier imageApplier;
    private boolean rtl;
    public VideoStoryboard(boolean rtl) {
        _16dp = Utils.dpToPx(16);
        padLeft = _16dp;
        padRight = padLeft;
        this.rtl = rtl;
    }

    public void setImageApplier(StoryboardImageApplier imageApplier) {
        this.imageApplier = imageApplier;
    }

    public void bindViews(@Nullable View viewToTranslate, @Nullable SeekBarInterface seekbarInterface, @Nullable ImageView imageView) {
        if (viewToTranslate != null)
            this.viewToTranslateWeakReference = new WeakReference<>(viewToTranslate);
        else {
            if (this.viewToTranslateWeakReference != null)
                this.viewToTranslateWeakReference.clear();
            this.viewToTranslateWeakReference = null;
        }
        if (seekbarInterface != null)
            this.seekBarWeakReference = new WeakReference<>(seekbarInterface);
        else {
            if (this.seekBarWeakReference != null)
                this.seekBarWeakReference.clear();
            this.seekBarWeakReference = null;
        }
        if (imageView != null)
            this.imageViewWeakReference = new WeakReference<>(imageView);
        else {
            if (this.imageViewWeakReference != null)
                this.imageViewWeakReference.clear();
            this.imageViewWeakReference = null;
        }
    }

    public void setPadLeft(float padLeft) {
        this.padLeft = padLeft;
    }

    public void setPadRight(float padRight) {
        this.padRight = padRight;
    }

    public void onStartTouch() {
        this.active = true;
        animateImageViewAppearance();
        onSeekBarProgressChanged(false);
    }

    public void onStopTouch() {
        this.active = false;
        animateImageViewDisappearance();
    }

    private void animateImageViewAppearance() {
        View viewToTranslate = viewToTranslateWeakReference == null ? null : viewToTranslateWeakReference.get();
        if (viewToTranslate != null) {
            viewToTranslate.setTranslationY(Utils.dpToPx(16));
            viewToTranslate.setAlpha(0f);
            viewToTranslate.setVisibility(View.VISIBLE);
            viewToTranslate.animate().alpha(1).translationY(0).setDuration(150).setInterpolator(new DecelerateInterpolator(1.5f)).setListener(null).start();
        }
    }

    private void animateImageViewDisappearance() {
        View viewToTranslate = viewToTranslateWeakReference == null ? null : viewToTranslateWeakReference.get();
        if (viewToTranslate != null) {
            viewToTranslate.animate().alpha(0).translationY(Utils.dpToPx(16)).setDuration(100).setInterpolator(new AccelerateInterpolator(2f)).setListener(null).start();
        }
    }

    public void onSeekBarProgressChanged(boolean force) {
        if (!active && force) {
            active = true;
            animateImageViewAppearance();
        }
        if (active) {
            SeekBarInterface seekBarInterface = seekBarWeakReference == null ? null : seekBarWeakReference.get();
            View viewToTranslate = viewToTranslateWeakReference == null ? null : viewToTranslateWeakReference.get();
            if (seekBarInterface != null && viewToTranslate != null) {
                ViewParent viewParent = viewToTranslate.getParent();
                if (viewParent != null && viewParent instanceof View) {
                    View parent = (View) viewParent;
                    int progress = seekBarInterface.getProgress();
                    int max = seekBarInterface.getMax();
                    float normalizedProgress = ((float) progress) / max;
                    float seekBarX = seekBarInterface.getX() + _16dp;
                    float seekBarWidth = seekBarInterface.getMeasuredWidth() - (2 * _16dp);
                    float minTranslationX = rtl ? padRight : padLeft;
                    float maxTranslationX = parent.getMeasuredWidth() - (viewToTranslate.getMeasuredWidth() + (rtl ? padLeft : padRight));
                    float translationX = ((seekBarX + (normalizedProgress * seekBarWidth)) - (viewToTranslate.getMeasuredWidth() / 2f)) - (rtl ? 3 * _16dp : 0);
                    viewToTranslate.setTranslationX((rtl ? -1 : 1) * Math.min(Math.max(translationX, minTranslationX), maxTranslationX));
                    ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
                    if (imageView != null && imageApplier != null) {
                        imageApplier.applyImageForProgress(imageView, progress);
                    }
                }

            }
        }
    }

    public interface SeekBarInterface {

        int getProgress();

        int getMax();

        float getX();

        float getMeasuredWidth();
    }


}

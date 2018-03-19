package com.glennio.storyboard;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.lang.ref.WeakReference;

/**
 * Created by rahulverma on 19/03/18.
 */

public class VideoStoryboard {

    private WeakReference<View> viewToTranslateWeakReference;
    private WeakReference<SeekBar> seekBarWeakReference;
    private WeakReference<ImageView> imageViewWeakReference;
    float padLeft, padRight;
    private boolean active;
    private float _16dp;
    private StoryboardImageApplier imageApplier;

    public VideoStoryboard() {
        _16dp = Utils.dpToPx(16);
        padLeft = _16dp;
        padRight = padLeft;
    }


    public void setImageApplier(StoryboardImageApplier imageApplier) {
        this.imageApplier = imageApplier;
    }

    public void bindViews(@Nullable View viewToTranslate, @Nullable SeekBar seekBar, @Nullable ImageView imageView) {
        if (viewToTranslate != null)
            this.viewToTranslateWeakReference = new WeakReference<>(viewToTranslate);
        else {
            if (this.viewToTranslateWeakReference != null)
                this.viewToTranslateWeakReference.clear();
            this.viewToTranslateWeakReference = null;
        }
        if (seekBar != null)
            this.seekBarWeakReference = new WeakReference<>(seekBar);
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
        onSeekBarProgressChanged();
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
            viewToTranslate.animate().alpha(1).translationY(0).setDuration(150).setListener(null).start();
        }
    }

    private void animateImageViewDisappearance() {
        View viewToTranslate = viewToTranslateWeakReference == null ? null : viewToTranslateWeakReference.get();
        if (viewToTranslate != null) {
            viewToTranslate.animate().alpha(0).translationY(Utils.dpToPx(16)).setDuration(150).setListener(null).start();
        }
    }

    public void onSeekBarProgressChanged() {
        if (active) {
            SeekBar seekBar = seekBarWeakReference == null ? null : seekBarWeakReference.get();
            View viewToTranslate = viewToTranslateWeakReference == null ? null : viewToTranslateWeakReference.get();
            if (seekBar != null && viewToTranslate != null) {
                ViewParent viewParent = viewToTranslate.getParent();
                if (viewParent != null && viewParent instanceof View) {
                    View parent = (View) viewParent;
                    int progress = seekBar.getProgress();
                    int max = seekBar.getMax();
                    float normalizedProgress = ((float) progress) / max;
                    float seekBarX = seekBar.getX() + _16dp;
                    float seekBarWidth = seekBar.getMeasuredWidth() - (2 * _16dp);
                    float minTranslationX = padLeft;
                    float maxTranslationX = parent.getMeasuredWidth() - (viewToTranslate.getMeasuredHeight() + padRight);
                    float translationX = (seekBarX + (normalizedProgress * seekBarWidth)) - (viewToTranslate.getMeasuredWidth() / 2f);
                    viewToTranslate.setTranslationX(Math.min(Math.max(translationX, minTranslationX), maxTranslationX));

                    ImageView imageView = imageViewWeakReference == null ? null : imageViewWeakReference.get();
                    if (imageView != null && imageApplier != null) {
                        imageApplier.applyImageForProgress(imageView, progress);
                    }
                }

            }
        }
    }



}

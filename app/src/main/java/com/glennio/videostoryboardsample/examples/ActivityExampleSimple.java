package com.glennio.videostoryboardsample.examples;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.glennio.storyboard.VideoStoryboard;
import com.glennio.storyboard.image_applier.StoryboardImageApplier;
import com.glennio.videostoryboardsample.R;

/**
 * Created by rahulverma on 19/03/18.
 */

public class ActivityExampleSimple extends AppCompatActivity {

    private Drawable staticDrawable;
    private SeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        staticDrawable = getResources().getDrawable(R.drawable.sample_image);

        seekBar = findViewById(R.id.seek_bar);
        final VideoStoryboard videoStoryboard = new VideoStoryboard(getResources().getBoolean(R.bool.is_right_to_left));
        videoStoryboard.bindViews(findViewById(R.id.view_to_translate), seekBarInterface, (ImageView) findViewById(R.id.view_to_translate));
        videoStoryboard.setImageApplier(new StoryboardImageApplier() {
            @Override
            public void applyImageForProgress(ImageView imageView, int progress) {
                imageView.setImageDrawable(staticDrawable);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoStoryboard.onSeekBarProgressChanged(fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoStoryboard.onStartTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoStoryboard.onStopTouch();
            }
        });
    }

    private VideoStoryboard.SeekBarInterface seekBarInterface = new VideoStoryboard.SeekBarInterface() {
        @Override
        public int getProgress() {
            return seekBar.getProgress();
        }

        @Override
        public int getMax() {
            return seekBar.getMax();
        }

        @Override
        public float getX() {
            return seekBar.getX();
        }

        @Override
        public float getMeasuredWidth() {
            return seekBar.getMeasuredWidth();
        }
    };


}

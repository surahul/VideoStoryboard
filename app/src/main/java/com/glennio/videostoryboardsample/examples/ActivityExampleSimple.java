package com.glennio.videostoryboardsample.examples;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.glennio.storyboard.StoryboardImageProvider;
import com.glennio.storyboard.VideoStoryboard;
import com.glennio.videostoryboardsample.R;

/**
 * Created by rahulverma on 19/03/18.
 */

public class ActivityExampleSimple extends AppCompatActivity{

    private Drawable staticDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_example);

        staticDrawable = getResources().getDrawable(R.drawable.sample_image);

        SeekBar seekBar = findViewById(R.id.seek_bar);
        final VideoStoryboard videoStoryboard = new VideoStoryboard();
        videoStoryboard.bindViews(findViewById(R.id.view_to_translate),seekBar,(ImageView)findViewById(R.id.view_to_translate));
        videoStoryboard.setImageProvider(new StoryboardImageProvider() {
            @Override
            public Drawable getImageForProgress(int progress) {
                return staticDrawable;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                videoStoryboard.onSeekBarProgressChanged();
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


}

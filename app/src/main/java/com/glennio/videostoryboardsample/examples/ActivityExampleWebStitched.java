package com.glennio.videostoryboardsample.examples;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.glennio.storyboard.VideoStoryboard;
import com.glennio.storyboard.image_applier.StitchedWebImageApplier;
import com.glennio.videostoryboardsample.R;

/**
 * Created by rahulverma on 19/03/18.
 */

public class ActivityExampleWebStitched extends AppCompatActivity {

    private Drawable staticDrawable;
    private StitchedWebImageApplier stitchedWebImageApplier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        staticDrawable = getResources().getDrawable(R.drawable.sample_image);
        SeekBar seekBar = findViewById(R.id.seek_bar);
        seekBar.setMax(500);
        seekBar.setProgress(0);

        final VideoStoryboard videoStoryboard = new VideoStoryboard();
        stitchedWebImageApplier = new StitchedWebImageApplier(getApplicationContext(), webImageApplierCallback, new StitchedWebImageApplier.Configuration(
                4,
                800,
                450,
                5,
                5,
                5
        ));
        stitchedWebImageApplier.setPlaceholderDrawable(new ColorDrawable(Color.GRAY));

        videoStoryboard.bindViews(findViewById(R.id.view_to_translate), seekBar, (ImageView) findViewById(R.id.view_to_translate));
        videoStoryboard.setImageApplier(stitchedWebImageApplier);
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

    private StitchedWebImageApplier.Callback webImageApplierCallback = new StitchedWebImageApplier.Callback() {
        @Override
        public String getImageUriForPage(int page) {
            return String.format("https://github.com/surahul/VideoStoryboard/blob/master/static_images/stitched/stitched_%s.png?raw=true", String.valueOf(1 + page));
        }


    };


}

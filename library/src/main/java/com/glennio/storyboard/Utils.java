package com.glennio.storyboard;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by rahulverma on 19/03/18.
 */

public class Utils {

    public static int dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }

}

package com.medroid.acnescanner.vizualize;

/**
 * Created by tal on 17/12/2015.
 */

import android.graphics.Color;

public class Utils {

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}

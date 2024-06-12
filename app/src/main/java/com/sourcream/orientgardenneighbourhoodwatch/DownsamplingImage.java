package com.sourcream.orientgardenneighbourhoodwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DownsamplingImage {

    public static Bitmap decodeSampleBitmapFromInputStream(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        final int standardHeight = 2400;
        final int standardWidth = 1080;
        int inSampleSize = 1;


        if (height > standardHeight || width > standardWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= standardHeight
                    && (halfWidth / 2) >= standardWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

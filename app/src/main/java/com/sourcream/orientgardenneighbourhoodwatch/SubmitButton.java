package com.sourcream.orientgardenneighbourhoodwatch;

import android.content.Context;
import android.util.AttributeSet;


public class SubmitButton extends androidx.appcompat.widget.AppCompatButton {
    public SubmitButton(Context context) {
        super(context);
        init();
    }

    public SubmitButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubmitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setElevation((float) 1.0);
    }
}

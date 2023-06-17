package com.example.goboom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class HoverImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnHoverListener {

    // Define some constants for the animation
    private static final float SCALE_FACTOR = 1.2f;
    private static final long ANIMATION_DURATION = 300;

    // Constructor
    public HoverImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Set the onHoverListener to this
        setOnHoverListener(this);
    }

    // onHover method
    @Override
    public boolean onHover(View v, MotionEvent event) {
        // Get the action of the event
        int action = event.getAction();

        // Check if the action is hover enter or hover exit
        if (action == MotionEvent.ACTION_HOVER_ENTER) {
            // Hover enter, scale up the ImageView
            scaleUp();
        } else if (action == MotionEvent.ACTION_HOVER_EXIT) {
            // Hover exit, scale down the ImageView
            scaleDown();
        }

        // Return true to indicate that we handled the event
        return true;
    }

    // Helper method to scale up the ImageView
    private void scaleUp() {
        // Animate the scaleX and scaleY properties of the ImageView
        animate().scaleX(SCALE_FACTOR).scaleY(SCALE_FACTOR).setDuration(ANIMATION_DURATION).start();
    }

    // Helper method to scale down the ImageView
    private void scaleDown() {
        // Animate the scaleX and scaleY properties of the ImageView
        animate().scaleX(1f).scaleY(1f).setDuration(ANIMATION_DURATION).start();
    }
}


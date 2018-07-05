package com.lu.swirl;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.Animatable2Compat;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class SwirlView extends AppCompatImageView {
    // Keep in sync with attrs.
    public enum State {
        OFF,
        ON,
        ERROR,
        SUCCESS
    }

    private State state = State.OFF;

    public SwirlView(Context context) {
        this(context, null);
    }

    public SwirlView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw new AssertionError("API 21 required.");
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.swirl_Swirl);
        int state = a.getInteger(R.styleable.swirl_Swirl_swirl_state, -1);
        if (state != -1) {
            setState(State.values()[state]);
        }
        a.recycle();
    }

    public void setState(State state) {
        if (state == this.state) return;

        @DrawableRes int resId = getDrawable(this.state, state);
        if (resId == 0) {
            setImageDrawable(null);
        } else {

            Drawable icon = AnimatedVectorDrawableCompat.create(getContext(), resId);
            if (icon == null) {
                icon = VectorDrawableCompat.create(getResources(), resId, getContext().getTheme());
            }
            if (state == State.SUCCESS && getDrawable() != null) {
                LayerDrawable layerDrawable = new LayerDrawable(
                        new Drawable[]{getDrawable(), icon}
                );
                setImageDrawable(layerDrawable);
            } else {
                setImageDrawable(icon);
            }

            if (icon instanceof AnimatedVectorDrawableCompat) {
                final AnimatedVectorDrawableCompat vectorDrawableCompat = (AnimatedVectorDrawableCompat) icon;
                vectorDrawableCompat.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        vectorDrawableCompat.unregisterAnimationCallback(this);
                    }
                });
                vectorDrawableCompat.start();
            }
        }

        this.state = state;
    }


    @DrawableRes
    private static int getDrawable(State currentState, State newState) {
        switch (newState) {
            case OFF:
                if (currentState == State.ERROR) {
                    return R.drawable.swirl_error_off_animation;
                }
                return R.drawable.swirl_draw_off_animation;
            case ON:
                if (currentState == State.ERROR) {
                    return R.drawable.swirl_error_state_to_fp_animation;
                }
                return R.drawable.swirl_draw_on_animation;
            case ERROR:
                if (currentState == State.OFF) {
                    return R.drawable.swirl_error_on_animation;
                }
                return R.drawable.swirl_fp_to_error_state_animation;
            case SUCCESS:
                if (currentState == State.ERROR) {
                    return R.drawable.swirl_error_state_to_success_animation;
                }
                return R.drawable.swirl_draw_success_animation;
            default:
                throw new IllegalArgumentException("Unknown state: " + newState);
        }
    }
}

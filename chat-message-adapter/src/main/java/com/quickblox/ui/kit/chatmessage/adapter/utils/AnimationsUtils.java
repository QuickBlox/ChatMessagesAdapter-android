package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class AnimationsUtils {

    public static void hideView(final View view, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animator.start();
    }

    public static void showView(final View view, long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        animator.start();
    }
}

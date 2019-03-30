package com.song.redcord.map;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;

import com.song.redcord.R;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfoController implements View.OnClickListener, View.OnTouchListener{
    private AtomicBoolean isExpand = new AtomicBoolean(false);
    private View expandView;
    private View closeView;
    private AnimatorSet animSet;

    public InfoController(View view) {
        expandView = view.findViewById(R.id.expand_view);
        closeView = view.findViewById(R.id.close_view);
        expandView.setOnClickListener(this);
        closeView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.expand_view || id == R.id.close_view) {
            if (isExpand.getAndSet(!isExpand.get())) {
                close();
            } else {
                expand();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.map) {
            if (isExpand.get()) {
                isExpand.set(false);
                close();
            }
        }
        return false;
    }

    private void close() {
        anim(closeView, expandView);
    }

    private void expand() {
        anim(expandView, closeView);
    }

    private void anim(final View in, final View out) {
        if (animSet != null && animSet.isRunning()) {
            return;
        }

        in.setVisibility(View.VISIBLE);
        out.setVisibility(View.VISIBLE);
        ObjectAnimator inT = ObjectAnimator.ofFloat(in, View.ROTATION_X, -90, 0);
        ObjectAnimator inA = ObjectAnimator.ofFloat(in, View.ALPHA, 0, 1);
        ObjectAnimator inS = ObjectAnimator.ofFloat(in, View.SCALE_Y, 0, 1);
        ObjectAnimator outT = ObjectAnimator.ofFloat(out, View.ROTATION_X, 0, 90);
        ObjectAnimator outS = ObjectAnimator.ofFloat(out, View.SCALE_Y, 1, 0);
        ObjectAnimator outA = ObjectAnimator.ofFloat(out, View.ALPHA, 1, 0);

        animSet = new AnimatorSet();
        animSet.playTogether(inT, inA, inS, outT, outA, outS);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                in.setAlpha(1);
                in.setRotationX(0);
                in.setScaleY(1);
                out.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

    }
}

package com.example.xyzreader.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.xyzreader.R;


public class Animations {

	public static void revealEffectHide(final Context ctx, final View view, @ColorRes final int color,
										final int finalRadius, final OnRevealAnimationListener listener) {
		int cx = view.getWidth() / 2;
		int cy = view.getHeight() / 2;
		int initialRadius = view.getWidth();

		Animator anim =
				ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, finalRadius);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				view.setBackgroundColor(ctx.getResources().getColor(color));
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				listener.onRevealHide();
				view.setVisibility(View.INVISIBLE);
			}
		});
		anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
		anim.start();
	}

	public static void revealEffectShow(final Context ctx, final View view, final int startRadius,
										@ColorRes final int color, int x, int y, final OnRevealAnimationListener listener) {
		float finalRadius = (float) Math.hypot(view.getWidth()/2, view.getHeight()/2);

		Animator anim =
				ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
		anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
		anim.setStartDelay(100);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				view.setBackgroundColor(ctx.getResources().getColor(color));
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				view.setVisibility(View.VISIBLE);
				listener.onRevealShow();
			}
		});
		anim.start();
	}
}

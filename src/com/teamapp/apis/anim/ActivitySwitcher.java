package com.teamapp.apis.anim;

import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

public class ActivitySwitcher {

	private final static int DURATION = 300;
	private final static float DEPTH = 300.0f;

	/* ----------------------------------------------- */

	public interface AnimationFinishedListener {
		/**
		 * Called when the animation is finished.
		 */
		public void onAnimationFinished();
	}

	/* ----------------------------------------------- */

	public static void animationLeftIn(View container,
			WindowManager windowManager) {
		animationLeftIn(container, windowManager, null);
	}

	public static void animationLeftIn(View container,
			WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(0, -90, true, container, windowManager, listener);
	}

	public static void animationRightOut(View container,
			WindowManager windowManager) {
		animationRightOut(container, windowManager, null);
	}

	public static void animationRightOut(View container,
			WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(90, 0, false, container, windowManager, listener);
	}

	public static void animationRightIn(View container,
			WindowManager windowManager) {
		animationRightIn(container, windowManager, null);
	}

	public static void animationRightIn(View container,
			WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(0, 90, true, container, windowManager, listener);
	}

	public static void animationLeftOut(View container,
			WindowManager windowManager) {
		animationLeftOut(container, windowManager, null);
	}

	public static void animationLeftOut(View container,
			WindowManager windowManager, AnimationFinishedListener listener) {
		apply3DRotation(-90, 0, false, container, windowManager, listener);
	}

	/* ----------------------------------------------- */

	private static void apply3DRotation(float fromDegree, float toDegree,
			boolean reverse, View container, WindowManager windowManager,
			final AnimationFinishedListener listener) {
		Display display = windowManager.getDefaultDisplay();
		final float centerX = display.getWidth() / 2.0f;
		final float centerY = display.getHeight() / 2.0f;

		final Rotate3dAnimation a = new Rotate3dAnimation(fromDegree, toDegree,
				centerX, centerY, DEPTH, reverse);
		a.reset();
		a.setDuration(DURATION);
		a.setFillAfter(true);
		a.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			a.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					listener.onAnimationFinished();
				}
			});
		}
		container.clearAnimation();
		container.startAnimation(a);
	}
}
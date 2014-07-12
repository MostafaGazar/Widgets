/*
 * Copyright 2014 Mostafa Gazar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.meg7.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Attach to any view to add ripple touch effect on top of.
 *
 * @author Mostafa Gazar <eng.mostafa.gazar@gmail.com>
 */
public class RippleView extends View {

    private static final int COLOR_RIPPLE_BG = Color.parseColor("#59787878");

	private Context mContext;
	private View mTargetView;
    private FrameLayout mContainer;

    public RippleView(Context context) {
        this(context, (AttributeSet) null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Simple constructor to use to add ripple effect to view from code.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param target  The View to add the ripple effect to.
     */
    public RippleView(Context context, View target) {
        this(context, null, 0);
        
        mTargetView = target;
        if (mTargetView != null) {
            apply();
        }
    }

    @SuppressWarnings("deprecation")
    public RippleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        setAlpha(0f);

        OvalShape oval = new OvalShape();
        ShapeDrawable rippleBg = new ShapeDrawable(oval);
        rippleBg.getPaint().setColor(COLOR_RIPPLE_BG);
        setBackgroundDrawable(rippleBg);
    }

    private void apply() {
        ViewGroup.MarginLayoutParams targetViewParams = (ViewGroup.MarginLayoutParams) mTargetView.getLayoutParams();
		ViewParent parent = mTargetView.getParent();
		mContainer = new FrameLayout(mContext);
        // For testing purposes.
//        mContainer.setBackgroundColor(Color.parseColor("#59FF0000"));

		if (parent instanceof ViewGroup) {// Just to be sure.
			ViewGroup group = (ViewGroup) parent;
			int index = group.indexOfChild(mTargetView);

            // Replace target view with the new container view.
			group.removeView(mTargetView);
			group.addView(mContainer, index, targetViewParams);

            // Remove target view margins because it is already added to the new container view.
            ViewGroup.MarginLayoutParams newTargetViewParams = new ViewGroup.MarginLayoutParams(targetViewParams);
            newTargetViewParams.leftMargin = 0;
            newTargetViewParams.topMargin = 0;
            newTargetViewParams.rightMargin = 0;
            newTargetViewParams.bottomMargin = 0;
            mTargetView.setLayoutParams(newTargetViewParams);

            // Add target and ripple views to the new container view.
            mContainer.addView(mTargetView);
			mContainer.addView(this);

			group.invalidate();
		}

        // Show the ripple effect on target view touch down.
        mTargetView.setOnTouchListener(mTargetViewOnTouchListener);
        mTargetView.addOnLayoutChangeListener(mTargetViewOnLayoutChangeListener);
	}

    private OnTouchListener mTargetViewOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setPivotX(e.getX());
                    setPivotY(e.getY());

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(ObjectAnimator.ofFloat(RippleView.this, "scaleX", 0.35f, 1f),
//                            ObjectAnimator.ofFloat(RippleView.this, "scaleY", 0f, 1f),
                            ObjectAnimator.ofFloat(RippleView.this, "alpha", 0f, 0.3f, 1f, 0f));
                    set.start();
            }

            return false;
        }
    };

    private OnLayoutChangeListener mTargetViewOnLayoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int targetViewWidth = right - left;
            int targetViewHeight = bottom - top;

            if (targetViewWidth > 0 && targetViewHeight > 0) {
                mTargetView.removeOnLayoutChangeListener(mTargetViewOnLayoutChangeListener);

                // Just to make sure it is not larger than the original target view size.
                ViewGroup.LayoutParams containerParams = mContainer.getLayoutParams();
                containerParams.width = targetViewWidth;
                containerParams.height = targetViewHeight;
                mContainer.setLayoutParams(containerParams);
            } else {
                // In case layout_weight was used on target view.
                targetViewWidth = mContainer.getMeasuredWidth();
                targetViewHeight = mContainer.getMeasuredHeight();

                ViewGroup.LayoutParams targetParams = mTargetView.getLayoutParams();
                targetParams.width = targetViewWidth;
                targetParams.height = targetViewHeight;
                mTargetView.setLayoutParams(targetParams);
            }

            // Allow drawing out of parent view.
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
            int diameter = Math.max(targetViewWidth, targetViewHeight);
            int delta = -5 * diameter / 100;// Small delta to subtract from ripple view size.
            params.width = diameter + delta;
            params.height = diameter + delta;
            int diff = Math.abs(targetViewWidth - targetViewHeight) + delta;
            if (targetViewWidth < targetViewHeight) {
                params.leftMargin = -diff / 2;
                params.topMargin = -delta / 2;
            } else if (targetViewHeight < targetViewWidth) {
                params.leftMargin = -delta / 2;
                params.topMargin = -diff / 2;
            }
            setLayoutParams(params);
        }
    };

}

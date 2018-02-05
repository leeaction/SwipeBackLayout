package me.imid.swipebacklayout.lib.app;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.TranslucentConversionListener;
import me.imid.swipebacklayout.lib.Utils;

/**
 * @author Yrom
 */
public class SwipeBackActivityHelper {
    private Activity mActivity;

    private SwipeBackLayout mSwipeBackLayout;

    private int mDefaultBackgroundColor = 0xBFFFFFFF;

    public SwipeBackActivityHelper(Activity activity) {
        mActivity = activity;
    }

    public void setDefaultBackgroundColor(int defaultBackgroundColor) {
        this.mDefaultBackgroundColor = defaultBackgroundColor;
    }

    @SuppressWarnings("deprecation")
    public void onActivityCreate() {
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(mDefaultBackgroundColor != -1){
            mActivity.getWindow().getDecorView().setBackgroundDrawable(new ColorDrawable(mDefaultBackgroundColor));
        }else{
            mActivity.getWindow().getDecorView().setBackgroundDrawable(null);
        }

        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(mActivity).inflate(
                me.imid.swipebacklayout.lib.R.layout.swipeback_layout, null);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                Utils.convertActivityToTranslucent(mActivity, new TranslucentConversionListener() {
                    @Override
                    public void onTranslucentConversionComplete() {
                        View decorView = mActivity.getWindow().getDecorView();
                        doBackgroundAnimation(decorView);
                    }
                });
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachToActivity(mActivity);
    }

    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    protected void doBackgroundAnimation(View view){
        if(mDefaultBackgroundColor == -1){
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "backgroundColor", mDefaultBackgroundColor, 0x00ffffff);
        animator.setDuration(500);//时间1s
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}

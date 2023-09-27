package com.lonelypluto.pdfviewerdemo.widget;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Project: boxplayer-cg<br/>
 * Package: com.cloudapp.client.player<br/>
 * ClassName: MoveTouchListener<br/>
 * Description: TODO<br/>
 * Date: 2020/7/15 3:27 PM <br/>
 * <p>
 * Author  LuoHao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class MoveTouchListener implements View.OnTouchListener {
    private static final String TAG = "MoveTouchListener";

    private boolean mHandleBound; // 是否要处理边界 true——>touch结束会将控件移到最边上
    private boolean mIgnoreBottom;
    private View moveView;//需要移动的view 为null时移动view本身
    float mDx, mDy;
    long mDownClick;
    protected boolean isMove = false;
    private int mWidth = -1, mHeight = -1, minTouch = -1;

    public MoveTouchListener(boolean handleBound) {
        this(handleBound, null);
    }

    public MoveTouchListener(boolean handleBound, View moveView) {
        this(handleBound, true, moveView);
    }

    public MoveTouchListener(boolean handleBound, boolean ignoreBottom, View moveView) {
        this.mHandleBound = handleBound;
        this.moveView = moveView;
        this.mIgnoreBottom = ignoreBottom;
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (-1 == mWidth || -1 == mHeight || mWidth == mHeight) {
            mWidth = getScreenWidth(view.getContext());
            mHeight = getScreenHeight(view.getContext());
        }
        if (-1 == minTouch) {
            minTouch = ViewConfiguration.get(view.getContext()).getScaledEdgeSlop();
        }
        int action = event.getAction();
        View v = (null == moveView) ? view : moveView;
        int orientation = v.getResources().getConfiguration().orientation;
        Log.i(TAG, String.format(" mWidth is %s  , mHeight is %s , orientation is %s ", mWidth, mHeight, orientation));
        if (SCREEN_ORIENTATION_PORTRAIT == orientation) {
            mHeight = Math.max(mWidth, mHeight);
        } else {
            mWidth = Math.min(mWidth, mHeight);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDx = v.getX() - event.getRawX();
                mDy = v.getY() - event.getRawY();
                mDownClick = System.currentTimeMillis();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float xo = event.getRawX() + mDx;
                float yo = event.getRawY() + mDy;
                if (xo <= 0) {
                    xo = 0;
                }
                if (yo <= 0) {
                    yo = 0;
                }
                int maxWidth = mWidth - v.getWidth();
                if (xo > maxWidth) {
                    xo = maxWidth;
                }
                int maxHeight = mHeight - v.getHeight();
                if (yo > maxHeight) {
                    yo = maxHeight;
                }
                if (Math.abs(xo - v.getX()) >= minTouch || Math.abs(yo - v.getY()) >= minTouch) {
                    isMove = true;
                }
                onMove(v, xo, yo);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mHandleBound) {
                    reLayout(v);
                }
                if (System.currentTimeMillis() - mDownClick <= 200 && !isMove) {
                    view.performClick();
                }
                break;
        }
        return true;
    }

    protected void onMove(View v, float x, float y) {
        v.setX(x);
        v.setY(y);
    }

    protected void reLayout(View v) {
        int x;
        if ((v.getX() + v.getWidth() / 2) > mWidth / 2) {
            x = mWidth - v.getWidth();
        } else {
            x = 0;
        }

        ObjectAnimator tAnimator = ObjectAnimator.ofFloat(v, "x", x).setDuration(300);
        tAnimator.start();
    }

    public static int getScreenWidth(Context context) {
        if (context == null) return -1;
        DisplayMetrics displaymetrics = getRealDisplayMetricsForAndroid40(context);
        return displaymetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        if (context == null) return -1;
        DisplayMetrics displaymetrics = getRealDisplayMetricsForAndroid40(context);
        return displaymetrics.heightPixels;
    }

    private static final DisplayMetrics getRealDisplayMetricsForAndroid40(Context context) {
        if (context == null) return null;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm;
    }
}

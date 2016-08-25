package com.example.administrator.verticallinearlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/8/22.
 */
public class VerticalLinearlayout extends ViewGroup {

    private static final String TAG = "VerticalLinearlayout";
    private int mScreenHeight;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private float mScrollStart,mScrollEnd;
    private int mLastY;
    private boolean isScrolling;

    public VerticalLinearlayout(Context context) {
        this(context, null);
    }

    public VerticalLinearlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获得windowManager，并且获得屏幕高度
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        Log.e(TAG, "VerticalLinearlayout: " + mScreenHeight);

        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //给每个childview确定大小
        int childCount = getChildCount();
        for(int i = 0 ; i < childCount ; i++){
            View childView = getChildAt(i);
            measureChild(childView,widthMeasureSpec,mScreenHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int childCount = getChildCount();
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            lp.height = childCount * mScreenHeight;
            setLayoutParams(lp);
            for(int i = 0 ; i < childCount ; i++){
                View child = getChildAt(i);
                if(child.getVisibility() != GONE){
                    child.layout(l,i * mScreenHeight , r , (i + 1) * mScreenHeight);
                }
            }
        }
        Log.e(TAG, "onLayout: getMeasuredHeight = " + getMeasuredHeight() + " , getHeight = " + getHeight() );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isScrolling){
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        int y = (int) event.getY();
        //初始化加速度检测器
        obtainVelocity(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                //ScrollY往上是正方向，ScrollX往左是正方向
                mScrollStart = getScrollY();
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if(!mScroller.isFinished())
                    mScroller.abortAnimation();
                int dy = mLastY - y;
                //当前VerticalLinearlayout的左上角y坐标
                int scrolly = getScrollY();
                Log.e(TAG, "onTouchEvent: getScrolly = "+ scrolly );
                //到顶部,还往下滑
                if(dy < 0 && scrolly + dy < 0){
                    dy = -scrolly;
                }
                //到了底部，还在上滑
                if(dy > 0 && dy + scrolly + mScreenHeight > getHeight()){
                    dy = getHeight() - mScreenHeight - scrolly;
                }
                //移动相对位移
                scrollBy(0,dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mScrollEnd = getScrollY();
                int dScrollY = (int) (mScrollEnd - mScrollStart);
                Log.e(TAG, "mScrollStart = " + mScrollStart + ",mScrollEnd = " + mScrollEnd );
                if (wantScrollToNext())// 往上滑动
                {
                    if (shouldScrollToNext())
                    {
                        //把剩下的距离往上滑，默认时间 DEFAULT_DURATION = 250
                        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
                    } else
                    {
                        //把已经往上滑的距离滑下来
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }

                if (wantScrollToPre())// 往下滑动
                {
                    if (shouldScrollToPre())
                    {
                        //和上面的原理相同，这里的dScrollY是负值
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
                    } else
                    {
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }
                isScrolling = true;
                //实现view的弹性滑动，postInvalidate会导致view重绘，在view的draw方法中会调用computeScroll
                postInvalidate();
                recycleVelocity();
                break;
        }
        return true;
    }


    /**
     * 滑动的距离超过屏幕的一半，速度大于600
     * @return
     */
    private boolean shouldScrollToNext(){
        return mScrollEnd - mScrollStart > mScreenHeight / 2 || Math.abs(getVelocity()) > 600;
    }

    /**
     * 结束的ScrollY的位置大于开始的ScrollY
     * @return
     */
    private boolean wantScrollToNext() {
        return mScrollEnd > mScrollStart;
    }

    private boolean shouldScrollToPre(){
        return mScrollStart - mScrollEnd > mScreenHeight / 2 || Math.abs(getVelocity()) >600;
    }

    private boolean wantScrollToPre(){
        return mScrollEnd < mScrollStart;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //computeScrollOffset滑动没有结束一直返回true
        if(mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            postInvalidate();
        }else{
            isScrolling = false;
        }
    }

    private double getVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        return mVelocityTracker.getYVelocity();
    }

    private void recycleVelocity(){
        if(mVelocityTracker != null){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void obtainVelocity(MotionEvent event) {
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
}

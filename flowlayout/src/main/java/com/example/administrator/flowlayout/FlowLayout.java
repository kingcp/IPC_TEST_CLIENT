package com.example.administrator.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";
    private ArrayList<ArrayList<View>> childViewsList = new ArrayList<ArrayList<View>>();
    private ArrayList<Integer> heightList = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        childViewsList.clear();
        heightList.clear();
        Log.e(TAG, "onMeasure: " + sizeWidth);
        //每一行的长度，每一行的高度
        int lineWidth = 0 , maxLineHeight = 0;
        //FlowLayout 的宽度高度
        int width = 0,height = 0;
        //存储一行的view
        ArrayList<View> hangChildViews = new ArrayList<>();
        int cCount = getChildCount();
        for(int i = 0 ; i < cCount ; i++){
            View childView = getChildAt(i);
//            Log.e(TAG, "onMeasure: before measureChild width = " + childView.getMeasuredWidth());
            //测量过子view才能获得宽高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
//            Log.e(TAG, "onMeasure: after measureChild width = " + childView.getMeasuredWidth());
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //childView的宽度包括本身宽度和左右margin
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            //宽度超过范围需要换行
            Log.e(TAG, "onMeasure: lineWidth + childWidth = " + (lineWidth + childWidth) + ", sizeWidth = " + sizeWidth );
            if(lineWidth + childWidth > (sizeWidth - getPaddingLeft() - getPaddingRight()) ){
                width = Math.max(width, lineWidth);
                //总高度 加上这一行中最高的高度
                height += maxLineHeight;
                //将这一行的行高数据和行中子view的数据添加到heightList和childViewList中
                heightList.add(maxLineHeight);
                childViewsList.add(hangChildViews);
                //开启新一行，并初始化值，这时的childView是新行的第一个view
                hangChildViews = new ArrayList<>();
                hangChildViews.add(childView);
                maxLineHeight = childHeight;
                lineWidth = childWidth;
            }else{//不需要换行
                lineWidth += childWidth;
                hangChildViews.add(childView);
                maxLineHeight = Math.max(maxLineHeight,childHeight);
            }
            //最后一个childView，确定下最后的width，height
            if(i == cCount - 1){
                width = Math.max(width,lineWidth);
                maxLineHeight = Math.max(childHeight,maxLineHeight);
                height = height + maxLineHeight + getPaddingTop() + getPaddingBottom();
                heightList.add(maxLineHeight);
                childViewsList.add(hangChildViews);
            }
        }
        //确定FlowLayout的确切宽高
        setMeasuredDimension(
                (modeWidth == MeasureSpec.EXACTLY  ? sizeWidth : width),
                (modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height));
    }

    /**
     * 根据childViewList和heightList数据，布局子view
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int cLeft;
            int cTop = getPaddingTop();
            int cRight,cBottom;
            for(int i = 0 ; i < childViewsList.size() ; i++){
                ArrayList<View> hangChildView = childViewsList.get(i);
                cLeft = getPaddingLeft();
                for(int j = 0 ; j < hangChildView.size() ; j++){
                    View childView = hangChildView.get(j);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    cTop += lp.topMargin;
                    cLeft += lp.leftMargin;
                    cRight = cLeft + childView.getMeasuredWidth() + lp.rightMargin;
                    cBottom = cTop + childView.getMeasuredHeight() + lp.bottomMargin;
                    childView.layout(cLeft,cTop,cRight,cBottom);
                    Log.e(TAG, "onLayout: cLeft = " + cLeft + ", cTop = " + cTop
                            + ", cRight = " + cRight + ", cBottom = " + cBottom );
                    cLeft += childView.getMeasuredWidth() + lp.rightMargin;
                }
                cTop += heightList.get(i);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }
}

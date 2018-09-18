package com.jimmy.peripheral;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class DragViewGroup extends LinearLayout {
    private static final String TAG = "TestViewGroup";

    // 记录手指上次触摸的坐标
    private float mLastPointX;
    private float mLastPointY;

    //用于识别最小的滑动距离
    private int mSlop;
    // 用于标识正在被拖拽的 child，为 null 时表明没有 child 被拖拽
    private View mDragView;

    // 状态分别空闲、拖拽两种
    enum State {
        IDLE,
        DRAGGING
    }

    State mCurrentState;

    public DragViewGroup(Context context) {
        this(context,null);
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }


    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSlop = ViewConfiguration.getWindowTouchSlop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                if ( isPointOnViews(event)) {
                    //标记状态为拖拽，并记录上次触摸坐标
                    mCurrentState = State.DRAGGING;
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getX() - mLastPointX);
                int deltaY = (int) (event.getY() - mLastPointY);
                if (mCurrentState == State.DRAGGING && mDragView != null
                        && (Math.abs(deltaX) > mSlop || Math.abs(deltaY) > mSlop)) {
                    //如果符合条件则对被拖拽的 child 进行位置移动
                    ViewCompat.offsetLeftAndRight(mDragView,deltaX);
                    ViewCompat.offsetTopAndBottom(mDragView,deltaY);
                    mLastPointX = event.getX();
                    mLastPointY = event.getY();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if ( mCurrentState == State.DRAGGING ){
                    // 标记状态为空闲，并将 mDragView 变量置为 null
                    mCurrentState = State.IDLE;
                    mDragView = null;
                }
                break;
        }
        return true;
    }

    /**
     * 判断触摸的位置是否落在 child 身上
     *
     * */
    private boolean isPointOnViews(MotionEvent ev) {
        boolean result = false;
        Rect rect = new Rect();
        for (int i = 0;i < getChildCount();i++) {
            View view = getChildAt(i);
            rect.set((int)view.getX(),(int)view.getY(),(int)view.getX()+(int)view.getWidth()
                ,(int)view.getY()+view.getHeight());

            if (rect.contains((int)ev.getX(),(int)ev.getY())){
                //标记被拖拽的child
                mDragView = view;
                result = true;
                break;
            }
        }

        return  result && mCurrentState != State.DRAGGING;
    }
}
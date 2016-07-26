package com.gyh.myapplication2;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by user on 2016/7/25.
 */
public class SlideCutListView extends ListView {
    private int screenWidth;
    private int downX,downY;
    private Scroller scroller;
    private boolean isSlide;


    private static final int SNAP_VELOCITY = 600;//设定的快速滑动的速度临界值
    private View itemView;
    /*当前滑动的listview position*/
    private int slidePosition;
    /*认为是用户滑动的最小距离*/
    private int mTouchSlop;
    private VelocityTracker velocityTracker;
    private RemoveListener mRemoveListener;

    public interface RemoveListener {
        public void removeItem(RemoveDirection direction, int position);
    }

    private RemoveDirection removeDirection;

    public enum RemoveDirection {
        RIGHT, LEFT;
    }

    public SlideCutListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public SlideCutListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置滑动删除的回调接口
     *
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addVelocityTracker(event);
                if (!scroller.isFinished()){
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();
                slidePosition = pointToPosition(downX, downY);
                /*无效的position 不做任何处理*/
                if(slidePosition == AdapterView.INVALID_POSITION){
                    return super.dispatchTouchEvent(event);
                }
                /*获取点击的item view*/
                itemView = getChildAt(slidePosition-getFirstVisiblePosition());
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(getScrollVelocity())>SNAP_VELOCITY||(Math.abs(event.getX() - downX) > mTouchSlop
                        && Math.abs(event.getY() - downY) < mTouchSlop)){
                    isSlide = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */
    private  void scrollRight(){
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + itemView.getScrollX());
        Log.i("gaouihao","scrollRight delta = "+delta);
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(),0,-delta,0,Math.abs(delta));
        postInvalidate();//刷新itemview
    }
    /**
     * 向左滑动，根据上面我们知道向左滑动为正值
     */
    private void scrollLeft() {
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        // 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0, Math.abs(delta));
        postInvalidate(); // 刷新itemView
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        // 如果向左滚动的距离大于屏幕的三分之一，就让其删除
        if (itemView.getScrollX() >= screenWidth / 3) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 3) {
            scrollRight();
        } else {
            // 滚回到原始位置,为了偷下懒这里是直接调用scrollTo滚动
            itemView.scrollTo(0, 0);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int deltaX = downX - x;
                    downX = x;

                    // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                    itemView.scrollBy(deltaX, 0);
                    break;
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();
                    // 手指离开的时候就不响应左右滚动
                    isSlide = false;
                    break;
            }

            return true; // 拖动的时候ListView不滚动
        }

        //否则直接交给ListView来处理onTouchEvent事件
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();

            // 滚动动画结束的时候调用回调接口
            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }

                itemView.scrollTo(0, 0);
                mRemoveListener.removeItem(removeDirection, slidePosition);
            }
        }
    }



    private void addVelocityTracker(MotionEvent event ){
        if(velocityTracker ==null){
                velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }
    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     *
     *
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return (int) velocityTracker.getXVelocity();
    }
    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }
}

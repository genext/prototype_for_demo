
package com.example.proto.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;


public final class HorizontalPager extends ViewGroup {

	public static interface OnScreenSwitchListener {        
        public void onScreenSwitched(int screen);
        
    }
    private static final int ANIMATION_SCREEN_SET_DURATION_MILLIS = 1000;

    private static final int FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE = 4;
    private static final int INVALID_SCREEN = -1;

    private static final int SNAP_VELOCITY_DIP_PER_SECOND = 600;

    private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;

    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
    private static final int TOUCH_STATE_VERTICAL_SCROLLING = -1;
    private int currentScreen;
    private int currentView;
    private int densityAdjustedSnapVelocity;
    private boolean firstLayout = true;
    private float lastMotionX;
    private float lastMotionY;
    private OnScreenSwitchListener onScreenSwitchListener;
    private int maximumVelocity;
    private int nextScreen = INVALID_SCREEN;
    private Scroller scroller;
    private int touchSlop;
    private int touchState = TOUCH_STATE_REST;
    private VelocityTracker velocityTracker;
    private int lastSeenLayoutWidth = -1;

    public HorizontalPager(final Context context) {
        super(context);
        init();
    }
    
    public HorizontalPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

   
    private void init() {
        scroller = new Scroller(getContext());

        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displayMetrics);
        densityAdjustedSnapVelocity =
                (int) (displayMetrics.density * SNAP_VELOCITY_DIP_PER_SECOND);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }
    
    //private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @SuppressWarnings("deprecation")
	@SuppressLint("DrawAllocation")
	@Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
        }

       
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        	//getChildAt(i).setLayoutParams(params);
        }

        if (firstLayout) {
            scrollTo(currentScreen * width, 0);
            firstLayout = false;
        }

        else if (width != lastSeenLayoutWidth) { 
        	
        	Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	int displayWidth = 0;
        	
        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
        		displayWidth = display.getWidth();
        	}else{
        		final Point size = new Point();
        		display.getSize(size);
        		displayWidth = size.x;
        	}         
            

            nextScreen = Math.max(0, Math.min(getCurrentScreen(), getChildCount() - 1));
            final int newX = nextScreen * displayWidth;
            final int delta = newX - getScrollX();

            scroller.startScroll(getScrollX(), 0, delta, 0, 0);
        }

        lastSeenLayoutWidth   = width;
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r,
            final int b) {
        int childLeft = 0;
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
       
        final int action = ev.getAction();
        boolean intercept = false;

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                
                if (touchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    
                    intercept = true;
                } else if (touchState == TOUCH_STATE_VERTICAL_SCROLLING) {
                    
                    intercept = false;
                } else { 

                    final float x = ev.getX();
                    final int xDiff = (int) Math.abs(x - lastMotionX);
                    boolean xMoved = xDiff > touchSlop;

                    if (xMoved) {
                       
                        touchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                        lastMotionX = x;
                    }

                    final float y = ev.getY();
                    final int yDiff = (int) Math.abs(y - lastMotionY);
                    boolean yMoved = yDiff > touchSlop;

                    if (yMoved) {
                        touchState = TOUCH_STATE_VERTICAL_SCROLLING;
                    }
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
               
                touchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_DOWN:
                
                lastMotionY = ev.getY();
                lastMotionX = ev.getX();
                break;
            default:
                break;
            }

        return intercept;
    }

	@Override
    public boolean onTouchEvent(final MotionEvent ev) {

        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }

               
                lastMotionX = x;

                if (scroller.isFinished()) {
                    touchState = TOUCH_STATE_REST;
                } else {
                    touchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - lastMotionX);
                boolean xMoved = xDiff > touchSlop;

                if (xMoved) {
                   
                    touchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                }

                if (touchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    // Scroll to follow the motion event
                    final int deltaX = (int) (lastMotionX - x);
                    lastMotionX = x;
                    final int scrollX = getScrollX();

                    if (deltaX < 0) {
                        if (scrollX > 0) {
                        	scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll =
                                getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();

                        if (availableToScroll > 0) {
                        	scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                if (touchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    final VelocityTracker velocityTracker = this.velocityTracker;
                    velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND,
                            maximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > densityAdjustedSnapVelocity && currentScreen > 0) {
                       
                        snapToScreen(currentScreen - 1);
                    } else if (velocityX < -densityAdjustedSnapVelocity
                            && currentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(currentScreen + 1);
                    } else {
                        snapToDestination();
                    }

                    if (this.velocityTracker != null) {
                    	this.velocityTracker.recycle();
                    	this.velocityTracker = null;
                    }
                }

                touchState = TOUCH_STATE_REST;

                break;
            case MotionEvent.ACTION_CANCEL:
                if (touchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    final VelocityTracker velocityTracker = this.velocityTracker;
                    velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND,
                            maximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > densityAdjustedSnapVelocity && currentScreen > 0) {
                        
                        snapToScreen(currentScreen - 1);
                    } else if (velocityX < -densityAdjustedSnapVelocity
                            && currentScreen < getChildCount() - 1) {
                        // Fling hard enough to move right
                        snapToScreen(currentScreen + 1);
                    } else {
                        snapToDestination();
                    }

                    if (this.velocityTracker != null) {
                    	this.velocityTracker.recycle();
                    	this.velocityTracker = null;
                    }
                }
                
                touchState = TOUCH_STATE_REST;
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        } else if (nextScreen != INVALID_SCREEN) {
            currentScreen = Math.max(0, Math.min(nextScreen, getChildCount() - 1));

            
            if (onScreenSwitchListener != null) {
                onScreenSwitchListener.onScreenSwitched(currentScreen);
            }

            nextScreen = INVALID_SCREEN;
        }
    }
    
    public int getCurrentView(){
    	return currentView;
    }

    public void setCurrnetView(int currentView){
    	if(currentView < 0){
    		currentView = 0;
    	}
    	final View child = getChildAt(currentView);
    	child.setSelected(true);
    }
    
    public int getCurrentScreen() {
        return currentScreen;
    }

   
    public void setCurrentScreen(final int currentScreen, final boolean animate) {
        this.currentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
        if (animate) {
            snapToScreen(currentScreen, ANIMATION_SCREEN_SET_DURATION_MILLIS);
        } else {
            scrollTo(this.currentScreen * getWidth(), 0);
        }
        invalidate();
        
        if(onScreenSwitchListener != null)
        	onScreenSwitchListener.onScreenSwitched(this.currentScreen);
    }

    
    public void setOnScreenSwitchListener(final OnScreenSwitchListener onScreenSwitchListener) {
        this.onScreenSwitchListener = onScreenSwitchListener;
    }

   
    private void snapToDestination() {
        final int screenWidth = getWidth();
        int scrollX = getScrollX();
        int whichScreen = currentScreen;
        int deltaX = scrollX - (screenWidth * currentScreen);

        // Check if they want to go to the prev. screen
        if ((deltaX < 0) && currentScreen != 0
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < -deltaX)) {
            whichScreen--;
            
        } else if ((deltaX > 0) && (currentScreen + 1 != getChildCount())
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < deltaX)) {
            whichScreen++;
        }

        snapToScreen(whichScreen);
    }

    
    private void snapToScreen(final int whichScreen) {
        snapToScreen(whichScreen, -1);
    }

    
    private void snapToScreen(final int whichScreen, final int duration) {
      
        nextScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        final int newX = nextScreen * getWidth();
        final int delta = newX - getScrollX();

        if (onScreenSwitchListener != null) {
            onScreenSwitchListener.onScreenSwitched(nextScreen);
        }
        
        if (duration < 0) {             
            scroller.startScroll(getScrollX(), 0, delta, 0, (int) (Math.abs(delta)
                    / (float) getWidth() * ANIMATION_SCREEN_SET_DURATION_MILLIS));
        } else {
            scroller.startScroll(getScrollX(), 0, delta, 0, duration);
        }

        invalidate();
    }

    
    
}

package com.example.belen.shrimps;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class MyCanvasView extends View implements OnTouchListener {

    private Canvas  mCanvas; //drawCanvas
    private Path    mPath; //drawPath
    private Paint       mPaint,canvasPaint; //drawPaint
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> undonePaths = new ArrayList<>();
    public Bitmap im; //canvasBitmap
    public Bitmap backupBitmap;
    public float mX,mY;
    private float mLastTouchX;
    private float mLastTouchY;
    //for zooming
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mScaleX,mScaleY;
    private float mPosX;
    private float mPosY;
    boolean zoomStatus;


    //These two constants specify the minimum and maximum zoom
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private boolean dragged;
    private ScaleGestureDetector detector;

    //These constants specify the mode that we're in
    private static int NONE = 0;
    private static int DRAG = 1;
    private static int ZOOM = 2;
    private int displayWidth = PhotoActivity.new_width;
    private int displayHeight = PhotoActivity.new_height;
    private int mode;

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    private float startX = 0f;
    private float startY = 0f;

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    private float translateX = 0f;
    private float translateY = 0f;

    //These two variables keep track of the amount we translated the X and Y coordinates, the last time we
    //panned.
    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;



    //private ScaleGestureDetector scaleDetector; //not before
    //private float scaleFactor = 1.f; //


    public MyCanvasView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        setupDrawing();
        mCanvas = new Canvas();
        //for zooming
        //mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        detector = new ScaleGestureDetector(context, new ScaleListener());
        this.zoomStatus = false;

    }

    public MyCanvasView(Context context, AttributeSet attrs, Bitmap bitmap)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        setupDrawing();
        mCanvas = new Canvas();
        this.im = bitmap;
        //for zoming
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.zoomStatus = false;
    }

    public void setupDrawing(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        mPath = new Path();
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.im!=null) {
            mCanvas = new Canvas(this.im);
        }

    }

    @Override
        protected void onDraw(Canvas canvas) {
            //super.onDraw(canvas);
            //canvas.scale(scaleFactor, scaleFactor);

            super.onDraw(canvas);

            canvas.save();

            //We're going to scale the X and Y coordinates by the same amount
            canvas.scale(mScaleFactor, mScaleFactor);

            //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
            if((translateX * -1) < 0) {
                translateX = 0;
            }

            //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
            //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of
            //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
            //as doing -1 * (scaleFactor - 1) * displayWidth
            else if((translateX * -1) > (mScaleFactor - 1) * displayWidth) {
                translateX = (1 - mScaleFactor) * displayWidth;
            }

            if(translateY * -1 < 0) {
                translateY = 0;
            }

            //We do the exact same thing for the bottom bound, except in this case we use the height of the display
            else if((translateY * -1) > (mScaleFactor - 1) * displayHeight) {
                translateY = (1 - mScaleFactor) * displayHeight;
            }

            //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
            //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
            canvas.translate(translateX / mScaleFactor, translateY / mScaleFactor);

        if(this.im!= null ){
            //canvas.translate(mPosX, mPosY);
            System.out.println("POSICIONES DE ESCALA "+ mPosX+" "+mPosY);
            //canvas.scale(mScaleFactor, mScaleFactor);
            canvas.drawBitmap(this.im, 0, 0, canvasPaint);
            //canvas.drawPath(mPath,mPaint);
            for (FingerPath p: paths){
                mPaint.setColor(p.color);
                mPaint.setStrokeWidth(p.strokeWidth);
                canvas.drawPath(p.path,mPaint);
            }
        }
        canvas.restore();


    }

    //private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        //mPath.reset(); //was before
        mPath = new Path();
        FingerPath fp = new FingerPath(mPaint.getColor(),6, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen


    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        if(!zoomStatus){

                float x = (event.getX()-translateX)/mScaleFactor;
                float y = (event.getY()-translateY)/mScaleFactor;
                //float x0=x-mScaleX;
                //float y0=y-mScaleY;
                //x0=x0*mScaleFactor;
                //y0=y0*mScaleFactor;
                //x0+=mScaleX;
                //y0+=mScaleY;
                //System.out.println("Pixel coordinates "+ x0+" "+y0);
                System.out.println("touch co-ordinates "+ x+" "+y);
                System.out.println("pivot points "+ mScaleX+" "+mScaleY);
                System.out.println("Scalefactor "+ Float.toString(mScaleFactor));
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up();
                        break;
                    default:
                        return false;
                }
                invalidate();
                return true;

        }
        else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    mode = DRAG;

                    //We assign the current X and Y coordinate of the finger to startX and startY minus the previously translated
                    //amount for each coordinates This works even when we are translating the first time because the initial
                    //values for these two variables is zero.
                    startX = event.getX() - previousTranslateX;
                    startY = event.getY() - previousTranslateY;
                    mActivePointerId = event.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    translateX = event.getX() - startX;
                    translateY = event.getY() - startY;

                    //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                    //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                    double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) +
                            Math.pow(event.getY() - (startY + previousTranslateY), 2)
                    );

                    if(distance > 0) {
                        dragged = true;
                    }
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = ZOOM;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = NONE;
                    dragged = false;

                    //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                    //previousTranslate
                    previousTranslateX = translateX;
                    previousTranslateY = translateY;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode = DRAG;

                    //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                    //and previousTranslateY when the second finger goes up
                    previousTranslateX = translateX;
                    previousTranslateY = translateY;
                    break;
            }
        }

        detector.onTouchEvent(event);

        //We redraw the canvas only in the following cases:
        //
        // o The mode is ZOOM
        //        OR
        // o The mode is DRAG and the scale factor is not equal to 1 (meaning we have zoomed) and dragged is
        //   set to true (meaning the finger has actually moved)
        if ((mode == DRAG && mScaleFactor != 1f && dragged) || mode == ZOOM) {
            invalidate();
        }

        return true;
        /*mScaleDetector.onTouchEvent(event);
        if(this.zoomStatus){
            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    final float x = event.getX();
                    final float y = event.getY();

                    mLastTouchX = x;
                    mLastTouchY = y;
                    mActivePointerId = event.getPointerId(0);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final int pointerIndex = event.findPointerIndex(mActivePointerId);
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    // Only move if the ScaleGestureDetector isn't processing a gesture.
                    if (!mScaleDetector.isInProgress()) {
                        final float dx = x - mLastTouchX;
                        final float dy = y - mLastTouchY;

                        mPosX += dx;
                        mPosY += dy;

                        invalidate();
                    }

                    mLastTouchX = x;
                    mLastTouchY = y;

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                            >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = event.getX(newPointerIndex);
                        mLastTouchY = event.getY(newPointerIndex);
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                }
            }

            return true;

        }
        else {
            float x = event.getX();
            float y = event.getY();
            //float x0=x-mScaleX;
            //float y0=y-mScaleY;
            //x0=x0*mScaleFactor;
            //y0=y0*mScaleFactor;
            //x0+=mScaleX;
            //y0+=mScaleY;
            //System.out.println("Pixel coordinates "+ x0+" "+y0);
            System.out.println("touch co-ordinates "+ x+" "+y);
            System.out.println("pivot points "+ mScaleX+" "+mScaleY);
            System.out.println("Scalefactor "+ Float.toString(mScaleFactor));
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        }
        */
    }

    public void onClickUndo(){
        if (paths.size() > 0){
            System.out.println("undooooo");
            // End current path
            // Cancel the last one and redraw
            undonePaths.add(paths.get(paths.size() - 1));
            paths.remove(paths.size() - 1);
            invalidate();
        }

    }

    public void onClickRedo(){
        if (undonePaths.size() > 0){
            // End current path
            // Cancel the last one and redraw
            paths.add(undonePaths.get(undonePaths.size() - 1));
            undonePaths.remove(undonePaths.size() - 1);
            invalidate();
        }

    }


    public void setBitmap(Bitmap im){
        Bitmap mutableBitmap = im.copy(Bitmap.Config.ARGB_8888, true);
        this.im = mutableBitmap;
    }

    public void setColor(String newColor){
    //set color
        int paintColor = Color.parseColor(newColor);
        this.mPaint.setColor(paintColor);


    }

    /*
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleX=detector.getFocusX();
            mScaleY=detector.getFocusY();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 5.0f));
            invalidate();
            return true;
        }
    }*/

    public void setZoomStatus(boolean status){
        this.zoomStatus = status;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
            return true;
        }
    }




}
package com.example.belen.shrimps;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;



public class MyCanvasView extends View implements OnTouchListener {

    private Canvas  mCanvas; //drawCanvas
    private Path    mPath; //drawPath
    private Paint       mPaint,canvasPaint; //drawPaint
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> undonePaths = new ArrayList<>();
    public Bitmap im; //canvasBitmap
    public float mX,mY, scale;
    private float mScaleFactor = 1.f;
    boolean zoomStatus;
    float lastTouchX, lastTouchY;


    //These two constants specify the minimum and maximum zoom
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    //These constants specify the mode that we're in
    private static int DRAG = 1;
    ScaleGestureDetector detector, mScaleDetector;
    //Zoom & pan touch event
    int y_old=0,y_new=0;int zoomMode=0;
    float pinch_dist_old=0,pinch_dist_new=0;
    int zoomControllerScale=1;//new and old pinch distance to determine Zoom scale
    // These matrices will be used to move and zoom image
    Matrix matrix, savedMatrix;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int PAN = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    public MyCanvasView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        setupDrawing();
        mCanvas = new Canvas();
        this.matrix = new Matrix();
        this.savedMatrix = new Matrix();
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
        this.matrix = new Matrix();
        this.savedMatrix = new Matrix();
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
        protected void onDraw(Canvas canvas) {
        canvas.save();
        if(this.im!= null ){

            canvas.drawBitmap(this.im, matrix, canvasPaint);
            canvas.concat(matrix);
            //canvas.drawPath(mPath,mPaint);
            for (FingerPath p: paths){
                mPaint.setColor(p.color);
                mPaint.setStrokeWidth(p.strokeWidth/mScaleFactor);
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
        mPath.transform(this.matrix);
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


    public boolean onTouch(View arg0, MotionEvent event){

        if(this.zoomStatus){

            PanZoomWithTouch(event);
            invalidate();//necessary to repaint the canvas
            return true;
        }
        else{
            int[] loc = new int[2];
            //arg0.getLocationOnScreen(loc);
            float []m = new float[9];
            matrix.getValues(m);
            float transX = m[Matrix.MTRANS_X] * -1;
            float transY = m[Matrix.MTRANS_Y] * -1;
            float scaleX = m[Matrix.MSCALE_X];
            float scaleY = m[Matrix.MSCALE_Y];
            lastTouchX = (int) ((event.getX() + transX) / scaleX);
            lastTouchY = (int) ((event.getY() + transY) / scaleY);
            lastTouchX = Math.abs(lastTouchX);
            lastTouchY = Math.abs(lastTouchY);
            float x= lastTouchX;
            float y = lastTouchY;
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



    }

    void PanZoomWithTouch(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://when first finger down, get first point
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                System.out.println("mode=PAN");
                mode = PAN;
                break;
            case MotionEvent.ACTION_POINTER_DOWN://when 2nd finger down, get second point
                oldDist = spacing(event);
                System.out.println("oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); //then get the mide point as centre for zoom
                    mode = ZOOM;
                    System.out.println("mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:       //when both fingers are released, do nothing
                mode = NONE;
                System.out.println("mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:     //when fingers are dragged, transform matrix for panning
                if (mode == PAN) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x,
                            event.getY() - start.y);
                    System.out.println("Mapping rect");
                    //start.set(event.getX(), event.getY());
                }
                else if (mode == ZOOM) { //if pinch_zoom, calculate distance ratio for zoom
                    float newDist = spacing(event);
                    System.out.println("newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        // ...
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }



    public void onClickUndo(){
        if (paths.size() > 0){
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


    public void setZoomStatus(boolean status){
        this.zoomStatus = status;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }



}
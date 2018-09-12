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

public class MyCanvasView extends View implements OnTouchListener {

    private Canvas  mCanvas; //drawCanvas
    private Path    mPath; //drawPath
    private Paint       mPaint,canvasPaint; //drawPaint
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> undonePaths = new ArrayList<>();
    public Bitmap im; //canvasBitmap
    public Bitmap backupBitmap;
    public float mX,mY;

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
        //scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        //paths.add(this.mPath); //was not before

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
        if(this.im!= null ){
            System.out.println("ESTOY DIBUJANDO EL BITMAP");
            canvas.drawBitmap(this.im, 0, 0, canvasPaint);
            //canvas.drawPath(mPath,mPaint);
            for (FingerPath p: paths){
                mPaint.setColor(p.color);
                mPaint.setStrokeWidth(p.strokeWidth);
                canvas.drawPath(p.path,mPaint);
            }
        }


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
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
             touch_start(x,y);
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

    public void onClickUndo () {
        System.out.println("TAMAÑO DE PATHS: "+paths.size());
        if (paths.size() > 0) {
            // End current path
            // Cancel the last one and redraw
            undonePaths.add(paths.get(paths.size() - 1));
            paths.remove(paths.size() - 1);
            invalidate();
        }

    }

    public void onClickRedo (){


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




}
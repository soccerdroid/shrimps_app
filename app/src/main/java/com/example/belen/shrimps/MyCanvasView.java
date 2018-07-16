package com.example.belen.shrimps;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MyCanvasView extends View implements OnTouchListener {
    private Canvas  mCanvas; //drawCanvas
    private Path    mPath; //drawPath
    private Paint       mPaint,canvasPaint; //drawPaint
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();
    public Bitmap im; //canvasBitmap


    public MyCanvasView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        setupDrawing();
        mCanvas = new Canvas();


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
            mCanvas = new Canvas(this.im); //was not before
        }
        System.out.println("ENTRE AL ONSIZECHANGED");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        System.out.println("CANVAS SIZE"+canvas.getWidth()+" "+canvas.getHeight());
        if(this.im!= null ){
            System.out.println("ESTOY DIBUJANDO EL BITMAP");
            canvas.drawBitmap(this.im, 0, 0, canvasPaint); //before was null
            canvas.drawPath(mPath, mPaint); //was not before
        }
        /*for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }*/
        //canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
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
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        paths.add(mPath);
        mPath.reset();


    }

    public void onClickUndo () {
        if (paths.size()>0)
        {
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }

    }

    public void onClickRedo (){
        if (undonePaths.size()>0)
        {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }
        else
        {

        }

    }

    public void setBitmap(Bitmap im){
        Bitmap mutableBitmap = im.copy(Bitmap.Config.ARGB_8888, true); //was not before
        this.im = mutableBitmap;

    }
    public void setColor(String newColor){
    //set color
        invalidate();  //was not before
        int paintColor = Color.parseColor(newColor);
        this.mPaint.setColor(paintColor);

    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                //invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                //invalidate();
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
    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec){

        super.setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

    }

    private int measureWidth(int measureSpec) {
        int preferred = this.im.getWidth() * 2;
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec) {
        int preferred = this.im.getHeight() * 2;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred) {
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement = 0;
        switch(MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                // This means the width of this view has been given.
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                // Take the minimum of the preferred size and what
                // we were told to be.
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }

        return measurement;
    }



}
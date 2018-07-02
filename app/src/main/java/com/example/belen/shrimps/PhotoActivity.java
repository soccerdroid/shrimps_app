package com.example.belen.shrimps;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Path;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.SocketOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity  {
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    public static FTPClient ftp;
    TextView thumbnail_name;
    Button backBtn;
    ImageView imageView;
    Canvas tempCanvas;
    //Paint paint;
    //MyCanvasView myCanvasView;
    Paint mPaint;
    Path mPath;
    float mX, mY;
    static final float TOUCH_TOLERANCE = 4;
    Display display;
    Point size ;
    //int screen_width, screen_height;
    //int image_height, image_width;
    //float scale;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    MyCanvasView myCanvasView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Bundle b = getIntent().getExtras();
        //Get passed file name
        String name = (String) b.get("name");
        thumbnail_name = findViewById(R.id.thumbnail_name_tv);
        //Set image view and button
        //imageView = findViewById(R.id.iv);
        //myCanvasView = findViewById(R.id.my_canvas_view); AQUI RECOJO EL CANVAS VIEW DE LAYOUT
        //myCanvasView = new MyCanvasView(this);
        //setContentView(myCanvasView);
        backBtn = findViewById(R.id.back_btn);
        thumbnail_name.setText(name);
        this.ftp = MainActivity.ftp;
        addListenerOnButton();
        //Try to open image
        Bitmap bitmap = null;
        //display = getWindowManager().getDefaultDisplay();
        size = new Point();
        //display.getSize(size);
        //screen_width = size.x;
        //screen_height = size.y;
        //scale = getResources().getDisplayMetrics().density;


        try {

            //System.out.println("FILENAME: "+name);
            InputStream input = this.ftp.retrieveFileStream(name);
            BufferedInputStream buf = new BufferedInputStream(input);
            bitmap = BitmapFactory.decodeStream(buf);
            //image_height = bitmap.getHeight();
            //image_width = bitmap.getWidth();
            //int iv_width = imageView.getWidth();
            //System.out.println("IV WIDTH: "+iv_width);
            //System.out.println("Image size: "+image_width+","+image_height);
            //System.out.println("Screen size: "+screen_width+","+screen_height);
            //paint = new Paint();
            //paint.setColor(Color.WHITE);
            mPath = new Path();
            mPaint = initPaint();
            //Bitmap tempBitmap = Bitmap.createBitmap(image_width, image_height,Bitmap.Config.ARGB_8888);
            //Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // No XML file; just one custom view created programmatically.
            myCanvasView = new MyCanvasView(this,null,bitmap);
            myCanvasView.setBitmap(bitmap);
            LinearLayout layout = (LinearLayout) findViewById(R.id.photo_activity_layout);
            layout.addView(myCanvasView);

            //setContentView(myCanvasView);
            buf.close();
            input.close();
            if(!this.ftp.completePendingCommand()) {
                this.ftp.logout();
                this.ftp.disconnect();
                System.err.println("File transfer failed.");
                finish();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void addListenerOnButton(){
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    if (ftp.isConnected()) {
                        ftp.logout();
                        ftp.disconnect();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                finish();
            }
        });

    }

    public Paint initPaint(){
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE); // default: FILL
        mPaint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        mPaint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        mPaint.setStrokeWidth(12); // default: Hairline-width (really thin)
        return mPaint;
    }

    public void touchStart(float x, float y) {
        mPath.reset();//added line
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            // Reset mX and mY to the last drawn point.
            mX = x;
            mY = y;
            // Save the path in the extra bitmap,
            // which we access through its canvas.
            tempCanvas.drawPath(mPath, mPaint);
        }
    }

    public void touchUp() {
// Reset the path so it doesn't get drawn again.
        paths.add(mPath);
        mPath = new Path(); //added line
    }




}

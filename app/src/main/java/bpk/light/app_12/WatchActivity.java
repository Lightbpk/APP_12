package bpk.light.app_12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class WatchActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{
    SurfaceView sw, drawSW;
    SurfaceHolder surfaceHolder, drawSWHolder;
    Camera mCamera;
    DrawThread drawThread;
    Bitmap bmp;
    int dw, dh;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_watch);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dw = size.x;
        dh = size.y;
        sw = findViewById(R.id.camSurface);
        drawSW = findViewById(R.id.drawSurface);
        surfaceHolder  = sw.getHolder();
        drawSWHolder = drawSW.getHolder();
        try{
            mCamera = Camera.open();
            Log.d("LightLog","Camera Open successful");
        }
        catch(Exception e){
            e.printStackTrace();
            Log.d("LightLog","Camera Open fail");
        }
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.d("LightLog","Frame geted");
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        Rect rect = new Rect(0, 0, width, height);
        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21,width,height,null);
        yuvimage.compressToJpeg(rect, 100, outstr);
        bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
        drawThread = new DrawThread(drawSWHolder);
        drawThread.setRunning(true);
        if(drawThread.getRunning()) {
            drawThread.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("LightLog","Surface created successful");
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            Log.d("LightLog","preview start successful");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("LightLog","preview start fail");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d("LightLog","Surface change");
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
            Log.d("LightLog","preview start successful");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("LightLog","preview start fail");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.release();
        drawThread.setRunning(false);
        Log.d("LightLog","Destoyed");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mCamera.release();
        drawThread.setRunning(false);
        Log.d("LightLog","Back pressed");
    }

    class DrawThread extends Thread {

        private boolean running = false;
        private SurfaceHolder surfaceHolder;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
        public boolean getRunning(){
            return running;
        }

        @Override
        public void run() {
            Canvas canvas;
            Paint pD,pS;
            Path path;
            Bitmap bitmap, half, coled;
            Log.d("LightLog","Drawing start");
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    Log.d("LightLog","Canvas locked");
                    if (canvas == null) {
                        Log.d("LightLog","Canvas null");
                        continue;
                    }
                    else Log.d("LightLog","Canvas not Null");
                        Matrix mScale = new Matrix();
                        mScale.setScale((float) 0.2, (float) 0.2);
                        bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mScale, true);
                        half = getCroppedBitmap(bitmap, true);
                        canvas.drawBitmap(half, 0, 0, null);
                        Matrix matrix = new Matrix();
                        matrix.preScale(1.0f, -1.0f);
                        matrix.setRotate(-180);
                        coled = Bitmap.createBitmap(half, 0, 0, half.getWidth(), half.getHeight(), matrix, true);
                        canvas.drawBitmap(coled, 0, 0, null);
                        Log.d("LightLog", "Drawing Green");

                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
    private Bitmap getCroppedBitmap(Bitmap bitmap,boolean tf) {
        Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path;
        final int color = 0xff424242;
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        path = new Path();
        path.reset();
        if(tf){
        path.moveTo(0,0);
        path.lineTo(bitmap.getWidth(),bitmap.getHeight());
        path.lineTo(bitmap.getWidth(),0);
        }
        else{
            path.moveTo(0,0);
            path.lineTo(0,bitmap.getHeight());
            path.lineTo(bitmap.getWidth(),bitmap.getHeight());
        }
        path.close();
        canvas.drawPath(path,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return targetBitmap;
    }
}




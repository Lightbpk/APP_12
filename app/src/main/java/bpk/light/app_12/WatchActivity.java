package bpk.light.app_12;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WatchActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    SurfaceView cameraView,drawView;
    SurfaceHolder camHolder,drawHolder;
    Camera camera;
    Bitmap bmp;
    DrawView dw;
    MyTask mt;
    String LL ="LightLog";
    int  deviceHeight,deviceWidth;
    private float RectLeft, RectTop,RectRight,RectBottom ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_watch);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        cameraView = findViewById(R.id.camSurface);
        camHolder = cameraView.getHolder();
        camHolder.addCallback(this);
        try {camera = Camera.open();
            Log.d(LL,"Cam open");}
        catch (Exception e){Log.d(LL,"Camopen err");}
        dw = new DrawView(this, bmp);
        //setContentView(dw = new DrawView(this, bmp));
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(LL,"surfaceCreated");
        //setContentView(dw = new DrawView(this));
    }

    public void surfaceChanged(SurfaceHolder sh, int format, int w, int h) {
        Log.d(LL,"surfaceChanged");
        //
        try {
            camera.setPreviewDisplay(sh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setPreviewCallback(this);
        camera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(LL,"surfaceKilled");
    }

    @Override
    protected void onPause(){
        super.onPause();
        camera.release();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.d(LL,"new Frame");
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        ByteArrayOutputStream outstr = new ByteArrayOutputStream();
        Rect rect = new Rect(0, 0, width, height);
        YuvImage yuvimage=new YuvImage(bytes, ImageFormat.NV21,width,height,null);
        yuvimage.compressToJpeg(rect, 100, outstr);
        bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
        //setContentView(dw);
        mt = new MyTask();
        mt.execute();
    }


    class MyTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
                //setContentView(dw);
                dw.upDate(bmp);
            return null;
        }
    }
   /* private void Draw()
    {
        Canvas canvas = drawHolder.lockCanvas(null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        RectLeft = 1;
        RectTop = 200 ;
        RectRight = RectLeft+ deviceWidth-100;
        RectBottom =RectTop+ 200;
        Rect rec=new Rect((int) RectLeft,(int)RectTop,(int)RectRight,(int)RectBottom);
        canvas.drawRect(rec,paint);
        canvas.drawBitmap(bmp,0,0,null);
        drawHolder.unlockCanvasAndPost(canvas);
    }*/
    /*@Override

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            synchronized (holder)
            {
                Draw();
            }
            camera = Camera.open();
        }

        catch (Exception e){
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        //фонарь- зачем фонарь? param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        }
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (camHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }
        try {
            camera.setPreviewDisplay(camHolder);
            camera.startPreview();
        }

        catch (Exception e) {

        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.release();
    }
    private void Draw()

    {

        Canvas canvas = drawHolder.lockCanvas(null);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(Color.GREEN);

        paint.setStrokeWidth(3);

        RectLeft = 1;

        RectTop = 200 ;

        RectRight = RectLeft+ deviceWidth-100;

        RectBottom =RectTop+ 200;

        Rect rec=new Rect((int) RectLeft,(int)RectTop,(int)RectRight,(int)RectBottom);

        canvas.drawRect(rec,paint);

        drawHolder.unlockCanvasAndPost(canvas);



    }*/

}


package bpk.light.app_12;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class WatchActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{
    SurfaceView sw;
    SurfaceHolder surfaceHolder;
    Camera mCamera;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        sw = findViewById(R.id.camSurface);
        surfaceHolder  = sw.getHolder();
        try{mCamera = Camera.open();}
        catch(Exception e){
            e.printStackTrace();
        }
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.d("LightLog","Frame geted");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.release();
    }
}



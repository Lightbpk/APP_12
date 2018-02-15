package bpk.light.app_12;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by Admin on 12.02.2018.
 */

public class CamSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder surfaceHolder;
    Path path;
    Camera mCamera;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public CamSurfaceView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        mCamera = Camera.open();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            path = new Path();
            path.moveTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            path.lineTo(event.getX(), event.getY());
        }

        if (path != null) {
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawPath(path, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        return true;
    }
   public void setCameraDisplayOrientation() {
       if (mCamera == null)
           return;

       Camera.CameraInfo info = new Camera.CameraInfo();
       Camera.getCameraInfo(0, info);

       WindowManager winManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
       int rotation = winManager.getDefaultDisplay().getRotation();
       Log.d("LightLog"," "+ rotation);
       int degrees = 0;

       switch (rotation) {
           case Surface.ROTATION_0: degrees = 0; break;
           case Surface.ROTATION_90: degrees = 90; break;
           case Surface.ROTATION_180: degrees = 180; break;
           case Surface.ROTATION_270: degrees = 270; break;
       }

       int result;
       if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
           result = (info.orientation + degrees) % 360;
           result = (360 - result) % 360;
       }
       else {
           result = (info.orientation - degrees + 360) % 360;
       }
       Log.d("LightLog"," "+ result);
       mCamera.setDisplayOrientation(result);
       Camera.Parameters parameters = mCamera.getParameters();
       int rotate = (degrees + 270) % 360;
       parameters.setRotation(rotate);
       mCamera.setParameters(parameters);
       Log.d("LightLog"," "+ rotate);

        /*mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(90);
        mCamera.setParameters(parameters);*/
   }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setCameraDisplayOrientation();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mCamera.stopPreview();
        setCameraDisplayOrientation();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

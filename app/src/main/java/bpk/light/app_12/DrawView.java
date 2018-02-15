package bpk.light.app_12;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

/**
 * Created by Admin on 15.02.2018.
 */

public class DrawView extends View {
    Bitmap bitmap;
    String LL = "LightLog";
    public DrawView(Context context) {
        super(context);
        Log.d(LL,"DW Crasted");
    }
    public void upDate(Bitmap bitmap){
        this.bitmap = bitmap;
        invalidate();
        Log.d(LL,"DW Updated");
    }
    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(bitmap,0,0,null);
        Log.d(LL,"DW Drawed");
    }
}

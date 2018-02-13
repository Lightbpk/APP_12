package bpk.light.app_12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class WatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new CamSurfaceView(this));
    }
}

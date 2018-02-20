package bpk.light.app_12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class WatchActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(new CamSurfaceView(this));
    }
}



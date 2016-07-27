package com.example.agitou.sensordata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    public TextView x, y, z, xdata, ydata, zdata, sped;
    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private MediaPlayer mp, mp2;
    private Button btn1;
    boolean isPlaying = false, loopexit = true;
    private int pos = 0;
    private long start, elapsed_time;
    private final int SPEED_THRESHOLD = 500;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.nextep);

        x = (TextView) findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);

        xdata = (TextView) findViewById(R.id.xdata);
        ydata = (TextView) findViewById(R.id.ydata);
        zdata = (TextView) findViewById(R.id.zdata);
        sped = (TextView) findViewById(R.id.sped);

        btn1 = (Button) findViewById(R.id.btn1);
        mp2 = MediaPlayer.create(this, R.raw.perc);
        btn1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(isPlaying){
                    mp2.pause();
                    mp2.setLooping(false);
                }
                else {
                    mp2.start();
                    mp2.setLooping(true);
                }
                isPlaying = !isPlaying;
            }
        });


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent sensor){
        Sensor mySensor = sensor.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float xn = sensor.values[0];
            float yn = sensor.values[1];
            float zn = sensor.values[2];
            long curTime = System.currentTimeMillis();

            //if ((curTime - lastUpdate) > 10)
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                xdata.setText(String.format("%d", Math.round(xn)));
                ydata.setText(String.format("%d", Math.round(yn)));
                zdata.setText(String.format("%d", Math.round(zn)));

                float speed = Math.abs(xn + yn + zn - last_x - last_y - last_z)/ diffTime * 10000;
                sped.setText(String.format("%d", Math.round(speed)));

                if(speed > SPEED_THRESHOLD && isPlaying == false){
                    mp.start();
                    isPlaying = !isPlaying;
                    start = System.nanoTime();
                }



                if(isPlaying == true && speed <= SPEED_THRESHOLD){
                    elapsed_time = System.nanoTime() - start;
                    if(elapsed_time > 7) {
                        mp.pause();
                        Toast.makeText(this, String.valueOf(isPlaying), Toast.LENGTH_SHORT).show();
                        isPlaying = !isPlaying;
                    }
                }



                last_x = xn;
                last_y = yn;
                last_z = zn;
            }
            /*
            xdata.setText(String.format("%.2f", xn));
            ydata.setText(String.format("%.2f", yn));
            zdata.setText(String.format("%.2f", zn));
            */

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
}

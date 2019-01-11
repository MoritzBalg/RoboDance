package com.example.moritz.android_robot_project;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.moritz.android_robot_project.Enums.Farbe;
import com.example.moritz.android_robot_project.Enums.Mode;
import com.example.moritz.android_robot_project.Enums.Port;
import com.example.moritz.android_robot_project.Enums.RegulationMode;
import com.example.moritz.android_robot_project.Enums.RunState;
import android.hardware.Camera;

import static com.example.moritz.android_robot_project.CameraPreview.getCameraInstance;

public class MainActivity extends AppCompatActivity {
    public USB NXT_USB;
    public NXT nxt;
   // public Motor a,b;
    //public Robot rob;
    public int x;
    private ProgressBar battery;
    private TextView speed;
    private TextView status;
    private TextView textViewKamera;
    private TextView sync;
    private TextView log;
    private SeekBar seekbar ;
    private Handler mainHandler = new Handler();
    private Handler kameraHandler = new Handler();
    private Handler fahrHandler = new Handler();


    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kamera
        mCamera = CameraPreview.getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        mCamera.setPreviewCallback(mPreview);
        //mPreview.onPreviewFrame(a, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        NXT_USB = new USB();
        NXT_USB.init((UsbManager) getSystemService(Context.USB_SERVICE));
        nxt = NXT.getInstance(NXT_USB);

        battery = (ProgressBar)findViewById(R.id.progressBar);
        speed = (TextView) findViewById(R.id.textViewSpeed);
        status = (TextView) findViewById(R.id.textViewStat);
        sync = (TextView) findViewById(R.id.textViewSync);
        textViewKamera = (TextView) findViewById(R.id.textViewKamera);
        log = (TextView) findViewById(R.id.log);
        log.setMovementMethod(new ScrollingMovementMethod());
        seekbar = (SeekBar) findViewById(R.id.seekBar);
      //  seekbar.setProgress(rob.getSpeed());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
             //   rob.setSpeed((byte)progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Tread Management

    }

    protected void onResume() {
        super.onResume();
        NXT_USB.open(getIntent());
    }
    StatusControll sc = new StatusControll();



    public void onPause() {
        super.onPause();
        NXT_USB.close();
    }

    public void onBackPressed() {

        startActivity(new Intent(MainActivity.this,MainActivity.class));
    }


    public void btnStart(View v){
        Fahren fahr = new Fahren();
        fahr.start();
        sc.start();


    }

    public void btnStop(View v){
    }



    class Fahren extends Thread {
        Robot rob;
        Motor a,b;

        public Fahren(){
            rob = new Robot();
            a = new Motor(nxt,Port.PORTA,Mode.MOTORON_BREAK_REGULATED,RegulationMode.SYNC,RunState.RUNNING);
            b = new Motor(nxt,Port.PORTB,Mode.MOTORON_BREAK_REGULATED,RegulationMode.SYNC,RunState.RUNNING);
            rob.setSpeed((byte)40);
            rob.addMotor(a);
            rob.addMotor(b);

        }


        @Override
        public void run() {
           rob.moveFor();

            while (true) {

                        switch (mPreview.isFarbe()){

                            case UNDEFINIERT:
                                break;
                            case GRUEN:
                                rob.stop();
                                break;
                            case ROT_LINKS:
                                rob.moveBack();
                                rob.turn_LINKS((int)(Math.random()*90+40));
                                rob.moveFor();
                                break;
                            case ROT_RECHTS:
                                rob.moveBack();
                                rob.turn_RECHTS((int)(Math.random()*90+40));
                                rob.moveFor();
                               break;
                        }
                                            }
            }


    }

    //
    class StatusControll extends Thread{
        private static final String TAG = "MainActivity";
        @Override
        public void run() {
            while(true){
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        int [] aktuell = new int[3];
                        switch (mPreview.isFarbe()){

                            case UNDEFINIERT:
                                aktuell = mPreview.Farbe(100,100);

                                textViewKamera.setText("undefiniert \nRot:"+aktuell[0] + " Grün:" + aktuell[1] +" Blau:"+aktuell[2]);
                                break;
                            case GRUEN:
                                aktuell = mPreview.Farbe(100,100);

                                textViewKamera.setText("Grün \nRot:"+aktuell[0] + " Grün:" + aktuell[1] +" Blau:"+aktuell[2]);

                                break;
                            case ROT_LINKS:
                                aktuell = mPreview.Farbe(100,100);

                                textViewKamera.setText("Rot_LINKS\nRot:"+aktuell[0] + " Grün:" + aktuell[1] +" Blau:"+aktuell[2]);
                                break;
                            case ROT_RECHTS:
                                aktuell = mPreview.Farbe(100,100);

                                textViewKamera.setText("Rot_RECHTS\nRot:"+aktuell[0] + " Grün:" + aktuell[1] +" Blau:"+aktuell[2]);


                                break;
                        }


                        battery.setProgress(getBattery());
                       // speed.setText(rob.getSpeed() + "%");
                        status.setText(NXT_USB.isConnected()?"aktiv":"inaktiv");
                        sync.setText(Motor.isSync()?"aktiv":"inaktiv");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private int getBattery(){
            int x;
            byte [] bat = nxt.getBatteryLevel();
            x = (int) ((int) bat[3] &0xff)
                    |((int) bat[4] &0xff) <<8;
            if(x >= 7500){return 100;}
            else if(x < 7500 && x > 7400){return 75;}
            else if(x < 7400 && x > 7300){return 50;}
            else if(x < 7300 && x > 7200){return 25;}
            else if(x < 7200 && x > 7100){return 10;}
            return 0;
        }
    }
}



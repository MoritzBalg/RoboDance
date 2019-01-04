package com.example.moritz.android_robot_project;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.moritz.android_robot_project.Enums.Mode;
import com.example.moritz.android_robot_project.Enums.Port;
import com.example.moritz.android_robot_project.Enums.RegulationMode;
import com.example.moritz.android_robot_project.Enums.RunState;

public class MainActivity extends AppCompatActivity {
    public USB NXT_USB;
    public NXT nxt;
    public Motor a,b;
    public Robot rob;
    public int x;
    private ProgressBar battery;
    private TextView speed;
    private TextView status;
    private TextView strecke;
    private TextView sync;
    private TextView log;
    private SeekBar seekbar ;
    private Handler mainHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NXT_USB = new USB();
        NXT_USB.init((UsbManager) getSystemService(Context.USB_SERVICE));
        nxt = NXT.getInstance(NXT_USB);
        a = new Motor(nxt,Port.PORTA,Mode.MOTORON_BREAK_REGULATED,RegulationMode.SYNC,RunState.RUNNING);
        b = new Motor(nxt,Port.PORTB,Mode.MOTORON_BREAK_REGULATED,RegulationMode.SYNC,RunState.RUNNING);
        rob = new Robot();
        rob.setSpeed((byte)20);
        rob.addMotor(a);
        rob.addMotor(b);
        battery = (ProgressBar)findViewById(R.id.progressBar);
        speed = (TextView) findViewById(R.id.textViewSpeed);
        status = (TextView) findViewById(R.id.textViewStat);
        strecke = (TextView) findViewById(R.id.textViewStrecke);
        sync = (TextView) findViewById(R.id.textViewSync);
        log = (TextView) findViewById(R.id.log);
        log.setMovementMethod(new ScrollingMovementMethod());
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setProgress(rob.getSpeed());
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rob.setSpeed((byte)progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Tread Management
        StatusControll sc = new StatusControll();

        sc.start();

    }

    protected void onResume() {
        super.onResume();
        NXT_USB.open(getIntent());
    }



    public void onPause() {
        super.onPause();
        NXT_USB.close();
    }

    public void onBackPressed() {

        startActivity(new Intent(MainActivity.this,MainActivity.class));
    }


    public void btnStart(View v){
        rob.moveForward();
    }

    public void btnStop(View v){

       rob.stop();
    }

    public void writeLog(String s){
        log.setText(log.getText() + "\n" + s);
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
                        battery.setProgress(getBattery());
                        speed.setText(rob.getSpeed() + "%");
                        status.setText(NXT_USB.isConnected()?"aktiv":"inaktiv");
                        strecke.setText("");
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



package com.example.moritz.android_robot_project;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    USB NXT_USB;

    public MainActivity(){
         NXT_USB = new USB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NXT_USB.init((UsbManager) getSystemService(Context.USB_SERVICE));
    }

    protected  void onResume()
    {
        super.onResume();
        NXT_USB.open(getIntent());
    }


    public void onPause()
    {
       super.onPause();
       NXT_USB.close();
    }



    //---------------------------------------------------------------
    // Beispielmethode, die ein Kommando an den NXT sendet und
    // Antwort des NXT anzeigt
    //---------------------------------------------------------------



    public void btnStart(View v)
    {
        startMotor(Motor.motorB,100);
        startMotor(Motor.motorC,100);
    }

    public void btnStop(View v)
    {
        stopMotor(Motor.motorB);
        stopMotor(Motor.motorC);
    }


    public  void startMotor(Motor motor, int speed){
        if(NXT_USB.isConnected()){
            byte[] bytes = new byte[64];
            bytes[0] = (byte) (0x00);//Fix
            bytes[1] = (byte) (0x04);//Fix
            //Port definieren
            switch(motor){
                case motorA: bytes[2] = (byte) (0x00);break;
                case motorB: bytes[2] = (byte) (0x01);break;
                case motorC: bytes[2] = (byte) (0x02);break;
            }
            //Speed definieren
            bytes[3] = (byte) (speed);
            //Mode Motor an
            bytes[4] = (byte) (0x01);
            //Regulation Mode: no Regulation
            bytes[5] = (byte) (0x00);
            //Turn Ratio: 0
            bytes[6] = (byte) (0x00);
            //RunState: 20 Motor running
            bytes[7] = (byte) (0x20);
            //Tacholimit: 0 läuft für immer
            bytes[8] = (byte) (0x00);
            bytes[9] = (byte) (0x00);
            bytes[10] = (byte) (0x00);
            bytes[11] = (byte) (0x00);
            bytes[12] = (byte) (0x00);
            boolean res = NXT_USB.command(bytes, 13, bytes, 3);

            Log.i("NXT", "playTone " + res + " " + bytes[0] + " " + bytes[1] + " " + bytes[2]);
        }
    }

    public  void stopMotor(Motor motor){
        if(NXT_USB.isConnected()){
            byte[] bytes = new byte[64];
            bytes[0] = (byte) (0x00);//Fix
            bytes[1] = (byte) (0x04);//Fix
            //Port definieren
            switch(motor){
                case motorA: bytes[2] = (byte) (0x00);break;
                case motorB: bytes[2] = (byte) (0x01);break;
                case motorC: bytes[2] = (byte) (0x02);break;
            }
            //Speed definieren
            bytes[3] = (byte) (0x00);
            //Mode Motor an
            bytes[4] = (byte) (0x02);
            //Regulation Mode: no Regulation
            bytes[5] = (byte) (0x02);
            //Turn Ratio: 0
            bytes[6] = (byte) (0x00);
            //RunState: 20 Motor running
            bytes[7] = (byte) (0x20);
            //Tacholimit: 0 läuft für immer
            bytes[8] = (byte) (0x00);
            bytes[9] = (byte) (0x00);
            bytes[10] = (byte) (0x00);
            bytes[11] = (byte) (0x00);
            bytes[12] = (byte) (0x00);
            boolean res = NXT_USB.command(bytes, 13, bytes, 3);

            Log.i("NXT", "playTone " + res + " " + bytes[0] + " " + bytes[1] + " " + bytes[2]);
        }
    }




}

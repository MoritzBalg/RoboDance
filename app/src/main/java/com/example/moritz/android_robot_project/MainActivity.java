package com.example.moritz.android_robot_project;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.moritz.android_robot_project.Enums.Mode;
import com.example.moritz.android_robot_project.Enums.Port;
import com.example.moritz.android_robot_project.Enums.RegulationMode;
import com.example.moritz.android_robot_project.Enums.RunState;


public class MainActivity extends AppCompatActivity {
    USB NXT_USB;
    NXT nxt;
    Motor a,b;
    Robot rob;



    public MainActivity() {
        NXT_USB = new USB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NXT_USB.init((UsbManager) getSystemService(Context.USB_SERVICE));
        nxt = NXT.getInstance(NXT_USB);
        a = new Motor(nxt,Port.PORTA,Mode.MOTORON_BREAK,RegulationMode.IDLE,RunState.RUNNING);
        b = new Motor(nxt,Port.PORTB,Mode.MOTORON_BREAK,RegulationMode.IDLE,RunState.RUNNING);
        a.synchronisation(b);
        rob = new Robot();
        rob.addMotor(a);
        rob.addMotor(b);
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
        setContentView(R.layout.activity_main);
    }

    public void btnQuali(View v) {
        setContentView(R.layout.activity_qualifikation);
    }

    public void btnStart(View v) {
        setContentView(R.layout.activity_start);
    }


    //---------------------------------------------------------------
    // Beispielmethode, die ein Kommando an den NXT sendet und
    // Antwort des NXT anzeigt
    //---------------------------------------------------------------


    public void btn_quali_Start(View v) {
        TextView status = findViewById(R.id.textViewStatus);
        status.setText("Motoren gestartet");
        try {
            quali();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void btn_quali_Stop(View v) {
        TextView status = findViewById(R.id.textViewStatus);
        status.setText("Motoren gestoppt");
        rob.stop();

    }


    public void quali() throws InterruptedException {

        rob.moveForwardFor((byte)50, 100);
        rob.stop();
        rob.turn((byte)50,90);
        rob.stop();
        rob.moveForwardFor((byte)50, 50);
        rob.stop();
        rob.turn((byte)50,90);
        rob.stop();
        rob.moveForwardFor((byte)50, 100);
        rob.stop();
        rob.turn((byte)50,90);
        rob.stop();
        rob.moveForwardFor((byte)50, 50);
        rob.stop();
        rob.turn((byte)50,90);
        rob.stop();




    }


}
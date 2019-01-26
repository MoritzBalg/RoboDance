package com.example.moritz.android_robot_project;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public USB NXT_USB;
    public NXT nxt;
    public int x;
    StatusControll sc = new StatusControll();
    Farbe akt = Farbe.UNDEFINIERT;
    Fahren fahr = new Fahren();
    FarbErkennung farb = new FarbErkennung();
    private ProgressBar battery;
    private TextView speed;
    private TextView status;
    private TextView textViewKamera;
    private TextView sync;
    private SeekBar seekbar;
    private Handler mainHandler = new Handler();
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //erstellen einer Kamera-Instanz
        mCamera = CameraPreview.getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        mCamera.setPreviewCallback(mPreview);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mCamera.setDisplayOrientation(90); //drehen der Kamera um 90°

        //Licht anschalten der Kamera, damit die Lichtverhältnise möglichst gleich bleiben.
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);

        //USB verbindeung zum NXT herstellen
        NXT_USB = new USB();
        NXT_USB.init((UsbManager) getSystemService(Context.USB_SERVICE));
        nxt = NXT.getInstance(NXT_USB);

        //Anzeige für unseren StatusThread, welcher aber leider rausgenommen werden musste, da dieser Thread alles "schlafen" gelegt hat.
        battery = findViewById(R.id.progressBar);
        speed = findViewById(R.id.textViewSpeed);
        status = findViewById(R.id.textViewStat);
        sync = findViewById(R.id.textViewSync);
        textViewKamera = findViewById(R.id.textViewKamera);
        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    public void btnStart(View v) {
        fahr.start();
        farb.start();
    }

    public void btnStop(View v) {
        fahr.rob.stop();
    }

    class FarbErkennung extends Thread {
        /*
        Thread der immerwieder scantm ob sich Grün oder Rot im Bild befindet, und
        den Wert dann in akt speichert.
         */
        public void run() {

            while (true) {
                akt = mPreview.isFarbe();
            }
        }
    }


    class Fahren extends Thread {

        Robot rob;
        Motor a, b;

        public Fahren() {
            rob = new Robot(); //anlegen eines neuen Roboter Objekt
            a = new Motor(nxt, Port.PORTA, Mode.MOTORON_BREAK_REGULATED, RegulationMode.SYNC, RunState.RUNNING); //anlegen neuer Motoren
            b = new Motor(nxt, Port.PORTB, Mode.MOTORON_BREAK_REGULATED, RegulationMode.SYNC, RunState.RUNNING);
            rob.setSpeed((byte) 50);
            rob.addMotor(a);
            rob.addMotor(b);

        }


        @Override
        public void run() {
            rob.moveFor();

            while (true) {

                switch (akt) { //anhand der Farbe, die in einem extra Thread immerwieder aktualisiert wird, wird der Roboter gesteuert.

                    case UNDEFINIERT:
                        break;
                    case GRUEN:
                        rob.stop();
                        rob.playGefunden();
                        return;
                    case ROT_RECHTS:
                            rob.stop();
                            rob.moveBack();
                            rob.stop();
                            rob.turn_RECHTS(getRandomNumberInRange(30, 110));
                            rob.stop();
                            rob.moveFor();


                        break;
                    case ROT_LINKS:
                            rob.stop();
                            rob.moveBack();
                            rob.stop();
                            rob.turn_LINKS(getRandomNumberInRange(30, 110));
                            rob.stop();
                            rob.moveFor();

                        break;
                }
            }
        }


        private int getRandomNumberInRange(int min, int max) {
            /*
            Eine Methode um Zufallszahlen zu generieren da die Methode math.random uns zu oft ähnliche Werte gegeben hat.
             */

            if (min >= max) {
                throw new IllegalArgumentException("max must be greater than min");
            }

            Random r = new Random();
            return r.nextInt((max - min) + 1) + min;
        }


    }


    class StatusControll extends Thread {
        /*
        StatusThread, welcher Daten über den NXT auf unserer App ausgibt.
         */
        private static final String TAG = "MainActivity";

        @Override
        public void run() {
            while (true) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        int[] aktuell = new int[3];
                        switch (mPreview.isFarbe()) {

                            case UNDEFINIERT:
                                aktuell = mPreview.Farbe(100, 100);

                                textViewKamera.setText("undefiniert \nRot:" + aktuell[0] + " Grün:" + aktuell[1] + " Blau:" + aktuell[2]);
                                break;
                            case GRUEN:
                                aktuell = mPreview.Farbe(100, 100);

                                textViewKamera.setText("Grün \nRot:" + aktuell[0] + " Grün:" + aktuell[1] + " Blau:" + aktuell[2]);

                                break;
                            case ROT_LINKS:
                                aktuell = mPreview.Farbe(100, 100);

                                textViewKamera.setText("Rot_LINKS\nRot:" + aktuell[0] + " Grün:" + aktuell[1] + " Blau:" + aktuell[2]);
                                break;
                            case ROT_RECHTS:
                                aktuell = mPreview.Farbe(100, 100);

                                textViewKamera.setText("Rot_RECHTS\nRot:" + aktuell[0] + " Grün:" + aktuell[1] + " Blau:" + aktuell[2]);


                                break;
                        }


                        battery.setProgress(getBattery());
                        speed.setText(fahr.rob.getSpeed() + "%");
                        status.setText(NXT_USB.isConnected() ? "aktiv" : "inaktiv");
                        sync.setText(Motor.isSync() ? "aktiv" : "inaktiv");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private int getBattery() {
            int x;
            byte[] bat = nxt.getBatteryLevel();
            x = (int) bat[3] & 0xff
                    | ((int) bat[4] & 0xff) << 8;
            if (x >= 7500) {
                return 100;
            } else if (x < 7500 && x > 7400) {
                return 75;
            } else if (x < 7400 && x > 7300) {
                return 50;
            } else if (x < 7300 && x > 7200) {
                return 25;
            } else if (x < 7200 && x > 7100) {
                return 10;
            }
            return 0;
        }
    }

}



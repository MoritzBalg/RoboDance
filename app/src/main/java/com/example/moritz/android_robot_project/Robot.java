package com.example.moritz.android_robot_project;

import java.util.ArrayList;


public class Robot {
    private ArrayList<Motor> motoren = new ArrayList<>();
    //private Sensor[] sensors;
    private boolean running;
    private byte speed;
    private int cm;

    public Robot(){
        this.cm = 0;
    }

    public void addMotor(Motor motor){
        motoren.add(motor);
    }

    public void moveForward(){
        for(Motor m: motoren){
            m.start(speed);
        }
    }

    public void moveForwardFor(double cm){
        int ticks = (int) (20.4627 * cm);
        int state = motoren.get(0).getRotation();
        moveForward();

        int current = motoren.get(0).getRotation();
        while (!(Math.abs(state-current) >= ticks)){
            current = motoren.get(0).getRotation();
        }
        stop();
    }

    public void moveBackward(){
        speed *=-1;
        for(Motor m: motoren){
            m.start(speed);
        }
    }

    public void stop(){
        for(Motor m: motoren){
            m.stop();
        }

    }

    public void turn(int degrees){
        stop();

        int ticks = (int)(2.02*(double)degrees);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        Motor.setSync(false);
        int state = a.getRotation();
        a.start((byte)25);
        b.start((byte)25);
        while(Math.abs(state-a.getRotation())<= ticks){
        }
        a.stop();
        b.stop();
        Motor.setSync(true);

    }


    public void setSpeed(byte speed) {
        this.speed = speed;

    }

    public byte getSpeed() {

        return speed;
    }


    public int getCm() {
        return cm;
    }

    public void setCm(int cm) {
        this.cm = cm;
    }

    public void playGefunden() {
        NXT nxt = NXT.getInstance();
        nxt.playTone(400,300);

    }
}

package com.example.moritz.android_robot_project;

import java.util.ArrayList;


public class Robot {
    private ArrayList<Motor> motoren = new ArrayList<>();
    //private Sensor[] sensors;
    private boolean running;
    private byte speed;

    public void addMotor(Motor motor){
        motoren.add(motor);
    }

    public void moveFor(){
        for(Motor m: motoren){
            m.start(speed);
        }
    }

    public void reverse(){
        this.speed*=-1;
        this.moveFor();
        this.speed*=-1;

    }

    public void moveBack(){
        int ticks = (int)(360);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        int state = a.getRotation();
        a.start((byte) -50);
        b.start((byte) -50);
        while(Math.abs(state-a.getRotation())<= ticks){
        }
        a.stop();
        b.stop();

    }

    public void stop(){
        for(Motor m: motoren){
            m.stop();
        }

    }

    public void turn_RECHTS(int degrees){
        stop();
        int ticks = (int)(2.02*(double)degrees);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        Motor.setSync(false);
        int state = a.getRotation();
        a.start((byte)25);
        b.start((byte)-25);
        while(Math.abs(state-a.getRotation())<= ticks){
        }
        a.stop();
        b.stop();
        Motor.setSync(true);

    }

    public void turn_LINKS(int degrees){
        stop();
        int ticks = (int)(2.02*(double)degrees);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        Motor.setSync(false);
        int state = a.getRotation();
        a.start((byte)-25);
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


    public void playGefunden() {
        NXT nxt = NXT.getInstance();
        nxt.playTone(400,300);
    }


    private class OUTLIMIT extends Thread{
        Robot rob;

        public OUTLIMIT(Robot rob){
            this.rob = rob;
        }

        @Override
        public void run() {
            rob.stop();
            rob.moveBack();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rob.stop();
            rob.turn_RECHTS(40);
            rob.stop();
            rob.moveFor();
        }
    }

}

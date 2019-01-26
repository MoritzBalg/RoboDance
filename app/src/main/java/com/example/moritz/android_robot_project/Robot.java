package com.example.moritz.android_robot_project;

import java.util.ArrayList;


public class Robot {
    public boolean running;
    private ArrayList<Motor> motoren = new ArrayList<>();
    private byte speed;


    public void addMotor(Motor motor) {
        motoren.add(motor);
    }

    public void moveFor() {
        for (Motor m : motoren) {
            m.start(speed);
        }
    }

    public void reverse() {
        this.speed *= -1;
        this.moveFor();
        this.speed *= -1;

    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void moveBack() {
        this.running = true;
        int ticks = 360;
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);

        int state = a.getRotation();
        a.start((byte) -50);
        b.start((byte) -50);
        while (Math.abs(state - a.getRotation()) <= ticks) {
        }
        a.stop();
        b.stop();
        this.running = false;
    }

    public void stop() {
        for (Motor m : motoren) {
            m.stop();
        }

    }

    public void turn_RECHTS(int degrees) {
        this.running = true;
        stop();
        int ticks = (int) (2.02 * (double) degrees);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        Motor.setSync(false);
        int state = a.getRotation();
        a.start((byte) 25);
        b.start((byte) -25);
        while (Math.abs(state - a.getRotation()) <= ticks) {
        }
        a.stop();
        b.stop();
        Motor.setSync(true);
        this.running = false;
    }

    public void turn_LINKS(int degrees) {
        this.running = true;
        stop();
        int ticks = (int) (2.02 * (double) degrees);
        Motor a = motoren.get(0);
        Motor b = motoren.get(1);
        Motor.setSync(false);
        int state = a.getRotation();
        a.start((byte) -25);
        b.start((byte) 25);
        while (Math.abs(state - a.getRotation()) <= ticks) {
        }
        a.stop();
        b.stop();
        Motor.setSync(true);
        this.running = true;
    }

    public byte getSpeed() {

        return speed;
    }

    public void setSpeed(byte speed) {
        this.speed = speed;

    }

    public void playGefunden() {
        NXT nxt = NXT.getInstance();
        nxt.playTone();
    }


    private class OUTLIMIT extends Thread {
        Robot rob;

        public OUTLIMIT(Robot rob) {
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

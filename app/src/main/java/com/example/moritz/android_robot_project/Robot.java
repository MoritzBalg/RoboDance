package com.example.moritz.android_robot_project;

import java.util.ArrayList;

public class Robot {
    private ArrayList<Motor> motoren = new ArrayList<>();
    //private Sensor[] sensors;
    boolean running;

    public Robot(){}

    public void addMotor(Motor motor){
        motoren.add(motor);
    }

    public void moveForward(byte speed){
        for(Motor m: motoren){
            m.start(speed);
        }
    }

    public void moveForwardFor(byte speed, double cm){
        int ticks = (int) (20.45 * cm);
        int state = getRotation();
        moveForward((byte) speed);

        int current = getRotation();
        while (!(Math.abs(state-current) >= ticks)){
            current = getRotation();
        }




    }

    public void moveBackward(byte speed){
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

    public void turn(int speed ,int degrees){
        /*for(Motor m: motoren){
            m.turn((byte)speed, degrees);
        }*/
        motoren.get(0).turn((byte)speed,degrees);
    }

    public int getRotation(){
        return  motoren.get(0).getRotation();
    }



}

package com.example.moritz.android_robot_project;

import android.util.Log;
import java.math.*;

import com.example.moritz.android_robot_project.Enums.*;

public class Motor {
    private NXT nxt;
    private Port port;
    private Mode mode;
    private RegulationMode regulationMode;
    private RunState runState;

    public Motor(NXT nxt, Port port, Mode mode, RegulationMode regulationMode, RunState runState) {
        this.nxt = nxt;
        this.port = port;
        this.mode = mode;
        this.regulationMode = regulationMode;
        this.runState = runState;
    }

    public void start(byte speed){
        nxt.setOutputState(this.port, speed, this.mode, this.regulationMode, (byte)0, this.runState,  0);
    }


    public void stop(){
        nxt.setOutputState(this.port, (byte)0, this.mode, RegulationMode.IDLE, (byte)0, RunState.IDLE,  0);
        nxt.resetMotorPosition(this.port, true);
    }

    public void synchronisation(Motor motor){
        //Modus der beiden Motoren aktivieren
        this.setMode(Mode.MOTORON_BREAK_REGULATED);
        motor.setMode(Mode.MOTORON_BREAK_REGULATED);

        //Regulierung aktivieren
        this.setRegulationMode(RegulationMode.SYNC);
        motor.setRegulationMode(RegulationMode.SYNC);

    }

    public void turn(byte speed, int degree){

        int x = getRotation();
        nxt.setOutputState(this.port, speed, this.mode, this.regulationMode, (byte)100, this.runState,  0);
        while(Math.abs(x-getRotation()) <= ((int)(degree * 4.6))){}




    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setRegulationMode(RegulationMode regulationMode) {
        this.regulationMode = regulationMode;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    public int getRotation(){
        byte[] get = nxt.getOutputState(this.port);

        int x = (int) ((int) get[21] &0xff)
                      |((int) get[22] &0xff) <<8
                      | ((int) get[23] &0xff) <<16
                      | ((int) get[24] &0xff) << 24;
      return x;
    }



}

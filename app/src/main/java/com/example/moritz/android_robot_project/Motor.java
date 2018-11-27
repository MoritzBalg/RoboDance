package com.example.moritz.android_robot_project;

import android.util.Log;

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

    public void turn(byte speed, byte turnRatio){
        nxt.setOutputState(this.port, speed, this.mode, this.regulationMode, turnRatio, this.runState,  0);
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
        String x;
        byte[] get = nxt.getOutputState(this.port);
       x = get[24] + get[23] + get[22] + get[21]  + "";
        return Integer.parseInt(x);
    }

}

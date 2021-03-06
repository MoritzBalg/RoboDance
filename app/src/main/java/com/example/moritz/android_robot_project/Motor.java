package com.example.moritz.android_robot_project;

import com.example.moritz.android_robot_project.Enums.Mode;
import com.example.moritz.android_robot_project.Enums.Port;
import com.example.moritz.android_robot_project.Enums.RegulationMode;
import com.example.moritz.android_robot_project.Enums.RunState;

public class Motor {
    private static boolean sync;
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
        sync = regulationMode == RegulationMode.SYNC;

    }

    public static boolean isSync() {
        return sync;
    }

    public static void setSync(boolean value) {
        sync = value;
    }

    public void start(byte speed) {
        nxt.resetMotorPosition(this.port, true);
        if (sync) {
            this.regulationMode = RegulationMode.SYNC;
        } else {
            this.regulationMode = RegulationMode.SPEED;
        }
        nxt.setOutputState(this.port, speed, this.mode, this.regulationMode, (byte) 0, this.runState, 0);
    }

    public RegulationMode getRegulationMode() {
        return regulationMode;
    }


    public void setRegulationMode(RegulationMode regulationMode) {
        this.regulationMode = regulationMode;
    }

    public void stop() {
        this.start((byte) 0);
    }

    public void turn(byte speed, int degree) {

        int x = getRotation();
        nxt.setOutputState(this.port, speed, this.mode, this.regulationMode, (byte) 100, this.runState, 0);
        while (Math.abs(x - getRotation()) <= ((int) (degree * 4.6))) {
        }


    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    public int getRotation() {
        byte[] get = nxt.getOutputState(this.port);

        int x = (int) get[21] & 0xff
                | ((int) get[22] & 0xff) << 8
                | ((int) get[23] & 0xff) << 16
                | ((int) get[24] & 0xff) << 24;
        return x;
    }


}

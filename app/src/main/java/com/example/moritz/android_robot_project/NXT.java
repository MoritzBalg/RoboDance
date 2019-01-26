package com.example.moritz.android_robot_project;

import com.example.moritz.android_robot_project.Enums.Mode;
import com.example.moritz.android_robot_project.Enums.Port;
import com.example.moritz.android_robot_project.Enums.RegulationMode;
import com.example.moritz.android_robot_project.Enums.RunState;
import com.example.moritz.android_robot_project.Enums.SensorMode;
import com.example.moritz.android_robot_project.Enums.SensorType;

public class NXT {
    private static NXT instance;
    private USB nxtUsb;

    private NXT() {
    }

    public static synchronized NXT getInstance(USB nxtUsb) {
        if (instance == null) {
            instance = new NXT();
            instance.nxtUsb = nxtUsb;
        }
        return instance;
    }


    public static synchronized NXT getInstance() {
        if (instance == null) {
            instance = new NXT();
        }
        return instance;
    }

    private static int[] toHex(int number, int blocks) {
        int[] result = new int[blocks];
        String substr;
        String binStr = Integer.toBinaryString(number);
        if (binStr.length() % (8 * blocks) != 0) {
            for (int i = (8 * blocks) - (binStr.length() % (8 * blocks)); i > 0; i--) {
                binStr = "0" + binStr;
            }
        }
        for (int i = 0; i < result.length; i++) {
            substr = binStr.substring(i * 8, (i + 1) * 8);
            result[i] = Integer.parseInt(substr, 2);
        }
        return result;
    }

    public byte[] playTone() {
        byte[] outBytes = new byte[6];
        byte[] inBytes = new byte[3];
        if (nxtUsb.isConnected()) {
            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x03);
            //Frequenz
            outBytes[2] = (byte) 0x02;
            outBytes[3] = (byte) 0x00;
            //Dauer
            outBytes[5] = (byte) 0x0c;
            outBytes[4] = (byte) 0x00;

            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] setOutputState(Port port, byte speed, Mode mode, RegulationMode regulationMode, byte turnRatio, RunState runState, int tacholimit) {
        byte[] outBytes = new byte[13];
        byte[] inBytes = new byte[3];
        if (/*nxtUsb.isConnected()*/true) {
            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x04);
            //Port
            switch (port) {
                case PORTA:
                    outBytes[2] = (byte) 0x00;
                    break;
                case PORTB:
                    outBytes[2] = (byte) 0x01;
                    break;
                case PORTC:
                    outBytes[2] = (byte) 0x02;
                    break;
                case ALLOUTPUTPORTS:
                    outBytes[2] = (byte) 0xff;
                    break;
            }
            //Power set point Value: -100 - 100
            outBytes[3] = speed;
            //Mode Byte
            switch (mode) {
                case MOTORON:
                    outBytes[4] = (byte) 0x01;
                    break;
                case MOTORON_BREAK:
                    outBytes[4] = (byte) 0x03;
                    break;
                case MOTORON_REGULATED:
                    outBytes[4] = (byte) 0x05;
                    break;
                case MOTORON_BREAK_REGULATED:
                    outBytes[4] = (byte) 0x07;
                    break;

            }
            //RegulationMode
            switch (regulationMode) {
                case IDLE:
                    outBytes[5] = (byte) 0x00;
                    break;
                case SPEED:
                    outBytes[5] = (byte) 0x01;
                    break;
                case SYNC:
                    outBytes[5] = (byte) 0x02;
                    break;
            }
            //Turn Ratio
            outBytes[6] = turnRatio;
            //RunState
            switch (runState) {
                case IDLE:
                    outBytes[7] = (byte) 0x00;
                    break;
                case RAMPUP:
                    outBytes[7] = (byte) 0x10;
                    break;
                case RUNNING:
                    outBytes[7] = (byte) 0x20;
                    break;
                case RAMPDOWN:
                    outBytes[7] = (byte) 0x40;
                    break;
            }
            //Tacholimit
            int[] tacho = toHex(tacholimit, 5);
            outBytes[8] = (byte) tacho[0];
            outBytes[9] = (byte) tacho[1];
            outBytes[10] = (byte) tacho[2];
            outBytes[11] = (byte) tacho[3];
            outBytes[12] = (byte) tacho[4];
            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] setInputMode(Port port, SensorType sensorType, SensorMode sensorMode) {
        byte[] outBytes = new byte[5];
        byte[] inBytes = new byte[3];
        if (nxtUsb.isConnected()) {
            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x05);
            switch (port) {
                case PORT1:
                    outBytes[2] = (byte) (0x00);
                    break;
                case PORT2:
                    outBytes[2] = (byte) (0x01);
                    break;
                case PORT3:
                    outBytes[2] = (byte) (0x02);
                    break;
                case PORT4:
                    outBytes[2] = (byte) (0x03);
                    break;
            }

            //SensorType
            switch (sensorType) {
                case NO_SENSOR:
                    outBytes[3] = (byte) (0x00);
                    break;
                case SWITCH:
                    outBytes[3] = (byte) (0x01);
                    break;
                case TEMPERATUR:
                    outBytes[3] = (byte) (0x02);
                    break;
                case REFELCTION:
                    outBytes[3] = (byte) (0x03);
                    break;
                case ANGLE:
                    outBytes[3] = (byte) (0x04);
                    break;
                case LIGHT_ACTIVE:
                    outBytes[3] = (byte) (0x05);
                    break;
                case LIGHT_INACTIVE:
                    outBytes[3] = (byte) (0x06);
                    break;
                case SOUND_DB:
                    outBytes[3] = (byte) (0x07);
                    break;
                case SOUND_DBA:
                    outBytes[3] = (byte) (0x08);
                    break;
                case CUSTOM:
                    outBytes[3] = (byte) (0x09);
                    break;
                case LOWSPEED:
                    outBytes[3] = (byte) (0x0A);
                    break;
                case LOWSPEED_9V:
                    outBytes[3] = (byte) (0x0B);
                    break;
                case NO_OF_SENSOR_TYPES:
                    outBytes[3] = (byte) (0x0C);
                    break;
            }

            switch (sensorMode) {
                case RAWMODE:
                    outBytes[4] = (byte) (0x00);
                    break;
                case BOOLEANMODE:
                    outBytes[4] = (byte) (0x20);
                    break;
                case TRANSITIONCNTMODE:
                    outBytes[4] = (byte) (0x40);
                    break;
                case PERIODECOUNTERMODE:
                    outBytes[4] = (byte) (0x60);
                    break;
                case PCTFULLSCALEMODE:
                    outBytes[4] = (byte) (0x80);
                    break;
                case CELSIUSMODE:
                    outBytes[4] = (byte) (0xA0);
                    break;
                case FAHRENHEITMODE:
                    outBytes[4] = (byte) (0xC0);
                    break;
                case ANGLESTEPMODE:
                    outBytes[4] = (byte) (0xE0);
                    break;
                case SLOPEMASK:
                    outBytes[4] = (byte) (0x1F);
                    break;
                case MODEMASK:
                    outBytes[4] = (byte) (0xE0);
                    break;
            }


            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] getOutputState(Port port) {
        byte[] outBytes = new byte[3];
        byte[] inBytes = new byte[25];
        if (nxtUsb.isConnected()) {

            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x06);
            //Port
            switch (port) {
                case PORTA:
                    outBytes[2] = (byte) 0x00;
                    break;
                case PORTB:
                    outBytes[2] = (byte) 0x01;
                    break;
                case PORTC:
                    outBytes[2] = (byte) 0x02;
                    break;
                case ALLOUTPUTPORTS:
                    outBytes[2] = (byte) 0xff;
                    break;
            }
            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] getInputValues(Port port) {
        byte[] outBytes = new byte[3];
        byte[] inBytes = new byte[16];
        if (nxtUsb.isConnected()) {

            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x07);
            //Port
            switch (port) {
                case PORT1:
                    outBytes[2] = (byte) (0x00);
                    break;
                case PORT2:
                    outBytes[2] = (byte) (0x01);
                    break;
                case PORT3:
                    outBytes[2] = (byte) (0x02);
                    break;
                case PORT4:
                    outBytes[2] = (byte) (0x03);
                    break;
            }


            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] resetInputScaledValue(Port port) {
        byte[] outBytes = new byte[3];
        byte[] inBytes = new byte[3];
        if (nxtUsb.isConnected()) {

            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x08);
            //Port
            switch (port) {
                case PORT1:
                    outBytes[2] = (byte) (0x00);
                    break;
                case PORT2:
                    outBytes[2] = (byte) (0x01);
                    break;
                case PORT3:
                    outBytes[2] = (byte) (0x02);
                    break;
                case PORT4:
                    outBytes[2] = (byte) (0x03);
                    break;
            }


            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] resetMotorPosition(Port port, boolean relativ) {
        byte[] outBytes = new byte[4];
        byte[] inBytes = new byte[3];
        if (nxtUsb.isConnected()) {

            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x0A);
            //Port
            switch (port) {
                case PORTA:
                    outBytes[2] = (byte) 0x00;
                    break;
                case PORTB:
                    outBytes[2] = (byte) 0x01;
                    break;
                case PORTC:
                    outBytes[2] = (byte) 0x02;
                    break;
                case ALLOUTPUTPORTS:
                    outBytes[2] = (byte) 0xff;
                    break;
            }
            //Relativ zur letzten bewegung
            if (relativ) {
                outBytes[3] = (byte) 0X01;
            } else {
                outBytes[3] = (byte) 0X00;
            }

            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

    public byte[] getBatteryLevel() {
        byte[] outBytes = new byte[2];
        byte[] inBytes = new byte[5];
        if (nxtUsb.isConnected()) {

            //fixe Bytes
            outBytes[0] = (byte) (0x00);
            outBytes[1] = (byte) (0x0B);

            boolean res = nxtUsb.command(outBytes, outBytes.length, inBytes, inBytes.length);
        }
        return inBytes;
    }

}

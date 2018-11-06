package com.example.moritz.android_robot_project;

import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;

//*******************************************************************
public class USB
{
    //---------------------------------------------------------------
    public USB()
    {
    }

    //---------------------------------------------------------------
    public void init(UsbManager _mUsbManager)
    {
        this.mUsbManager = _mUsbManager;
    }

    //---------------------------------------------------------------
    public void open(Intent intent)
    {
        String action = intent.getAction();
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if( UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action) )
        {
            setDevice(device);
        }
        else if( UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action) )
        {
            if( mDevice != null && mDevice.equals(device) )
            {
                setDevice(null);
            }
        }
        else
        {
            HashMap<String, UsbDevice> map = mUsbManager.getDeviceList();
            Iterator<UsbDevice> it = map.values().iterator();
            while (it.hasNext())
            {
                device = it.next();
                setDevice(device);
            }
        }
    }

    //---------------------------------------------------------------
    public void close()
    {
        usbConnected=false;
    }

    //---------------------------------------------------------------
    public boolean isConnected()
    {
        return( usbConnected );
    }

    //-----------------------------------------------------------------
    private void setDevice(UsbDevice device)
    {
        Log.w(TAG, "setDevice " + device);
        if (device.getInterfaceCount() != 1)
        {
            Log.e(TAG, "could not find interface");
            return;
        }

        UsbInterface intf = device.getInterface(0);
        if (device.getInterfaceCount() != 1)
        {
            Log.e(TAG, "interface 0 error");
            return;
        }

        epIN = intf.getEndpoint(1);
        if (epIN.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK)
        {
            Log.e(TAG, "endpoint 1 error");
            return;
        }

        epOUT = intf.getEndpoint(0);
        if (epOUT.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK)
        {
            Log.e(TAG, "endpoint 0 error");
            return;
        }

        mDevice = device;

        if( device != null )
        {
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(intf, true))
            {
                Log.e(TAG, "open SUCCESS");
                mConnection = connection;
                usbConnected = true;
            }
            else
            {
                Log.d(TAG, "open FAIL");
                mConnection = null;
            }
        }
        else
        {
            usbConnected=false;
        }
    }

    //---------------------------------------------------------------
    public boolean command(byte[] outBytes, int outSize, byte[] inBytes, int inSize)
    {
        int res = mConnection.bulkTransfer(epOUT, outBytes, outSize, 100);

        if( res == outSize )
        {
            res = mConnection.bulkTransfer(epIN, inBytes, inSize, 100);
            if( res == inSize )
            {
                return (true);
            }
        }
        return( false );
    }

    //---------------------------------------------------------------
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;

    private UsbEndpoint epOUT;
    private UsbEndpoint epIN;

    //---------------------------------------------------------------
    private boolean usbConnected = false;
    private static final String TAG = "NXT_USB";
}
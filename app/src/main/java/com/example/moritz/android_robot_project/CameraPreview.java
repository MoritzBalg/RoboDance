package com.example.moritz.android_robot_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.moritz.android_robot_project.Enums.Farbe;

import java.io.IOException;

import static android.content.ContentValues.TAG;


/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    int[] myPixels;
    byte[] dataP;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        dataP = data;
       // android.util.Log.d("onPreviewFrame", "called");

        int frameHeight = camera.getParameters().getPreviewSize().height;
        int frameWidth = camera.getParameters().getPreviewSize().width;
        // number of pixels//transforms NV21 pixel data into RGB pixels
        int rgb[] = new int[frameWidth * frameHeight];
        // convertion
        myPixels = decodeYUV420SP(rgb, data, frameWidth, frameHeight);

    }

    public int[] decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

        // here we're using our own internal PImage attributes
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                // use interal buffer instead of pixels for UX reasons
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }

        return rgb;
    }

    int frameHeight;
    int frameWidth;
    Bitmap bmp;

    public void Foto(){
        frameHeight = mCamera.getParameters().getPreviewSize().height;
        frameWidth = mCamera.getParameters().getPreviewSize().width;
        int rgb[] = new int[frameWidth * frameHeight];
        decodeYUV420SP(rgb, dataP, frameWidth, frameHeight);
        bmp = Bitmap.createBitmap(rgb, frameWidth, frameHeight, Bitmap.Config.RGB_565);
    }


    public int[] Farbe(int x, int y) {

        int pixel = bmp.getPixel(x,y);
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);
        int color = Color.rgb(redValue, greenValue, blueValue);
        int rgbE[] = {redValue, greenValue, blueValue};
        return rgbE;
    }




     public Farbe isFarbe(){
        Foto();
        //0 = Nichts; 1 = Gruen; 2 = Rot
        for(int i = 0; i<frameWidth; i+=5){
            for(int j =(int)((frameHeight/5)*3); j<frameHeight; j+=3){
                int[] color = Farbe(i,j);
                if(color[0] > 200&&color[1]< 100 && color[2]<100){
                    if(i>(int)(frameWidth/2)+200) {
                        return Farbe.ROT_LINKS;
                    }else if(i<(int)(frameWidth/2)-200){
                        return Farbe.ROT_RECHTS;
                    }
                }
                if(color[0] < 100&&color[1]> 150 && color[2]<100){
                    return Farbe.GRUEN;
                }
            }
        }
        return Farbe.UNDEFINIERT;
    }


}


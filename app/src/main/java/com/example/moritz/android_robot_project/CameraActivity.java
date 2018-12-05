package com.example.moritz.android_robot_project;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

import static com.example.moritz.android_robot_project.CameraPreview.getCameraInstance;

public class CameraActivity extends MainActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.Camera_preview);
        preview.addView(mPreview);
    }
}

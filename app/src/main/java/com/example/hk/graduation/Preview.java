package com.example.hk.graduation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.os.Environment;

public class Preview extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder mHolder;

    Camera mCamera = null;

    public void close() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);

        try {
            if (mCamera == null)
                mCamera = Camera.open();

            mHolder = getHolder();

            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) {
                mCamera = Camera.open();
            }

            if (mCamera != null) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    Camera.Parameters parameters = mCamera.getParameters();
                    mCamera.setParameters(parameters);
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                } catch (IOException exception) {
                    Log.e("Error", "exception:surfaceCreated Camera Open ");
                    mCamera.release();
                    mCamera = null;
                    // TODO: add more exception handling logic here
                }
            }
        } catch (Exception e) {
            Log.e("camera open error", "error");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewSize(640,480);
        params.setPictureSize(640,480);
        params.setRotation(90);

        mCamera.setParameters(params);
        mCamera.startPreview();
    }
}
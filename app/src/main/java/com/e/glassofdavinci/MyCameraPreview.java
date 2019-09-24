package com.e.glassofdavinci;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MyCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;

    private int mCameraID;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    private int mDisplayOrientation;

    //생성자를 통해 카메라 실행
    public MyCameraPreview(Context context, int cameraId){
        super(context);

        // 0은 뒷면 카메라
        // 1은 앞면 카메라
        mCameraID = cameraId;

        Log.d("My Camera","MyCameraPreview camera ID : " + cameraId);

        try {
            //카메라 인스턴스를 받아오기 시전
            mCamera = Camera.open(mCameraID);
        }catch (Exception e){
            //카메라 불가능(사용중이거나 존재하지 않음)
            Log.d("My Camera","Camera is not available");
        }

        //---------------------이부분 왜 필요한지 질문------------------//

        //SurfaceHolder.Callback 설치
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //---------------------이부분 왜 필요한지 질문------------------//

        //디스플레이 방향
        mDisplayOrientation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
    }

    // 1.이게 제일 먼저 동작
    public void surfaceCreated(SurfaceHolder holder){
        Log.d("My Camera","surfaceCreated");

        //retrieve camera's info.
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID,cameraInfo);

        mCameraInfo = cameraInfo;

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d("Error", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        Log.d("My Camera","surfaceDestroyed");
    }

    // 2. 그다음 동작
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        Log.d("My Camera","surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            Log.e("My Camera", "preview surface does not exist");
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
            Log.d("My Camera", "Preview stopped.");
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d("My Camera", "Error starting camera preview: " + e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            Log.d("My Camera", "Camera preview started.");
        } catch (Exception e) {
            Log.d("My Camera", "Error starting camera preview: " + e.getMessage());
        }
    }

    //안드로이드 디바이스 방향에 맞는 카메라 프리뷰를 화면에 보여주기 위해 계산
    public int calculatePreviewOrientation(Camera.CameraInfo info, int rotation){
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) %360;
        }

        return result;
    }
}

package com.example.heyha.framecamera.stickercamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.heyha.framecamera.R;
import com.example.heyha.framecamera.framecamera.CameraInterface;
import com.example.heyha.framecamera.util.DisplayUtil;

public class StickerPhotoActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    //    private SurfaceHolder mSurfaceHolder;
    private float previewRate = -1f; //屏幕宽高比
    private Activity mActivity = this;

    @Bind(R.id.sticker_surface_view)
    public TextureView mSurfaceView;
    @Bind(R.id.sticker_image_layout)
    public LinearLayout mSelectedFrame;
    @Bind(R.id.sticker_select_bg)
    public ImageView mSelectBackground;

    @OnClick(R.id.sticker_take_photo)

    public void takePhoto() {
        Log.i("TAG", "mSelectedFrame width:" + mSelectedFrame.getWidth() + "mSelectedFrame height:" + mSelectedFrame.getHeight());
        Log.i("TAG", "mSurfaceView width:" + mSurfaceView.getWidth() + "mSurfaceView height:" + mSurfaceView.getHeight());
        CameraInterface.getInstance().doTakePicture(mSelectedFrame, mSurfaceView);
    }

    private CameraInterface.CamCompletedCallback completedCallback = new CameraInterface.CamCompletedCallback() {
        @Override
        public void cameraHasCompleted(String filePath) {
            Intent intent = new Intent();
            intent.putExtra("filePath", filePath);
            setResult(RESULT_OK, intent);
            mActivity.finish();
        }
    };

    private CameraInterface.FaceChangedCallback faceChangedCallback = new CameraInterface.FaceChangedCallback() {
        @Override
        public void facechanged(Rect rect) {
            //do nothing 用于边框拍照
        }

        @Override
        public void faceCapture(Camera.Face face) {
            //捕获的人脸
            if (face != null){
//                mSelectedFrame.removeAllViews();
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//                BouncingView bouncingView = new BouncingView(getApplicationContext(),null);
//
//                int cx = -face.leftEye.y;
//                int cy = face.leftEye.x;
//                int wScreen = DisplayUtil.getScreenMetrics(getApplicationContext()).x;
//                int hScreen = DisplayUtil.getScreenMetrics(getApplicationContext()).y;
//                int coordinateX = (int) ((cx + 1000f) * wScreen / 2000f);
//                int coordinateY = (int) ((cy + 1000f) * hScreen / 2000f);
//                bouncingView.setLocation(new Point(coordinateX,coordinateY));
//                mSelectedFrame.addView(bouncingView,params);
            }else {
                //do nothing
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_photo);
        ButterKnife.bind(this);

//        mSurfaceHolder = mSurfaceView.getHolder();
//        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
//        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        mSurfaceHolder.addCallback(this);
        mSurfaceView.setSurfaceTextureListener(this);
        mSurfaceView.setAlpha(1.0f);
        previewRate = DisplayUtil.getScreenRate(this);
        CameraInterface.getInstance().setCamCompletedCallback(completedCallback);
        CameraInterface.getInstance().setFaceChangedCallback(faceChangedCallback);


    }

/*    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInterface.getInstance().doOpenCamera();
        CameraInterface.getInstance().doStartPreview(holder,previewRate);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraInterface.getInstance().doStopCamera();
    }*/

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        CameraInterface.getInstance().doOpenCamera(2);
        CameraInterface.getInstance().doStartPreview(surface, previewRate);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraInterface.getInstance().doStopCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}

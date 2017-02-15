package com.example.heyha.framecamera.framecamera;

import android.graphics.*;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import com.example.heyha.framecamera.util.CamParaUtil;
import com.example.heyha.framecamera.util.FileUtil;
import com.example.heyha.framecamera.util.ImageUtil;

import java.io.IOException;
import java.util.List;

public class CameraInterface {
    private static final String TAG = "ZZF";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;
    private CamCompletedCallback camCompletedCallback;
    private FaceChangedCallback faceChangedCallback;
    private View imageView;  //仅用于贴纸拍照
    private TextureView mSurfaceView; //仅用于贴纸拍照
    private Point picSizePx;   //带边框拍照,需要的图片的大小，单位为px
    private Point scrSize;     //带边框拍照，屏幕的大小
    private Rect centerRect;   //带边框拍照，在屏幕中边框的矩阵
    private Point rectSize;    //带边框拍照,需要的边框大小
    private int TYPE_MODE = 0;  //0-普通拍照，1-边框拍照，2-贴纸拍照
    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            switch (TYPE_MODE){
                case 0:
                    break;
                case 1:
                    if (faces.length > 0){
                        for (int i = 0; i < faces.length; i++) {
//                            Log.i(TAG, "face rect:" + faces[i].rect.left + "," + faces[i].rect.top + ","
//                                    + faces[i].rect.right + "," + faces[i].rect.bottom);
                            faceChangedCallback.facechanged(dri2View(faces[i])); //用于边框拍照
                        }
                    }else {
                        faceChangedCallback.facechanged(null);
                    }
                    break;
                case 2:
                    if (faces.length > 0){
                        for (int i = 0; i < faces.length; i++) {
                            Log.i(TAG, "face rect:" + faces[i].rect + "face lefteye:" + faces[i].leftEye + "face righteye:" + faces[i].rightEye);
                            faceChangedCallback.faceCapture(faces[i]); //用于贴纸拍照
                        }
                    }else {
                        faceChangedCallback.faceCapture(null);
                    }
                    break;
            }


        }
    };

    /**
     * 将人脸坐标转换成视图坐标
     * 为了保证边框是固定大小的正方形，需要取得View坐标下人脸中心的坐标
     *
     * @param face
     * @return
     */
    private Rect dri2View(Camera.Face face) {
        //View坐标下人脸中心的坐标
        int cx = -face.rect.centerY();
        int cy = face.rect.centerX();
        int coordinateX = (int) ((cx + 1000f) * scrSize.x / 2000f);
        int coordinateY = (int) ((cy + 1000f) * scrSize.y / 2000f);

        //求得View坐标下固定大小的边框的准确坐标
        int leftR = coordinateX - (rectSize.x / 2);
        int topR = coordinateY - (rectSize.y / 2);
        int rightR = coordinateX + (rectSize.x / 2);
        int bottomR = coordinateY + (rectSize.y / 2);
        Rect rect = new Rect(leftR, topR, rightR, bottomR);
        Log.i(TAG, "view rect:" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
        return rect;
    }

    public interface FaceChangedCallback {
        public void facechanged(Rect rect);  //用于人脸检测带边框
        public void faceCapture(Camera.Face face);

    }

    public void setFaceChangedCallback(FaceChangedCallback callback) {
        this.faceChangedCallback = callback;
    }

    public interface CamCompletedCallback {
        public void cameraHasCompleted(String filePath);
    }

    public void setCamCompletedCallback(CamCompletedCallback callback) {
        this.camCompletedCallback = callback;
    }

    private CameraInterface() {

    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }


    public void doOpenCamera(int typeMode) {
        this.TYPE_MODE = typeMode;
        Log.i(TAG, "Camera open....");
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Camera open over....");
    }

    public void doStartPreview(Point scrSize, Point rectSize, SurfaceHolder holder, float previewRate) {
        this.scrSize = scrSize;
        this.rectSize = rectSize;
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            initCamera(previewRate);
        }

    }

    public void doStartPreview(SurfaceTexture surface, float previewRate) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }

    }

    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 正常拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    int DST_RECT_WIDTH, DST_RECT_HEIGHT;

    /**
     * 带边框的拍照
     *
     * @param w
     * @param h
     */
    public void doTakePicture(int w, int h) {
        if (isPreviewing && (mCamera != null)) {
            DST_RECT_WIDTH = w;
            DST_RECT_HEIGHT = h;
            mCamera.takePicture(mShutterCallback, null, mRectJpegPictureCallback);
        }
    }

    public void doTakePicture(Point picSize, Rect centerRect) {
        if (isPreviewing && (mCamera != null)) {
            this.picSizePx = picSize;
            this.centerRect = centerRect;
//            mCamera.stopFaceDetection();
            mCamera.takePicture(mShutterCallback, null, mRectJpegPictureCallback);
        }
    }

    /**
     * 带贴纸的拍照
     *
     * @param imageView   贴纸
     * @param surfaceView 相机预览
     */
    public void doTakePicture(View imageView, TextureView surfaceView) {
        this.imageView = imageView;
        this.mSurfaceView = surfaceView;
        if (isPreviewing && mCamera != null) {
            mCamera.takePicture(mShutterCallback, null, mStickerPictureCallback);
        }
    }

    public Point doGetPrictureSize() {
        Size s = mCamera.getParameters().getPictureSize();
        return new Point(s.width, s.height);
    }

    public Camera getmCamera() {
        if (mCamera != null) {
            return mCamera;
        }
        return null;
    }


    private void initCamera(float previewRate) {
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);
            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(), previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setDisplayOrientation(90);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();
            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters();
            Log.i(TAG, "init Camera:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "init Camera:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);

            mCamera.setFaceDetectionListener(faceDetectionListener);
            mCamera.startFaceDetection();

        }
    }


    ShutterCallback mShutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }
            if (null != b) {
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                String filePath = FileUtil.saveBitmap(rotaBitmap);
                camCompletedCallback.cameraHasCompleted(filePath);
                if (rotaBitmap.isRecycled()) {
                    rotaBitmap.recycle();
                    rotaBitmap = null;
                }
            }
            mCamera.startPreview();
            isPreviewing = true;
            if (!b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }
    };

    PictureCallback mRectJpegPictureCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }
            if (null != b) {
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);//拍照后的原生图片
                //难点部分
                float wRate = (float) rotaBitmap.getWidth() / (float) scrSize.x;
                float hRate = (float) rotaBitmap.getHeight() / (float) scrSize.y;
                int objPicWidth = (int) (wRate * picSizePx.x);
                int objPicHeight = (int) (hRate * picSizePx.y);
                int objPicLeft = (int) (wRate * centerRect.left);
                int objPicTop = (int) (hRate * centerRect.top);

                Bitmap rectBitmap = Bitmap.createBitmap(rotaBitmap, objPicLeft, objPicTop, objPicWidth, objPicHeight);//截取的边框内的图片
                String filePath = FileUtil.saveBitmap(rectBitmap);
                camCompletedCallback.cameraHasCompleted(filePath);
                if (rotaBitmap.isRecycled()) {
                    rotaBitmap.recycle();
                    rotaBitmap = null;
                }
                if (rectBitmap.isRecycled()) {
                    rectBitmap.recycle();
                    rectBitmap = null;
                }
            }

            //继续预览
            mCamera.startPreview();
            isPreviewing = true;
            if (!b.isRecycled()) {
                b.recycle();
                b = null;
            }

        }
    };


    PictureCallback mStickerPictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap imgBitmap = imageView.getDrawingCache();
            Bitmap surfaceBitmap = mSurfaceView.getBitmap();
            Log.i(TAG, "img width:" + imgBitmap.getWidth() + "img height:" + imgBitmap.getHeight());
            Log.i(TAG, "sur width:" + surfaceBitmap.getWidth() + "sur height:" + surfaceBitmap.getHeight());
            Bitmap objBitmap = Bitmap.createBitmap(surfaceBitmap.getWidth(), surfaceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(objBitmap);
            canvas.drawBitmap(surfaceBitmap, 0, 0, new Paint());
            canvas.drawBitmap(imgBitmap, 0, 0, new Paint());
            canvas.save();
            canvas.restore();
            imageView.setDrawingCacheEnabled(false);

            String filePath = FileUtil.saveBitmap(objBitmap);
            camCompletedCallback.cameraHasCompleted(filePath);

        }
    };
}

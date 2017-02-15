package com.example.heyha.framecamera.framecamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.heyha.framecamera.R;
import com.example.heyha.framecamera.util.DisplayUtil;

/**
 * 带边框的拍照难点：
 * 1、SurfaceView本身不能绘制,因为setWillNotDraw(true);的存在，所以需要重写SurfaceView，并在构造中setWillNotDraw(false);这样onDraw（）方法才会执行
 * 2、屏幕的尺寸和拍摄的原生照片的尺寸不一样，因此想要获取边框内的图片，需要进行转换，算法如下：
 * 水平比率=原生照片宽/屏幕宽；
 * 竖直比率=原生照片高/屏幕高
 * 再用边框矩阵的(left,top) * (水平比率,竖直比率) => (原生照片中对应的边框的左上角的坐标)
 */
public class PhotoActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder = null;
    private float previewRate = -1f;
    private int DST_CENTER_RECT_WIDTH = 200;  //单位是dip
    private int DST_CENTER_RECT_HEIGHT = 200;
    private Activity mActivity = this;
    private Point scrSize;
    private Point picSize;
    private Rect screenCenterRect;  //边框矩阵，用于设置边框位置和获取边框图片的位置

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
            if (rect == null){
                mSurfaceView.setCenterRect(null);
            }else {
                screenCenterRect = rect;
                mSurfaceView.setCenterRect(screenCenterRect);
            }
        }

        @Override
        public void faceCapture(Camera.Face face) {
            //do nothing
        }
    };

    @Bind(R.id.mySurfaceVIew)
    public MySurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        initViewParams();
        CameraInterface.getInstance().setCamCompletedCallback(completedCallback);
        CameraInterface.getInstance().setFaceChangedCallback(faceChangedCallback);
    }

    private void initViewParams() {
        if (mSurfaceView != null) {
            picSize = new Point(DisplayUtil.dip2px(this, DST_CENTER_RECT_WIDTH),
                    DisplayUtil.dip2px(this, DST_CENTER_RECT_HEIGHT));
            screenCenterRect = createCenterScreenRect(picSize.x, picSize.y);
            mSurfaceView.setCenterRect(screenCenterRect);
        }
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);
        mSurfaceView.setLayoutParams(params);
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);

        int wScreen = DisplayUtil.getScreenMetrics(this).x;
        int hScreen = DisplayUtil.getScreenMetrics(this).y;
        scrSize = new Point(wScreen, hScreen);
    }

    @OnClick(R.id.sticker_select_bg)
    public void takePhoto() {
        int picWidthPx = DisplayUtil.dip2px(this, DST_CENTER_RECT_WIDTH);
        int picHeightPx = DisplayUtil.dip2px(this, DST_CENTER_RECT_HEIGHT);
        Point picSize = new Point(picWidthPx, picHeightPx);
        CameraInterface.getInstance().doTakePicture(picSize,screenCenterRect);
    }

    @OnClick(R.id.full_take)
    public void fullPhoto() {
        CameraInterface.getInstance().doTakePicture();
    }

    /**
     * 取边框的位置
     *
     * @param w
     * @param h
     * @return
     */
    private Rect createCenterScreenRect(int w, int h) {
        int x1 = DisplayUtil.getScreenMetrics(this).x / 2 - w / 2;
        int x2 = x1 + w;
        int y1 = DisplayUtil.getScreenMetrics(this).y / 2 - h / 2;
        int y2 = y1 + h;
        return new Rect(x1, y1, x2, y2);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInterface.getInstance().doOpenCamera(1);
        CameraInterface.getInstance().doStartPreview(scrSize, picSize, mSurfaceHolder, previewRate);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraInterface.getInstance().doStopCamera();
    }


}

package com.example.heyha.framecamera.stickercamera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

/**
 * 图层
 * Created by Heyha on 2017/2/17.
 */
public class GraphicOverlay extends View {

    private final Object mLock = new Object();
    private Set<Graphic> mGraphic = new HashSet<>();
    private int mPreviewHeight;
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_FRONT;


    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mLock){
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)){
                mWidthScaleFactor = (float)canvas.getWidth() / (float)mPreviewWidth;
                mHeightScaleFactor = (float)canvas.getHeight() / (float)mPreviewHeight;
            }
            for (Graphic graphic : mGraphic){
                graphic.draw(canvas);
            }
        }
    }

    public void add(Graphic graphic){
        synchronized (mLock){
            mGraphic.add(graphic);
        }
        postInvalidate();
    }

    public void remove(Graphic graphic){
        synchronized (mLock){
            mGraphic.remove(graphic);
        }
        postInvalidate();
    }

    public void clear(){
        synchronized (mLock){
            mGraphic.clear();
        }
        postInvalidate();
    }

    public void setCameraInfo(int previewHeight,int previewWidth,int cameraFacing){
        synchronized (mLock){
            mPreviewHeight = previewHeight;
            mPreviewWidth = previewWidth;
            mFacing = cameraFacing;
        }
        postInvalidate();
    }



    //图像实体
    abstract class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay graphicOverlay) {
            mOverlay = graphicOverlay;
        }

        public abstract void draw(Canvas canvas);

        public float scaleX(float horizontal){
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        public float scaleY(float vertical){
            return vertical * mOverlay.mHeightScaleFactor;
        }

        public float translateX(float x){
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT){
                return mOverlay.getWidth() - scaleX(x);
            }else {
                return scaleX(x);
            }
        }

        public float translateY(float y){
                return scaleY(y);
        }

        public void postInvalidate(){
            mOverlay.postInvalidate();
        }
    }
}



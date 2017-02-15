package com.example.heyha.framecamera.framecamera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import com.example.heyha.framecamera.R;

/**
 * Created by Heyha on 2017/2/6.
 */
public class RectFrameView extends ImageView {
    private static final String TAG = "zzf";
    private Paint mLinePaint;
    private Paint mAreaPaint;
    private Rect mCenterRect = null;
    private Context mContext;
    private int mWidthScreen;
    private int mHeightScreen;

    public RectFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mContext = context;
        Point point = getScreenMetrics();
        mWidthScreen = point.x;
        mHeightScreen = point.y;
    }

    private void initPaint() {
        //绘制中间透明区域矩形边框
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(getResources().getColor(R.color.colorLinePaint));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(200);

        //绘制阴影区域
//        mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mAreaPaint.setColor(Color.GRAY);
//        mAreaPaint.setStyle(Paint.Style.FILL);
//        mAreaPaint.setAlpha(180);
    }

    public void setCenterRect(Rect r){
        Log.i(TAG,"setCenterRect。。。");
        mCenterRect = r;
        postInvalidate();
    }

    public void clearCenterRect(){
        Log.i(TAG,"clearCenterRect");
        mCenterRect = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG,"onDraw。。。");
        if (mCenterRect == null){
            return;
        }
        //绘制阴影区域
//        canvas.drawRect(0,0,mWidthScreen,mCenterRect.top,mAreaPaint);
//        canvas.drawRect(0,mCenterRect.top,mCenterRect.left - 1,mCenterRect.bottom + 1,mAreaPaint);
//        canvas.drawRect(mCenterRect.right + 1,mCenterRect.top,mWidthScreen,mCenterRect.bottom + 1,mAreaPaint);
//        canvas.drawRect(0,mCenterRect.bottom + 1,mWidthScreen,mHeightScreen,mAreaPaint);
        //绘制边框
        canvas.drawRect(mCenterRect,mLinePaint);
        super.onDraw(canvas);
    }

    /**
     * 获取屏幕宽高，单位为px
     * @return
     */
    private Point getScreenMetrics(){
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int w_width = displayMetrics.widthPixels;
        int h_height = displayMetrics.heightPixels;
        return new Point(w_width,h_height);
    }


}

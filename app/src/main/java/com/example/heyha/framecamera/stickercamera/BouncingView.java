package com.example.heyha.framecamera.stickercamera;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.example.heyha.framecamera.R;

/**
 * Created by Heyha on 2017/2/13.
 */
public class BouncingView extends View {

    protected Point location = null;
    private Paint paint = null;

    public BouncingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setLocation(Point point){
        this.location = point;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (location != null){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.eye_l,null);
            canvas.drawBitmap(bitmap,location.x,location.y,paint);
        }
    }
}

package com.example.heyha.framecamera.stickercamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.heyha.framecamera.R;
import com.example.heyha.framecamera.stickercamera.ui.CameraSourcePreview;
import com.example.heyha.framecamera.stickercamera.ui.GraphicOverlay;

public class StickerPhotoActivity extends AppCompatActivity {

    @Bind(R.id.camera_source_preview)
    public CameraSourcePreview mPreview;
    @Bind(R.id.graphic_overlay)
    public GraphicOverlay mOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_photo);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.button, R.id.btn_sticker, R.id.flipButton})
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.button:
                break;
            case R.id.btn_sticker:
                break;
            case R.id.flipButton:
                break;
        }
    }

}

package com.example.heyha.framecamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.heyha.framecamera.framecamera.PhotoActivity;
import com.example.heyha.framecamera.stickercamera.StickerPhotoActivity;

public class MainActivity extends AppCompatActivity {

    private String filePath;

    @Bind(R.id.imageView)
    public ImageView photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button)
    public void takePhoto(){
        startActivityForResult(new Intent(this,PhotoActivity.class),1);
    }

    @OnClick(R.id.btn_sticker)
    public void takeStickerPhoto(){
        startActivityForResult(new Intent(this,StickerPhotoActivity.class),2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    filePath = data.getStringExtra("filePath");
                    if (filePath != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        photo.setImageBitmap(bitmap);
                    }
                    break;
                case 2:
                    filePath = data.getStringExtra("filePath");
                    if (filePath != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        photo.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }
}

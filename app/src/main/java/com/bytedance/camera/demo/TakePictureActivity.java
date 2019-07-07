package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;

public class TakePictureActivity extends AppCompatActivity {
    private static final String TAG = "TakePictureActivity";
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 101;

    public static final String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private File imagefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                requestPermissions(permissions,REQUEST_EXTERNAL_STORAGE);
            } else {
                takePicture();
            }
        });

    }

    private void galleryAddPic(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void takePicture() {
        //todo 打开相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagefile = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
        Log.d(TAG, "takePicture: imagefilepath="+imagefile.getAbsolutePath());
        if(imagefile!=null){
            Uri fileUri = FileProvider.getUriForFile(this,
                    "com.bytedance.camera.demo",imagefile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {
        //todo 根据imageView裁剪
        int targetW=imageView.getWidth();
        int targetH=imageView.getHeight();
        //todo 根据缩放比例读取文件，生成Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imagefile.getAbsolutePath(),options);
        int bmpW=options.outWidth;
        int bmpH=options.outHeight;
        int scaler=Math.min(bmpW/targetW,bmpH/targetH);
        options.inJustDecodeBounds=false;
        options.inSampleSize=scaler;
        options.inPurgeable=true;
        Bitmap bmp = BitmapFactory.decodeFile(imagefile.getAbsolutePath(),options);

        //todo 如果存在预览方向改变，进行图片旋转
        bmp = Utils.rotateImage(bmp,imagefile.getAbsolutePath());
        imageView.setImageBitmap(bmp);

        galleryAddPic(imagefile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                //todo 判断权限是否已经授予
                if(checkSelfPermission(permissions[0])==PackageManager.PERMISSION_GRANTED
                &&checkSelfPermission(permissions[1])==PackageManager.PERMISSION_GRANTED){
                    takePicture();
                }
                break;
            }
        }
    }
}

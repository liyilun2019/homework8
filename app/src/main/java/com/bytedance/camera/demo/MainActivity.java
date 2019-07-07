package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_CAMERA = 101;
    public static final String[] permissions=new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TakePictureActivity.class));
        });

        findViewById(R.id.btn_camera).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RecordVideoActivity.class));
        });

        findViewById(R.id.btn_custom).setOnClickListener(v -> {
            //todo 在这里申请相机、麦克风、存储的权限
            boolean flg = true;
            for(int i=0;i<permissions.length;i++){
                if(checkSelfPermission(permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    flg=false;
                    break;
                }
            }
            if (!flg) {
                requestPermissions(permissions,REQUEST_EXTERNAL_CAMERA);
            }else {
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_EXTERNAL_CAMERA){
            for(int i=0;i<permissions.length;i++){
                if(checkSelfPermission(permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
            startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
        }
    }
}

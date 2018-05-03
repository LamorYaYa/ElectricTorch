package com.example.electrictorch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author master
 */
public class MainActivity extends AppCompatActivity {

    private Camera camera;
    private CameraManager manager;
    private ImageButton mImageButton;

    private boolean isOpen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

    }

    private void initView() {
        mImageButton = findViewById(R.id.img_btn_torch);
    }

    private void initListener() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "手机没有闪光灯,开启屏幕最强亮度!", Toast.LENGTH_SHORT).show();
            mImageButton.setVisibility(View.INVISIBLE);
            screenLight();
        } else {
            mImageButton.setOnClickListener(v -> {
                if (isOpen) {
                    openTorch();
                    mImageButton.setBackgroundResource(R.mipmap.button_on);
                } else {
                    closeTorch();
                    mImageButton.setBackgroundResource(R.mipmap.button_off);
                }
                isOpen = !isOpen;
            });
        }
    }

    private void screenLight() {
        Window localWindow = this.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = 1.0f;
        localWindow.setAttributes(params);
    }

    private void openTorch() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                if (manager != null) {
                    manager.setTorchMode("0", true);
                }
            } else {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeTorch() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (manager == null) {
                    return;
                }
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera == null) {
                return;
            }
            camera.stopPreview();
            camera.release();
        }
    }

    @Override
    protected void onDestroy() {
        closeTorch();
        super.onDestroy();
    }

}

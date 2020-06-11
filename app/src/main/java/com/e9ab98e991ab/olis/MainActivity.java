package com.e9ab98e991ab.olis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.e9ab98e991ab.olis.databinding.ActivityMainBinding;
import com.e9ab98e991ab.olis.util.LongPictureCreate;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.tool.ui.ToastUtil;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private LongPictureCreate drawLongPictureUtil;
    private int REQUEST_CODE_CHOOSE = 0x000011;
    private String resultPath;
    private List<String> mCurrentSelectedPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        drawLongPictureUtil = new LongPictureCreate(MainActivity.this);
        drawLongPictureUtil.setListener(new LongPictureCreate.Listener() {
            @Override public void onSuccess(String path) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        resultPath = path;
                        binding.tvText.setText(resultPath);
                    }
                });
            }

            @Override public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(MainActivity.this.getApplicationContext(), "onFail！",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    public void btnImage(View view) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isEnableCrop(true)// 是否裁剪 true or false
                .maxSelectNum(10)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .loadImageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.MULTIPLE)
                .forResult(REQUEST_CODE_CHOOSE);//结果回调onActivityResult code
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            mCurrentSelectedPath.clear();

            final StringBuffer stringBuffer = new StringBuffer();
            for (LocalMedia paths : selectList) {
                stringBuffer.append(paths.getCutPath()).append("\n");
                mCurrentSelectedPath.add(paths.getCutPath());
            }
            runOnUiThread(() -> binding.tvText.setText(stringBuffer.toString()));
        }
    }

    public void buttonClickLong(View view) {
        if (TextUtils.isEmpty(resultPath)) {
            ToastUtil.getInstance()._short(this,"暂无长图");
            return;
        }
        ImagePreview
                .getInstance()
                .setContext(MainActivity.this)
                .setIndex(0)
                .setImage(resultPath)
                .start();
    }

    public void generateLongImages(View view) {
        if (mCurrentSelectedPath.size() <1 ) {
            ToastUtil.getInstance()._short(this,"请选择长图");
            return;
        }
        Glide.with(this).asBitmap().load("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2800997457,1841442195&fm=26&gp=0.jpg").into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                drawLongPictureUtil.setbuttomBitmap(resource);
                drawLongPictureUtil.setData(mCurrentSelectedPath);
                drawLongPictureUtil.startDraw();
            }
        });


    }


}
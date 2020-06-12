package com.e9ab98e991ab.olis.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cc.shinichi.library.tool.ui.PhoneUtil;

/**
 * @author gaoxin 2020/6/9 16:35
 * @version V1.0.0
 * @name DrawLongPictureUtil
 * @mail godfeer@aliyun.com
 * @description  TODO
 */
public class LongPictureCreate extends View {

    private final String TAG = "LongPictureCreate";
    private Context context;
    private Listener listener;

    // 图片的url集合
    private List<String> imageUrlList;
    // 保存下载后的图片url和路径键值对的链表
    private LinkedHashMap<String, String> localImagePathMap;


    // 长图的宽度，默认为屏幕宽度
    private int longPictureWidth;
    // 长图两边的间距
    private int picMargin;

    // 被认定为长图的长宽比
    private int maxSingleImageRatio = 3;
    private Bitmap buttomBitmap;

    public LongPictureCreate(Context context) {
        super(context);
        init(context);
    }

    public LongPictureCreate(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LongPictureCreate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public void removeListener() {
        this.listener = null;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        this.context = context;
        picMargin = 0;
        longPictureWidth = PhoneUtil.getPhoneWid(context);
        initView();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView() {
    }

    public void setData(List<String> imageList) {
        this.imageUrlList = imageList;
        if (this.imageUrlList == null) {
            this.imageUrlList = new ArrayList<>();
        }
        if (localImagePathMap != null) {
            localImagePathMap.clear();
        } else {
            localImagePathMap = new LinkedHashMap<>();
        }
    }

    public void startDraw() {
        // 需要先下载全部需要用到的图片（用户头像、图片等），下载完成后再进行长图的绘制操作
        new Thread(() -> {
            // 图片下载完成后，进行view的绘制
            // 模拟保存图片url、路径的键值对
            for (int i = 0; i < imageUrlList.size(); i++) {
                localImagePathMap.put(imageUrlList.get(i), imageUrlList.get(i));
            }
            // 开始绘制view
            draw();
        }).start();
    }

    private int getAllImageHeight() {
        int height = 0;
        for (int i = 0; i < imageUrlList.size(); i++) {
            int[] wh = ImageUtil.getWidthHeight(localImagePathMap.get(imageUrlList.get(i)));
            int w = wh[0];
            int h = wh[1];
            wh[0] = (longPictureWidth - (picMargin) * 2);
            wh[1] = (wh[0]) * h / w;
            float imgRatio = h / w;
            if (imgRatio > maxSingleImageRatio) {
                wh[1] = wh[0] * maxSingleImageRatio;
                Log.d(TAG, "getAllImageHeight w h > maxSingleImageRatio = " + Arrays.toString(wh));
            }
            height = height + wh[1];
        }
        height = height + imageUrlList.size();
        return height;
    }

    private Bitmap getSingleBitmap(String path) {
        int[] wh = ImageUtil.getWidthHeight(path);
        final int w = wh[0];
        final int h = wh[1];
        wh[0] = (longPictureWidth - (picMargin) * 2);
        wh[1] = (wh[0]) * h / w;
        Bitmap bitmap = null;
        try {
            // 长图，只截取中间一部分
            float imgRatio = h / w;
            if (imgRatio > maxSingleImageRatio) {
                wh[1] = wh[0] * maxSingleImageRatio;
                Log.d(TAG, "getSingleBitmap w h > maxSingleImageRatio = " + Arrays.toString(wh));
            }
            bitmap = Glide.with(context).asBitmap().load(path).centerCrop().into(wh[0], wh[1]).get();
            Log.d(TAG, "getSingleBitmap glide bitmap w h = " + bitmap.getWidth() + " , " + bitmap.getHeight());
            return bitmap;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
            paint.setAntiAlias(true);
            paint.setDither(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            Log.d(TAG, "getRoundedCornerBitmap w h = " + output.getWidth() + " × " + output.getHeight());
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private int getAllTopHeightWithIndex(int index) {

        int height = 0;
        for (int i = 0; i < index + 1; i++) {
            int[] wh = ImageUtil.getWidthHeight(localImagePathMap.get(imageUrlList.get(i)));
            int w = wh[0];
            int h = wh[1];
            wh[0] = (longPictureWidth - (picMargin) * 2);
            wh[1] = (wh[0]) * h / w;
            float imgRatio = h / w;
            if (imgRatio > maxSingleImageRatio) {
                wh[1] = wh[0] * maxSingleImageRatio;
                Log.d(TAG, "getAllImageHeight w h > maxSingleImageRatio = " + Arrays.toString(wh));
            }
            height = height + wh[1];
        }
        height = height ;
        Log.d(TAG, "---getAllTopHeightWithIndex = " + height);
        return height;
    }

    private void draw() {
        // 计算出最终生成的长图的高度 = 上、中、图片总高度、下等个个部分加起来
        int allBitmapHeight = 0;

        Bitmap last = Bitmap.createScaledBitmap(buttomBitmap, longPictureWidth - 2*picMargin,buttomBitmap.getHeight(),false);

        // 计算图片的总高度
        if (imageUrlList != null & imageUrlList.size() > 0) {
            allBitmapHeight = getAllImageHeight()+last.getHeight() ;
        }else {
            allBitmapHeight = last.getHeight() ;
        }


        // 创建空白画布
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmapAll;
        try {
            bitmapAll = Bitmap.createBitmap(longPictureWidth, allBitmapHeight, config);
        } catch (Exception e) {
            e.printStackTrace();
            config = Bitmap.Config.RGB_565;
            bitmapAll = Bitmap.createBitmap(longPictureWidth, allBitmapHeight, config);
        }
        Canvas canvas = new Canvas(bitmapAll);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        int top = 0;
        if (imageUrlList != null && imageUrlList.size() > 0) {
            Bitmap bitmapTemp;
            int imageRadius = 0;
            for (int i = 0; i < imageUrlList.size(); i++) {
                bitmapTemp = getSingleBitmap(localImagePathMap.get(imageUrlList.get(i)));
                Bitmap roundBitmap = getRoundedCornerBitmap(bitmapTemp, imageRadius);

                top = getAllTopHeightWithIndex(i - 1 );
                if (roundBitmap != null) {
                    canvas.drawBitmap(roundBitmap, picMargin, top, paint);
                }
            }
        }
        top = getAllTopHeightWithIndex(imageUrlList.size() - 1 );
        last.setDensity(bitmapAll.getDensity());
        //getAllTopHeightWithIndex(imageUrlList.size()+1 )
        canvas.drawBitmap(last, picMargin, top, paint);

        // 生成最终的文件，并压缩大小，这里使用的是：implementation 'com.github.nanchen2251:CompressHelper:1.0.5'
        try {
            String path = ImageUtil.saveBitmapBackPath(bitmapAll);
            //保存图片到本地
            savePicLocal(path);
            Log.d(TAG, "最终生成的长图路径为：" + path);
            if (listener != null) {
                listener.onSuccess(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onFail();
            }
        }
    }

    private void savePicLocal(String path) {
        float imageRatio = ImageUtil.getImageRatio(path);
        // 最终压缩后的长图宽度
        int finalCompressLongPictureWidth;
        if (imageRatio >= 10) {
            finalCompressLongPictureWidth = 750;
        } else if (imageRatio >= 5 && imageRatio < 10) {
            finalCompressLongPictureWidth = 900;
        } else {
            finalCompressLongPictureWidth = longPictureWidth;
        }
        String result;
//             由于长图一般比较大，所以压缩时应注意OOM的问题，这里并不处理OOM问题，请自行解决。
        try {
            result = new CompressHelper.Builder(context).setMaxWidth(finalCompressLongPictureWidth)
                .setMaxHeight(Integer.MAX_VALUE) // 默认最大高度为960
                .setQuality(80)    // 默认压缩质量为80
                .setFileName("长图_" + System.currentTimeMillis()) // 设置你需要修改的文件名
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+ "/长图分享/")
                .build()
                .compressToFile(new File(path))
                .getAbsolutePath();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();

            finalCompressLongPictureWidth = finalCompressLongPictureWidth / 2;
            result = new CompressHelper.Builder(context).setMaxWidth(finalCompressLongPictureWidth)
                .setMaxHeight(Integer.MAX_VALUE) // 默认最大高度为960
                .setQuality(50)    // 默认压缩质量为80
                .setFileName("长图_" + System.currentTimeMillis()) // 设置你需要修改的文件名
                .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + "/长图分享/")
                .build()
                .compressToFile(new File(path))
                .getAbsolutePath();
        }
    }

    public static Bitmap resizeImage(Bitmap origin, int newWidthOrHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidthOrHeight) / width;
        float newHeight = height * scaleWidth;
        //float scaleHeight = ((float) newHeight) / height;
        float offsetx = (newWidthOrHeight - width) / 2f;
        float offsety = (newHeight - height) / 2f;
        offsetx = newWidthOrHeight < width ? Math.abs(offsetx) : 0;
        offsety = newWidthOrHeight < width ? Math.abs(offsety) : 0;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);//, width >> 1, height >> 1

        Bitmap bitmap = Bitmap.createBitmap(origin, (int)offsetx, (int)offsety, newWidthOrHeight, (int)newHeight, matrix, false);
        Bitmap result =  Bitmap.createScaledBitmap(origin, newWidthOrHeight, (int)newHeight, false);

        if (!origin.isRecycled()) {
            origin.recycle();
        }

        return result;
    }

    public void setButtomBitmap(Bitmap buttomBitmap) {
        this.buttomBitmap = buttomBitmap;
    }

    public interface Listener {

        /**
         * 生成长图成功的回调
         *
         * @param path 长图路径
         */
        void onSuccess(String path);

        /**
         * 生成长图失败的回调
         */
        void onFail();
    }
}
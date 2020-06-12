## OLIS（Create Long Image Synthesis)
本项目是整理 处理多张图片合成并且加底部的示例
 
<img src="https://upload-images.jianshu.io/upload_images/1917623-bc8a2a616b217b7a.gif?imageMogr2/auto-orient/strip|imageView2/2/w/480/format/webp"  width="360" height="640" align="bottom" />



#### 使用方式

```java
LongPictureCreate drawLongPictureUtil = new LongPictureCreate(MainActivity.this);
drawLongPictureUtil.setListener(new LongPictureCreate.Listener() {
    @Override public void onSuccess(String path) {
        runOnUiThread(new Runnable() {
            @Override public void run() { 
                //合成长图路径
            }
        });
    }

    @Override public void onFail() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //合成失败回调
            }
        });
    }
});

```

调用方法

```java
drawLongPictureUtil.setbuttomBitmap(buttomBitmap);
drawLongPictureUtil.setData('List<String>本地路径');
drawLongPictureUtil.startDraw();
```

网络地址图片转换bitmap

```
Glide.with(this).asBitmap().load("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2800997457,1841442195&fm=26&gp=0.jpg").into(new SimpleTarget<Bitmap>() {
    @Override
    public void onResourceReady(@NonNull Bitmap buttomBitmap, @Nullable Transition<? super Bitmap> transition) {
 
    }
});
```
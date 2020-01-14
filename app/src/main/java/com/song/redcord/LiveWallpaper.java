package com.song.redcord;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.song.redcord.bean.Her;
import com.song.redcord.bean.Me;
import com.song.redcord.interfaces.RequestCallback;
import com.song.redcord.util.ColorUtil;
import com.song.redcord.util.Pref;
import com.song.redcord.util.ScreenUtil;
import com.song.redcord.util.TAG;

/**
 * 锁屏时间过长,会导致无法继续定位,需要重新拉起定位服务
 * 见高德文档
 * 目前手机设备在长时间黑屏或锁屏时CPU会休眠，
 * 这导致定位SDK不能正常进行位置更新。
 * https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
 */
public class LiveWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new RedcordEngine();
    }

    private class RedcordEngine extends Engine implements AMapLocationListener {
        private static final int MAX_DISTANCE = 600;
        private static final int OFFSET = 10;
        private static final long START_LOCATION_INTERVAL = 30000;
        private static final long LOCATION_INTERVAL = 30000;
        private static final long STOP_DELAY_INTERVAL =  2000;
        private int leftX, leftY;
        private int rightX, rightY;
        private int centerX, centerY;
        private int startX, startY;
        private int endX, endY;
        private int mapStartX, mapStartY;
        private int mapEndX, mapEndY;
        private Paint paint;
        private Paint tipsTextPaint;

        private boolean isVisible;
        private boolean isTouch;
        private boolean isUp;
        private Me me = new Me(Pref.get().getId());
        private Handler locationHandler = new Handler();
        private AMapLocationClient locationClient;
        private AMapLocationClientOption locationOption;
        private AMapLocation aMapLocation;
        private long lastLocationTime;

        private final Handler handler = new Handler();
        private final Runnable updateDisplay = new Runnable() {
            @Override
            public void run() {

                if (leftY < centerY - MAX_DISTANCE) {
                    isUp = false;
                } else if (leftY > centerY + MAX_DISTANCE) {
                    isUp = true;
                }
                if (isUp) {
                    leftY -= OFFSET;
                } else {
                    leftY += OFFSET;
                }
                rightY = 2 * centerY - leftY;
                doDraw();
            }
        };
        private RoundedBitmapDrawable bitmapDrawable;
        private final SimpleTarget<Bitmap> mapBitmapTarget = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                bitmapDrawable = RoundedBitmapDrawableFactory.create(LiveWallpaper.this.getResources(), resource);
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // 圈和线
            paint = new Paint();
            paint.setAntiAlias(true);
            // 文字
            tipsTextPaint = new Paint();
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setColor(Color.WHITE);
            tipsTextPaint.setTextSize(24);
            tipsTextPaint.setTextAlign(Paint.Align.CENTER);
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setDither(true);
            // 视图
            centerX = ScreenUtil.getWidth(App.get()) / 2;
            centerY = ScreenUtil.getHeight(App.get()) / 2;
            startX = centerX - 250;
            startY = centerY - 250;
            endX = centerX + 250;
            endY = centerY + 250;
            leftX = startX;
            leftY = centerY - 250;
            rightX = endX;
            rightY = endY - 250;
            // 地图
            mapStartX = startX - 100;
            mapStartY = startY - 100;
            mapEndX = endX + 100;
            mapEndY = endY + 100;

            me.pull(new RequestCallback() {
                @Override
                public void onSuccess() {
                    final Her her = new Her(me.getLoverId());
                    me.setLover(her);
                    her.pull(new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            if (aMapLocation != null) {
                                me.setLocation(aMapLocation);
                                me.push();
                            }
                            her.adjustDownloadMapBitmap(LiveWallpaper.this, mapBitmapTarget);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }

                @Override
                public void onFail() {

                }
            });
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            isVisible = visible;
            if (visible) {
                startLocation();
                doDraw();
            } else {
                stopLocationDelay();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            stopLocation();
        }

        private void doDraw() {
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            if (surfaceHolder == null) {
                return;
            }

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    return;
                }
                // 画背景
                float f = (leftY - centerY + MAX_DISTANCE) / (float) (2 * MAX_DISTANCE);
                f = Math.min(1 , Math.max(f, 0));
                canvas.drawColor(ColorUtil.getColor(f));

                // 画地图
                if (bitmapDrawable != null) {
                    bitmapDrawable.setCornerRadius(20);
                    bitmapDrawable.setBounds(new Rect(mapStartX, mapStartY, mapEndX, mapEndY));
                    bitmapDrawable.draw(canvas);
                }

                // 画距离
                Her her = me.getLover();
                if (her != null && !TextUtils.isEmpty(her.getLineDistance())) {
                    canvas.drawText("相距 : " + her.getLineDistance() + "  |  " + her.getUpdateTime(), centerX, mapEndY + 120, tipsTextPaint);
                    canvas.drawText("位置 : " + her.getAddress(), centerX, mapEndY + 170, tipsTextPaint);
                }

                // 画二阶贝塞尔曲线
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setColor(getColor(R.color.colorAccent));
                Path path = new Path();
                path.moveTo(startX, startY);
                path.cubicTo(leftX, leftY, rightX, rightY, endX, endY);
                canvas.drawPath(path, paint);

                // 画点
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(0);
                paint.setColor(ColorUtil.getColor(f));
                canvas.drawCircle(startX, startY, 16, paint);
                canvas.drawCircle(endX, endY, 16, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(startX, startY, 7, paint);
                canvas.drawCircle(endX, endY, 7, paint);

            } catch (Exception | OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            handler.removeCallbacks(updateDisplay);
            if (isVisible && !isTouch) {
                // Wait one frame, and redraw
                handler.postDelayed(updateDisplay, 33);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isTouch = true;
                    leftX = (int) event.getX();
                    leftY = (int) event.getY();
                    rightX = 2 * centerX - leftX;
                    rightY = 2 * centerY - leftY;
                    doDraw();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isTouch = false;
                    doDraw();
                    break;
            }

        }

        @Override
        public void onLocationChanged(final AMapLocation location) {
            Log.i(TAG.V, " ~~~~~~~~~~ 壁纸定位完成 ~~~~~~~~~~~ " + location);

            // 位置改变
            if (location != null && location.getErrorCode() == 0) {
                this.aMapLocation = location;
                me.setLocation(location);
                me.push();
            }

            final Her her = me.getLover();
            if (her == null) {
                return;
            }

            her.pull(new RequestCallback() {
                @Override
                public void onSuccess() {
                    her.adjustDownloadMapBitmap(LiveWallpaper.this, mapBitmapTarget);
                }

                @Override
                public void onFail() {

                }
            });
        }

        private void startLocation() {
            if (System.currentTimeMillis() - lastLocationTime < START_LOCATION_INTERVAL) {
                return;
            }
            lastLocationTime = System.currentTimeMillis();

            stopLocation();
            locationHandler.removeCallbacksAndMessages(null);
            locationClient = new AMapLocationClient(LiveWallpaper.this);
            locationOption = new AMapLocationClientOption();
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationOption.setInterval(LOCATION_INTERVAL);
            locationClient.setLocationListener(this);
            locationClient.setLocationOption(locationOption);
            locationClient.startLocation();
        }

        private void stopLocation() {
            if (locationClient != null) {
                locationClient.stopLocation();
                locationClient.onDestroy();
                locationClient = null;
            }
        }

        private void stopLocationDelay() {
            locationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopLocation();
                }
            }, STOP_DELAY_INTERVAL);
        }
    }

}

package com.song.redcord;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.song.redcord.bean.Her;
import com.song.redcord.bean.Lover;
import com.song.redcord.bean.Me;
import com.song.redcord.interfaces.RequestCallback;
import com.song.redcord.util.ColorUtil;
import com.song.redcord.util.Pref;
import com.song.redcord.util.ScreenUtil;

public class LiveWallpaper extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new RedcordEngine();
    }


    private class RedcordEngine extends Engine implements AMapLocationListener {
        private static final int MAX_DISTANCE = 600;
        private static final int OFFSET = 10;

        private int leftX, leftY;
        private int rightX, rightY;
        private int centerX, centerY;
        private int startX, startY;
        private int endX, endY;
        private Paint paint;

        private Paint tipsTextPaint;

        private boolean isVisible;
        private boolean isTouch;
        private boolean isUp;
        private Me me = new Me(Pref.get().getId());
        private Her her;
        private AMapLocationClient mlocationClient;
        private AMapLocationClientOption mLocationOption = null;
        private AMapLocation aMapLocation;

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

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(10);

            tipsTextPaint = new Paint();
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setColor(Color.WHITE);
            tipsTextPaint.setTextSize(24);
            tipsTextPaint.setTextAlign(Paint.Align.CENTER);
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setDither(true);

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

            me.pull(new RequestCallback() {
                @Override
                public void onSuccess() {
                    her = new Her(me.getLoverId());
                    me.setLover(her);
                    her.pull(new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            if (aMapLocation != null) {
                                me.setLocation(aMapLocation);
                                me.push();
                            }
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

            mlocationClient = new AMapLocationClient(LiveWallpaper.this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            mLocationOption.setInterval(20000);
            mlocationClient.setLocationOption(mLocationOption);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            isVisible = visible;
            if (visible) {
                mlocationClient.startLocation();
                doDraw();
            } else {
                mlocationClient.stopLocation();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mlocationClient.onDestroy();
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
                // Draw background
                float f = (leftY - centerY + MAX_DISTANCE) / (float) (2 * MAX_DISTANCE);
                f = Math.min(1 , Math.max(f, 0));
                canvas.drawColor(ColorUtil.getColor(f));

                paint.setColor(Color.WHITE);
                //画4个点
                canvas.drawCircle(startX, startY, 10, paint);
                canvas.drawCircle(endX, endY, 10, paint);
//                canvas.drawCircle(leftX, leftY, 8, paint);
//                canvas.drawCircle(rightX, rightY, 8, paint);


                // 画距离
                if (her != null && !TextUtils.isEmpty(her.getLineDistance())) {
                    canvas.save();
                    canvas.translate(centerX, centerY);
                    canvas.rotate(45);
                    canvas.drawText(her.getLineDistance(), 0, -10, tipsTextPaint);
                    canvas.restore();
                }

                //绘制连线

                //画二阶贝塞尔曲线
                paint.setColor(getColor(R.color.colorAccent));
                paint.setStyle(Paint.Style.STROKE);
                Path path = new Path();
                path.moveTo(startX, startY);
                path.cubicTo(leftX, leftY, rightX, rightY, endX, endY);
                canvas.drawPath(path, paint);

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
        public void onLocationChanged(AMapLocation location) {
            Log.i("songhang", " ~~~~~~~~~~ 壁纸定位 ~~~~~~~~~~~");
            // 位置改变
            if (location != null && location.getErrorCode() == 0) {
                this.aMapLocation = location;
                me.setLocation(location);
                me.push();
            }

            final Lover lover = me.getLover();
            if (lover == null) {
                return;
            }

            lover.pull(null);
        }
    }

}

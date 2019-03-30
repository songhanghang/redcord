package com.song.redcord;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.service.wallpaper.WallpaperService;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new RedcrodEngine();
    }

    private class RedcrodEngine extends Engine {
        private float touchDownX;
        private float touchDownY;

        private Paint guideTextPaint;
        private Paint tipsTextPaint;
        private Paint itemBackgroundPaint;
        private TextPaint itemTextPaint;

        private String[] appUsageStrings = new String[5];
        private Context context = LiveWallpaper.this;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            guideTextPaint = new Paint();
            guideTextPaint.setColor(Color.WHITE);
            guideTextPaint.setTextSize(100);
            guideTextPaint.setTextAlign(Paint.Align.CENTER);
            guideTextPaint.setAntiAlias(true);
            guideTextPaint.setDither(true);

            tipsTextPaint = new Paint();
            tipsTextPaint.setColor(Color.WHITE);
            tipsTextPaint.setTextSize(24);
            tipsTextPaint.setTextAlign(Paint.Align.CENTER);
            tipsTextPaint.setAntiAlias(true);
            tipsTextPaint.setDither(true);

            itemBackgroundPaint = new Paint();
            itemBackgroundPaint.setStyle(Paint.Style.FILL);
            itemBackgroundPaint.setAntiAlias(true);
            itemBackgroundPaint.setDither(true);

            itemTextPaint = new TextPaint();
            itemTextPaint.setTextSize(30);
            itemTextPaint.setColor(Color.WHITE);
            itemTextPaint.setAntiAlias(true);
            itemTextPaint.setDither(true);

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                doDraw();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
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
                canvas.drawColor(getColor(R.color.colorBlue));
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
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
        }
    }

}

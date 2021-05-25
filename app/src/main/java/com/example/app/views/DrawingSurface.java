package com.example.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // save painting on backgroundcolor change

    private SurfaceHolder holder;
    private Thread drawingThread;
    private boolean threadWorking = false;
    private final Object blockade = new Object();

    private Bitmap bitmap = null;
    private Path path = null;
    private final Paint paint = new Paint();
    private Canvas canvas = null;
    private int backgroundColor = Color.WHITE;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void resumeDrawing() {
        drawingThread = new Thread(this);
        threadWorking = true;
        drawingThread.start();
    }

    public void pauseDrawing() {
        threadWorking = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        synchronized (blockade) {
            Canvas canvas = new Canvas(bitmap);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path = new Path();
                    canvas.drawCircle(event.getX(), event.getY(), getPaintWidth() * 3, paint);
                    path.moveTo(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(event.getX(), event.getY());
                    canvas.drawPath(path, paint);
                    break;
                case MotionEvent.ACTION_UP:
                    canvas.drawCircle(event.getX(), event.getY(), getPaintWidth() * 3, paint);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public boolean performClick() {
        return super.performClick();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    // hmm
    public void changeBackgroundColor(int color) {
        canvas.drawColor(color);
    }

    public void clearScreen() {
        canvas.drawColor(backgroundColor);
    }

    public void setPaintColor(final int color) {
        paint.setColor(color);
    }

    public int getPaintColor() {
        return paint.getColor();
    }

    public void setPaintWidth(int width) {
        paint.setStrokeWidth(width);
    }

    public float getPaintWidth() {
        return paint.getStrokeWidth();
    }

    @Override
    public void run() {
        while (threadWorking) {
            Canvas canvas = null;
            try {
                synchronized (holder) {
                    if (!holder.getSurface().isValid()) {
                        continue;
                    }
                    canvas = holder.lockCanvas(null);
                    synchronized (blockade) {
                        if (threadWorking) {
                            canvas.drawBitmap(bitmap, 0, 0, null);
                            Log.d("dx", "xdd");
                        }
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.holder = holder;
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(backgroundColor);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        threadWorking = false;
    }
}

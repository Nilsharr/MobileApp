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

    private final SurfaceHolder holder;
    private boolean threadWorking = false;
    private final Object blockade = new Object();

    private Bitmap drawingBitmap = null;
    private Canvas drawingCanvas = null;
    private Bitmap backgroundBitmap = null;
    private Canvas backgroundCanvas = null;

    private Path path = null;
    private final Paint paint = new Paint();
    private int backgroundColor;

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
        threadWorking = true;
        Thread drawingThread = new Thread(this);
        drawingThread.start();
    }

    public void pauseDrawing() {
        threadWorking = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        synchronized (blockade) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path = new Path();
                    drawingCanvas.drawCircle(event.getX(), event.getY(), getPaintWidth() * 3, paint);
                    path.moveTo(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(event.getX(), event.getY());
                    drawingCanvas.drawPath(path, paint);
                    break;
                case MotionEvent.ACTION_UP:
                    drawingCanvas.drawCircle(event.getX(), event.getY(), getPaintWidth() * 3, paint);
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

    public void refreshBackgroundColor() {
        backgroundCanvas.drawColor(backgroundColor);
    }

    public void clearScreen() {
        drawingBitmap.eraseColor(Color.TRANSPARENT);
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
                            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
                            canvas.drawBitmap(drawingBitmap, 0, 0, null);
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("hmm", "xdd");
        super.onSizeChanged(w, h, oldw, oldh);
        if (drawingBitmap != null) {
            drawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, w, h, true);
            drawingCanvas = new Canvas(drawingBitmap);

            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, w, h, true);
            backgroundCanvas = new Canvas(backgroundBitmap);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d("dxx", "mhh");
        if (drawingBitmap == null) {
            drawingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            drawingCanvas = new Canvas(drawingBitmap);
            drawingCanvas.drawColor(Color.TRANSPARENT);

            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            backgroundCanvas = new Canvas(backgroundBitmap);
            backgroundCanvas.drawColor(backgroundColor);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        threadWorking = false;
    }

}

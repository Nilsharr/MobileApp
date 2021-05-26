package com.example.app.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
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

        // setting initial background color based on device theme
        // black background for dark theme, white for light theme
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            backgroundColor = Color.BLACK;
        } else {
            backgroundColor = Color.WHITE;
        }
    }

    public void changeBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundCanvas.drawColor(backgroundColor);
    }

    public int getBackgroundColor() {
        return backgroundColor;
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

    public void clearScreen() {
        drawingBitmap.eraseColor(Color.TRANSPARENT);
    }

    // detecting drawn path based on user input
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

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    // starting thread responsible for drawing
    public void resumeDrawing() {
        threadWorking = true;
        Thread drawingThread = new Thread(this);
        drawingThread.start();
    }

    // stopping thread responsible for drawing
    public void pauseDrawing() {
        threadWorking = false;
    }

    // drawing path drawn by the user
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
                            // drawing background color
                            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
                            // drawing path
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

    // scaling image on device orientation change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (drawingBitmap != null) {
            drawingBitmap = Bitmap.createScaledBitmap(drawingBitmap, w, h, true);
            drawingCanvas = new Canvas(drawingBitmap);

            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, w, h, true);
            backgroundCanvas = new Canvas(backgroundBitmap);
        }
    }

    // setting up bitmaps and canvases for background color and drawn shape
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (drawingBitmap == null) {
            drawingBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            drawingCanvas = new Canvas(drawingBitmap);
            drawingCanvas.drawColor(Color.TRANSPARENT);

            backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            backgroundCanvas = new Canvas(backgroundBitmap);
            backgroundCanvas.drawColor(backgroundColor);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        threadWorking = false;
    }
}

package com.example.tripleapp;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private Canvas bitmapCanvas;
    private Bitmap bitmap;
    private int currentColor = Color.BLACK;
    private float currentSize = 8f;

    public DrawingView(Context c, AttributeSet a) {
        super(c, a);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(currentColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(currentSize);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmapCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX(), y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (bitmapCanvas != null) bitmapCanvas.drawPath(path, paint);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    public void setColor(int color) {
        currentColor = color;
        paint.setColor(color);
        paint.setXfermode(null);
    }

    public void setSize(float size) { currentSize = size; paint.setStrokeWidth(size); }

    public void setEraser() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(40f);
    }

    public void clear() {
        if (bitmapCanvas != null) bitmapCanvas.drawColor(Color.WHITE);
        path.reset();
        invalidate();
    }

    public Bitmap getBitmap() {
        if (bitmap == null) return null;
        Bitmap out = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(out);
        c.drawPath(path, paint);
        return out;
    }
}
package com.aarkir.SimpleHandwritingPractice;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;

public class DrawingView extends View {
    private Path mDrawingPath;
    private Paint mDrawingPaint;
    private Paint mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    private Canvas mDrawingCanvas;
    private Bitmap mCanvasBitmap;
    private boolean mFollowingSizeChange = false;
    private boolean mFollowingColorChange = false;

    public DrawingView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }

    private void setupDrawing() {
        //mBrushSize = 30;
        //setup area for drawing interaction
        mDrawingPath = new Path();
        mDrawingPaint = new Paint();

        mDrawingPaint.setAntiAlias(true);
        //mDrawingPaint.setStrokeWidth(mBrushSize);
        mDrawingPaint.setStyle(Paint.Style.STROKE);
        mDrawingPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawingPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        //view given size
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mCanvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mDrawingCanvas = new Canvas(mCanvasBitmap);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawingPath, mDrawingPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mFollowingSizeChange && !mFollowingColorChange) {
                    mDrawingPath.moveTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mFollowingSizeChange && !mFollowingColorChange) {
                    mDrawingPath.lineTo(touchX, touchY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mFollowingSizeChange && !mFollowingColorChange) {
                    mDrawingCanvas.drawPath(mDrawingPath, mDrawingPaint);
                    mDrawingPath.reset();
                }
                mFollowingSizeChange = false;
                mFollowingColorChange = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(int newColor) {
        //set color
        invalidate();
        mDrawingPaint.setColor(newColor);
    }

    public void setBrushSize(float newSize) {
        //update size
        //mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        mDrawingPaint.setStrokeWidth(newSize);
    }

    public float getBrushSize() {
        return mDrawingPaint.getStrokeWidth();
    }

    public void setErase(boolean erase) {
        if (erase) {
            mDrawingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            mDrawingPaint.setXfermode(null);
        }
    }

    public void startNew() {
        mDrawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setFollowingSizeChange(boolean value) {
        mFollowingSizeChange = value; //switches value
    }

    public void setFollowingColorChange(boolean value) {
        mFollowingColorChange = value;
    }

    public int getPaintColor() {
        return mDrawingPaint.getColor();
    }
}

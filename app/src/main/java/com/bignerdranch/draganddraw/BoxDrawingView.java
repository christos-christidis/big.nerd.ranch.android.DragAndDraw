package com.bignerdranch.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

class BoxDrawingView extends View {

    private static final String LOG_TAG = "BoxDrawingView";

    private static final String PARENT_VIEW_STATE = "PARENT_VIEW_STATE";
    private static final String BOXES = "boxes";

    private Box mCurrentBox;
    private List<Box> mBoxes = new ArrayList<>();

    private final Paint mBoxPaint;
    private final Paint mBackgroundPaint;

    private final PointF mPrimaryFinger;
    private final PointF mSecondaryFinger;
    private int mPrimaryFingerId = -1;

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000); // semi-transparent red

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);  // off-white

        mPrimaryFinger = new PointF();
        mSecondaryFinger = new PointF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            box.draw(canvas, mBoxPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // SOS: ignore all later events if primary finger is lifted off-screen (we set mPrimaryFingerId
        // to -1 in that case). The events will keep being ignored until we lift all remaining fingers
        // off-screen and start again.
        if (mPrimaryFingerId == -1 && event.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return true;
        }

        // SOS: Also ignore any action if 3 or more fingers touch the screen (simplifies things)
        if (event.getPointerCount() > 2) {
            return true;
        }

        // SOS: valid only when action = ACTION_POINTER_DOWN or ACTION_POINTER_UP! It's used to find
        // out which finger was lifted up/brought down.
        int actionIndex = event.getActionIndex();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(LOG_TAG, "PRIMARY FINGER ON SCREEN");
                PointF startingPosition = new PointF(event.getX(), event.getY());
                mCurrentBox = new Box(startingPosition);
                mBoxes.add(mCurrentBox);
                mPrimaryFinger.set(event.getX(), event.getY());
                // SOS: First finger is always put at 0 initially
                mPrimaryFingerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                // SOS: Both fingers may have moved when ACTION_MOVE is received!
                if (event.getPointerCount() == 1) {
                    Log.i(LOG_TAG, "PRIMARY FINGER ALONE MOVING");
                    if (mCurrentBox != null) {
                        PointF newPosition = new PointF(event.getX(), event.getY());
                        mCurrentBox.setCurrent(newPosition);
                        mPrimaryFinger.set(newPosition.x, newPosition.y);
                    }
                } else {
                    Log.i(LOG_TAG, "TWO FINGERS MOVING (ROTATION)");
                    float primaryFingerNewX = event.getX(0);
                    float primaryFingerNewY = event.getY(0);
                    float secondaryFingerNewX = event.getX(1);
                    float secondaryFingerNewY = event.getY(1);

                    rotateBox(primaryFingerNewX, primaryFingerNewY, secondaryFingerNewX, secondaryFingerNewY);

                    mPrimaryFinger.set(primaryFingerNewX, primaryFingerNewY);
                    mSecondaryFinger.set(secondaryFingerNewX, secondaryFingerNewY);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG, "LAST FINGER OFF SCREEN");
                endOfGesture();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(LOG_TAG, "ACTION_CANCEL");
                endOfGesture();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(LOG_TAG, "SECONDARY FINGER ON SCREEN");
                mSecondaryFinger.set(event.getX(actionIndex), event.getY(actionIndex));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (actionIndex == 0) {
                    Log.i(LOG_TAG, "PRIMARY FINGER OFF SCREEN");
                    endOfGesture();
                } else {
                    Log.i(LOG_TAG, "SECONDARY FINGER OFF SCREEN");
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void endOfGesture() {
        mCurrentBox = null;
        mPrimaryFingerId = -1;
    }

    private float angleBetweenLines(float primaryFingerNewX, float primaryFingerNewY,
                                    float secondaryFingerNewX, float secondaryFingerNewY) {
        double angle1 = Math.atan2(mSecondaryFinger.y - mPrimaryFinger.y, mSecondaryFinger.x - mPrimaryFinger.x);
        double angle2 = Math.atan2(secondaryFingerNewY - primaryFingerNewY, secondaryFingerNewX - primaryFingerNewX);

        return (float) getDeltaAngle(Math.toDegrees(angle1), Math.toDegrees(angle2));
    }

    private double getDeltaAngle(double angle1, double angle2) {
        double from = clipAngleTo0_360(angle2);
        double to = clipAngleTo0_360(angle1);

        double deltaAngle = to - from;

        if (deltaAngle < -180) {
            deltaAngle += 360;
        } else if (deltaAngle > 180) {
            deltaAngle -= 360;
        }

        return deltaAngle;
    }

    private double clipAngleTo0_360(double angle) {
        return angle % 360;
    }

    // SOS: The math is taken from stackoverflow. Have no idea how it works
    private void rotateBox(float primaryFingerNewX, float primaryFingerNewY, float secondaryFingerNewX, float secondaryFingerNewY) {
        float angle = angleBetweenLines(primaryFingerNewX, primaryFingerNewY, secondaryFingerNewX, secondaryFingerNewY);
        mCurrentBox.addAngle(-angle);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARENT_VIEW_STATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(BOXES, (ArrayList<Box>) mBoxes);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(PARENT_VIEW_STATE));
        mBoxes = bundle.getParcelableArrayList(BOXES);
    }
}

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

    // SOS: this is called when the view is inflated from the layout file
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000); // semi-transparent red

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);  // off-white
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                mCurrentBox = new Box(current);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
            default:
                action = "ACTION_UNKNOWN";
                break;
        }

        Log.i(LOG_TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }

    // SOS: These methods will be called only if the view has an id (I give it one in the layout file)
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

package com.bignerdranch.draganddraw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

class Box implements Parcelable {

    private final PointF mOrigin;
    private PointF mCurrent;
    private final PointF mCenter;
    private float mAngle;

    Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
        mCenter = new PointF();
        mAngle = 0;
    }

    void setCurrent(PointF current) {
        mCurrent = current;
        mCenter.set((mOrigin.x + mCurrent.x) / 2, (mOrigin.y + mCurrent.y) / 2);
    }

    void addAngle(float angle) {
        mAngle += angle;
    }

    void draw(Canvas canvas, Paint boxPaint) {
        float left = Math.min(mOrigin.x, mCurrent.x);
        float right = Math.max(mOrigin.x, mCurrent.x);
        float top = Math.min(mOrigin.y, mCurrent.y);
        float bottom = Math.max(mOrigin.y, mCurrent.y);

        if (mAngle != 0) {
            canvas.save();
            canvas.rotate(mAngle, mCenter.x, mCenter.y);
        }
        canvas.drawRect(left, top, right, bottom, boxPaint);
        if (mAngle != 0) {
            canvas.restore();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mOrigin.writeToParcel(dest, 0);
        mCurrent.writeToParcel(dest, 0);
    }

    private Box(Parcel in) {
        mOrigin = new PointF();
        mCurrent = new PointF();
        mCenter = new PointF();
        mOrigin.readFromParcel(in);
        mCurrent.readFromParcel(in);
    }

    static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
        @Override
        public Box createFromParcel(Parcel source) {
            return new Box(source);
        }

        @Override
        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}

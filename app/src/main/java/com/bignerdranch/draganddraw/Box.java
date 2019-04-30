package com.bignerdranch.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

class Box implements Parcelable {

    private final PointF mOrigin;

    private PointF mCurrent;

    Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    PointF getCurrent() {
        return mCurrent;
    }

    void setCurrent(PointF current) {
        mCurrent = current;
    }

    PointF getOrigin() {
        return mOrigin;
    }

    // SOS: Parcelable methods
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

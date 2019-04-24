package com.bignerdranch.draganddraw;

import android.graphics.PointF;

class Box {

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
}

package com.bignerdranch.draganddraw;

import android.support.v4.app.Fragment;

public class DragAndDrawActivity extends SingleFragmentActivity {

    @Override
    Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}

package com.visoft.network.custom_views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.visoft.network.R;

public class CustomNavigationBar extends LinearLayout {
    private int cantItems;
    private ViewGroup[] items;

    public CustomNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public int getCantItems() {
        return cantItems;
    }

    public void setCantItems(int cantItems) {
        this.cantItems = cantItems;
        items = new ViewGroup[cantItems];
        for (int i = 0; i < cantItems; i++) {
            ViewGroup item = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.custom_nav_item, null);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            item.setLayoutParams(lp);
            setWeightSum(cantItems);
            items[i] = item;
            addView(item);
        }
    }

    public ViewGroup getItem(int i) {
        return items[i];
    }

}

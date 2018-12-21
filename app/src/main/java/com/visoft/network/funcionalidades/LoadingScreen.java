package com.visoft.network.funcionalidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.R;

public class LoadingScreen {

    private View loadingScreen;

    public LoadingScreen(Context context, ViewGroup rootView) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.loading_screen_layout, rootView);
        loadingScreen = view.findViewById(R.id.progressBarContainer);
        hide();
    }

    public void show() {
        loadingScreen.setVisibility(View.VISIBLE);
    }

    public void hide() {
        loadingScreen.setVisibility(View.GONE);
    }

}

package com.visoft.network.funcionalidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.visoft.network.R;

public class FillerRatingOptions {


    public FillerRatingOptions(ViewGroup rootView, final String[] ratings, final ListenerButtonsRating listener) {
        final int[] i = {0};
        for (String a : ratings) {
            Button button = (Button) LayoutInflater.from(rootView.getContext()).inflate(R.layout.button_review, null);
            button.setText(a);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(i[0]++);
                }
            });
            rootView.addView(button);
        }
    }

    public interface ListenerButtonsRating {
        void onClick(int result);
    }

}

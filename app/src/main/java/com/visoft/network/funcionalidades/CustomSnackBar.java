package com.visoft.network.funcionalidades;

import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.visoft.network.R;

public class CustomSnackBar {

    public static void makeText(View rootView, CharSequence c) {
        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View view = LayoutInflater.from(rootView.getContext()).inflate(R.layout.toast_layout, null);
        TextView v = view.findViewById(R.id.text);
        v.setText(c);

        layout.setBackgroundColor(rootView.getContext().getResources().getColor(R.color.transparent));
        layout.setPadding(0, 0, 0, 0);
        layout.addView(view, 0);
        snackbar.show();
    }
}

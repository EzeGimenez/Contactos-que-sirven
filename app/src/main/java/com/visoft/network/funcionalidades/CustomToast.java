package com.visoft.network.funcionalidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.visoft.network.R;

public class CustomToast {

    public static void makeText(Context context, CharSequence c) {
        Toast t = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);

        TextView v = view.findViewById(R.id.text);
        v.setText(c);
        t.setDuration(Toast.LENGTH_SHORT);
        t.setView(view);
        t.show();
    }
}

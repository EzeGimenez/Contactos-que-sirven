package com.visoft.network.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.visoft.network.R;

/**
 * Dialogo personalizado
 *
 * @author ezequiel
 */
public class CustomDialog {

    private AlertDialog.Builder builder;

    public CustomDialog(Context context) {
        builder = new AlertDialog.Builder(context, R.style.alertDialogs);
    }

    /**
     * Ingresa la view pasada por parámetro en el diálogo
     *
     * @param view view a ingresar
     * @return this, para seguir con el patrón de diseño builder
     */
    public CustomDialog setView(View view) {
        builder.setView(view);
        return this;
    }

    public CustomDialog setMessage(String a) {
        builder.setMessage(a);
        return this;
    }

    /**
     * Setea el titulo al dialogo
     *
     * @param s titulo
     * @return this, para seguir con el patrón de diseño builder
     */
    public CustomDialog setTitle(String s) {
        builder.setTitle(s);
        return this;
    }

    /**
     * Setea un botón positivo con el titulo y su listener
     *
     * @param title    titulo
     * @param listener listener
     * @return this, para seguir con el patrón de diseño builder
     */
    public CustomDialog setPositiveButton(String title, DialogInterface.OnClickListener listener) {
        builder.setPositiveButton(title, listener);
        return this;
    }

    /**
     * Setea un botón positivo con el titulo y su listener
     *
     * @param title    titulo
     * @param listener listener
     * @return this, para seguir con el patrón de diseño builder
     */
    public CustomDialog setNegativeButton(String title, DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(title, listener);
        return this;
    }

    /**
     * Muestra el diálogo
     */
    public void show() {
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

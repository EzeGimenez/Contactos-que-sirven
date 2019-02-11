package com.visoft.network.objects;

import android.content.Context;
import android.view.View;

import com.visoft.network.R;
import com.visoft.network.tab_search.ViewHolderRubro;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RubroEspecifico extends AbstractFlexibleItem<ViewHolderRubro> implements Serializable {

    private int color = 0;
    private String nombre;
    private String id;
    private int layoutRes = R.layout.rubro_button_layout;

    public RubroEspecifico(Context context, String id) {
        this.id = id;
        int resId = context.getResources().getIdentifier(id, "string", context.getPackageName());
        this.nombre = context.getString(resId);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return layoutRes;
    }

    public void setLayoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
    }

    @Override
    public ViewHolderRubro createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderRubro(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderRubro holder, int position, List<Object> payloads) {
        holder.nombre.setText(nombre);
        if (color != 0) {
            holder.c.setCardBackgroundColor(color);
        }
    }

    public String getId() {
        return id;
    }
}

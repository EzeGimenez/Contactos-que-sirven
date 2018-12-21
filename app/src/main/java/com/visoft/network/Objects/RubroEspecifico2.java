package com.visoft.network.Objects;

import android.content.Context;
import android.view.View;

import com.visoft.network.MainPageSearch.ViewHolderRubro;
import com.visoft.network.R;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RubroEspecifico2 extends AbstractFlexibleItem<ViewHolderRubro> implements Serializable {

    private String nombre;
    private String id;

    public RubroEspecifico2(Context context, String id) {
        this.id = id;

        int resId = context.getResources().getIdentifier(id, "string", context.getPackageName());
        this.nombre = context.getString(resId);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rubro_button_layout;
    }


    @Override
    public ViewHolderRubro createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolderRubro(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolderRubro holder, int position, List<Object> payloads) {
        holder.nombre.setText(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public String getId() {
        return id;
    }
}

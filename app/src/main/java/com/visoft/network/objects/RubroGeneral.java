package com.visoft.network.objects;

import android.content.Context;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.tab_search.ViewHolderRubro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.GlideApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class RubroGeneral extends AbstractFlexibleItem<ViewHolderRubro> implements Serializable {

    private ArrayList<RubroEspecifico> subRubros;
    private String nombre;
    private String id;

    public RubroGeneral(Context context, String id) {
        this.id = id;

        int resId = context.getResources().getIdentifier(id, "string", context.getPackageName());
        this.nombre = context.getString(resId);

        resId = context.getResources().getIdentifier(
                id + "ID",
                "array",
                context.getPackageName()
        );

        subRubros = new ArrayList<>();
        String[] subRubrosID = context.getResources().getStringArray(resId);
        for (String a : subRubrosID) {
            subRubros.add(new RubroEspecifico(context, a));
        }
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

        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if (holder.img != null) {
            StorageReference userRef = storage.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME + "/" + id + ".jpg");
            GlideApp.with(holder.img.getContext())
                    .load(userRef)
                    .into(holder.img);
        }
    }

    public ArrayList<RubroEspecifico> getSubRubros() {
        return subRubros;
    }

    public String getId() {
        return id;
    }
}

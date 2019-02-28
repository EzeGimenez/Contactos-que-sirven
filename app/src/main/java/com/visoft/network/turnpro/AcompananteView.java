package com.visoft.network.turnpro;

import android.view.View;

import com.visoft.network.R;
import com.visoft.network.objects.Acompanante;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;

public class AcompananteView extends AbstractFlexibleItem<ConfiguratorAcompanante.ViewHolderAcompanantes> {

    private String name, dni, tel;

    public AcompananteView(String name, String dni, String tel) {
        this.name = name;
        this.dni = dni;
        this.tel = tel;
    }

    public AcompananteView(Acompanante m) {
        this.name = m.getName();
        this.dni = m.getDni();
        this.tel = m.getPhone();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.acompanante;
    }

    @Override
    public ConfiguratorAcompanante.ViewHolderAcompanantes createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ConfiguratorAcompanante.ViewHolderAcompanantes(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ConfiguratorAcompanante.ViewHolderAcompanantes holder, int position, List<Object> payloads) {
        if (name != null) {
            holder.name.setText(name);
            holder.pic.setImageDrawable(holder.pic.getContext().getResources().getDrawable((R.drawable.profile_pic)));
        } else {
            holder.pic.setImageDrawable(holder.pic.getContext().getResources().getDrawable((R.drawable.ic_add_black_24dp)));
            holder.name.setText(holder.name.getContext().getString(R.string.add));
            holder.c.setCardBackgroundColor(holder.c.getContext().getResources().getColor(R.color.transparent));
        }
    }

    public String getDni() {
        return dni;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}

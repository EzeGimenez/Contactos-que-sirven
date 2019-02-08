package com.visoft.network.turnpro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;
import com.visoft.network.objects.Acompanante;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;


public class ConfiguratorAcompanante extends ConfiguratorTurnPro {

    private RecyclerView rv;
    private FlexibleAdapter<AcompananteView> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acompanante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.rvAcompanante);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void iniciar() {
        if (adapter == null) {
            adapter = new FlexibleAdapter<>(null);

            adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, final int position) {
                    if (adapter.getItem(position).getName() != null) {
                        CustomDialog dialog = new CustomDialog(getContext());

                        dialog.setTitle(getString(R.string.borrar_acompanante));
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                adapter.removeItem(position);
                            }
                        });
                        dialog.show();

                    } else {
                        CustomDialog dialog = new CustomDialog(getContext());

                        final View addView = getLayoutInflater().inflate(R.layout.add_acompanante_layout, null);
                        dialog.setView(addView);
                        dialog.setTitle(getString(R.string.add_acompanante));
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EditText etName = addView.findViewById(R.id.etName);
                                EditText etDni = addView.findViewById(R.id.etDNI);
                                EditText ettel = addView.findViewById(R.id.ettel);
                                if (etName.getText().length() > 0 && etDni.getText().length() > 0) {
                                    adapter.addItem(position, new AcompananteView(etName.getText().toString(), etDni.getText().toString(), ettel.getText().toString()));
                                }
                            }
                        });

                        dialog.show();
                    }

                    return false;
                }
            });

            rv.setAdapter(adapter);
            for (Acompanante m : user.getAcompanantes()) {
                adapter.addItem(new AcompananteView(m));
            }
            adapter.addItem(new AcompananteView(null, null, null));

        } else {
            rv.setAdapter(adapter);
        }
    }

    @Override
    protected void finalizar() {

        List<Acompanante> m = new ArrayList<>();

        for (AcompananteView a : adapter.getCurrentItems()) {
            m.add(new Acompanante(a.getDni(), a.getName(), a.getTel()));
        }

        m.remove(m.size() - 1);

        user.setAcompanantes(m);
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public String getDescriptor() {
        return "complete_acompanante";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }

    public static class ViewHolderAcompanantes extends FlexibleViewHolder {

        public CircleImageView pic;
        public TextView name;

        public ViewHolderAcompanantes(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            pic = view.findViewById(R.id.pic);
            name = view.findViewById(R.id.name);
        }
    }

}

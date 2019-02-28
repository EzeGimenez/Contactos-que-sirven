package com.visoft.network.turnpro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomSnackBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ConfiguratorContacto extends ConfiguratorTurnPro {

    //Componentes gráficas
    private EditText etTel1, etTel2;
    private Spinner sFecha1, sFecha2, sHora1, sHora2;

    private List<String> hrL;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_contacto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.complete_info_contacto);

        etTel1 = view.findViewById(R.id.etTel1);
        etTel2 = view.findViewById(R.id.etTel2);
        sFecha1 = view.findViewById(R.id.sFecha1);
        sFecha2 = view.findViewById(R.id.sFecha2);
        sHora1 = view.findViewById(R.id.sHora1);
        sHora2 = view.findViewById(R.id.sHora2);

        hrL = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.horas)));
        List<String> diasL = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.dias)));

        sFecha1.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, diasL));
        sFecha2.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, diasL));
        sFecha2.setSelection(4);

        sHora1.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, hrL));
        sHora1.setSelection(8);
        sHora2.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, hrL));
        sHora2.setSelection(18);
    }

    @Override
    protected void finalizar() {
        user.setTelefono1(etTel1.getText().toString());
        user.setTelefono2(etTel2.getText().toString());
        user.setDiasAtencion(
                sFecha1.getSelectedItemPosition() * 10 + sFecha2.getSelectedItemPosition());
        user.setHoraAtencion(
                sHora1.getSelectedItem().toString() + " - " + sHora2.getSelectedItem().toString());
    }

    @Override
    protected void iniciar() {
        etTel1.setText(user.getTelefono1());
        etTel2.setText(user.getTelefono2());
        if (user.getDiasAtencion() != -1) {
            sFecha1.setSelection(user.getDiasAtencion() / 10);
            sFecha2.setSelection(user.getDiasAtencion() % 10);
        }
        if (!user.getHoraAtencion().equals("")) {
            sHora1.setSelection(hrL.indexOf(user.getHoraAtencion().split(" - ")[0]));
            sHora2.setSelection(hrL.indexOf(user.getHoraAtencion().split(" - ")[1]));
        }
    }

    @Override
    public boolean canContinue() {

        if (etTel1.getText().toString().length() > 0) {
            return true;
        }

        CustomSnackBar.makeText(getView().findViewById(R.id.rootView), getString(R.string.ingrese_telefono));

        return false;
    }

    @Override
    public String getDescriptor() {
        return "complete_info_contacto";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}

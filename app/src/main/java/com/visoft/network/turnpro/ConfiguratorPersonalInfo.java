package com.visoft.network.turnpro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomSnackBar;


public class ConfiguratorPersonalInfo extends ConfiguratorTurnPro {

    private EditText etdni, etdireccionlegal, etpatente, etobrasocial;
    private CheckBox checkBoxMovilidad, checkBoxPatente, checkboxObraSocial, checkBoxCredit, checkBoxDebit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etdni = view.findViewById(R.id.etdni);
        etdireccionlegal = view.findViewById(R.id.etdireccionlegal);
        etpatente = view.findViewById(R.id.etpatente);
        etobrasocial = view.findViewById(R.id.etobrasocial);
        checkBoxMovilidad = view.findViewById(R.id.checkboxmovilidadpropia);
        checkboxObraSocial = view.findViewById(R.id.checkboxobrasocial);
        checkBoxPatente = view.findViewById(R.id.checkboxpatente);
        checkBoxCredit = view.findViewById(R.id.checkboxcredit);
        checkBoxDebit = view.findViewById(R.id.checkboxdebit);


        checkBoxPatente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etpatente.setVisibility(View.VISIBLE);
                } else {
                    etpatente.setVisibility(View.GONE);
                }
            }
        });

        checkboxObraSocial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    etobrasocial.setVisibility(View.VISIBLE);
                } else {
                    etobrasocial.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void finalizar() {
        if (etdni.getText().length() > 0) {
            user.setDni(etdni.getText().toString());
        }
        user.setDireccion(etdireccionlegal.getText().toString());
        user.setMovilidadPropia(checkBoxMovilidad.isChecked());
        user.setDebit(checkBoxDebit.isChecked());
        user.setCredit(checkBoxCredit.isChecked());

        if (checkboxObraSocial.isChecked()) {
            user.setObrasocial(etobrasocial.getText().toString());
        } else {
            user.setObrasocial(null);
        }

        if (checkBoxPatente.isChecked()) {
            user.setPatente(etpatente.getText().toString());
        } else {
            user.setPatente(null);
        }
    }

    @Override
    protected void iniciar() {
        if (user.getDni().length() > 0) {
            etdni.setText(user.getDni() + "");
        }
        etdireccionlegal.setText(user.getDireccion());
        checkBoxMovilidad.setChecked(user.isMovilidadPropia());
        checkBoxCredit.setChecked(user.isCredit());
        checkBoxDebit.setChecked(user.isDebit());

        if (user.getObrasocial() != null) {
            etobrasocial.setVisibility(View.VISIBLE);
            checkboxObraSocial.setChecked(true);
            etobrasocial.setText(user.getObrasocial());
        } else {
            etobrasocial.setVisibility(View.GONE);
            etobrasocial.setText("");
        }

        if (user.getPatente() != null) {
            etpatente.setVisibility(View.VISIBLE);
            checkBoxPatente.setChecked(true);
            etpatente.setText(user.getPatente());
        } else {
            etpatente.setVisibility(View.GONE);
            etpatente.setText("");
        }
    }

    @Override
    public boolean canContinue() {

        if (etdni.getText().length() < 4) {
            CustomSnackBar.makeText(getView().findViewById(R.id.rootView), getString(R.string.ingrese_dni));
            return false;
        }

        if (etdireccionlegal.getText().length() < 3) {
            CustomSnackBar.makeText(getView().findViewById(R.id.rootView), getString(R.string.ingrese_direccion_legal));
            return false;
        }

        if (checkBoxPatente.isChecked() && etpatente.getText().length() < 3) {
            CustomSnackBar.makeText(getView().findViewById(R.id.rootView), getString(R.string.ingrese_patente));
            return false;
        }

        if (checkboxObraSocial.isChecked() && etobrasocial.getText().length() < 3) {
            CustomSnackBar.makeText(getView().findViewById(R.id.rootView), getString(R.string.ingrese_obra_social));
            return false;
        }

        return true;
    }

    @Override
    public String getDescriptor() {
        return "complete_personal_info";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}

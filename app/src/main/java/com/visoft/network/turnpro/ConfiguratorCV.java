package com.visoft.network.turnpro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.util.Constants;

public class ConfiguratorCV extends ConfiguratorTurnPro {
    private TextView tvCaracteres;
    private EditText etCV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.escriba_descripcion);

        tvCaracteres = view.findViewById(R.id.tvCaracteres);
        etCV = view.findViewById(R.id.etCV);

        etCV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCaracteres.setVisibility(View.VISIBLE);
                tvCaracteres.setText(s.length() + "/" + Constants.MAX_CARACTERES);
                if (s.length() > Constants.MAX_CARACTERES) {
                    tvCaracteres.setTextColor(Color.RED);
                } else {
                    tvCaracteres.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void finalizar() {
        user.setCvText(etCV.getText().toString().trim());
    }

    @Override
    protected void iniciar() {
        etCV.setText(user.getCvText());
    }

    @Override
    public boolean canContinue() {
        if (etCV.getText().toString().length() <= Constants.MAX_CARACTERES) {
            return true;
        }

        return false;
    }

    @Override
    public String getDescriptor() {
        return "escriba_descripcion";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}

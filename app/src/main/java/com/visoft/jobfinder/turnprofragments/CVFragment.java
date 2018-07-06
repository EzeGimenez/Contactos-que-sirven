package com.visoft.jobfinder.turnprofragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.visoft.jobfinder.Constants;
import com.visoft.jobfinder.ProUser;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.TurnProActivity;


public class CVFragment extends Fragment {

    //COmponentes gráficas
    private TextView tvCaracteres;
    private EditText etCV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button btnNext = getActivity().findViewById(R.id.btnNext);
        btnNext.setText("Finalizar");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etCV.getText().toString();
                if (text.length() <= Constants.MAX_CARACTERES) {
                    ProUser user = ((TurnProActivity) getActivity()).getProUser();
                    user.setCvText(text);
                    ((TurnProActivity) getActivity()).saveProUser();
                }
            }
        });
    }
}

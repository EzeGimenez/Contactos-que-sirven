package com.visoft.jobfinder.turnprofragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.Util.ErrorAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContactoFragment extends Fragment {

    //Componentes gráficas
    private CheckBox cbEmail;
    private EditText etTel1, etTel2;
    private Spinner sFecha1, sFecha2, sHora1, sHora2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.complete_info_contacto);

        cbEmail = view.findViewById(R.id.cbEmail);
        etTel1 = view.findViewById(R.id.etTel1);
        etTel2 = view.findViewById(R.id.etTel2);
        sFecha1 = view.findViewById(R.id.sFecha1);
        sFecha2 = view.findViewById(R.id.sFecha2);
        sHora1 = view.findViewById(R.id.sHora1);
        sHora2 = view.findViewById(R.id.sHora2);

        List<String> hrL = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.horas)));
        List<String> diasL = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.dias)));

        sFecha1.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, diasL));
        sFecha2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, diasL));
        sFecha2.setSelection(4);

        sHora1.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, hrL));
        sHora1.setSelection(8);
        sHora2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, hrL));
        sHora2.setSelection(18);

        Bundle bundle = getArguments();
        if (bundle != null) {
            etTel1.setText(bundle.getString("tel1"));
            etTel2.setText(bundle.getString("tel2"));
            cbEmail.setChecked(bundle.getBoolean("email"));
            sFecha1.setSelection(bundle.getInt("fecha1"));
            sFecha2.setSelection(bundle.getInt("fecha2"));
            sHora1.setSelection(hrL.indexOf(bundle.getString("hr1")));
            sHora2.setSelection(hrL.indexOf(bundle.getString("hr2")));
        }

    }

    public boolean isContactInfoOk() {
        return !etTel1.getText().toString().trim().equals("");
    }

    public void setContactInfo(ProUser user) {
        user.setShowEmail(cbEmail.isChecked());
        user.setTelefono1(etTel1.getText().toString());
        user.setTelefono2(etTel2.getText().toString());
        user.setDiasAtencion(
                sFecha1.getSelectedItemPosition() * 10 + sFecha2.getSelectedItemPosition());
        user.setHoraAtencion(
                sHora1.getSelectedItem().toString() + " - " + sHora2.getSelectedItem().toString());
    }

    public void vibrate() {
        ErrorAnimator.shakeError(getContext(), etTel1);
    }
}

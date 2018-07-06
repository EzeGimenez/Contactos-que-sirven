package com.visoft.jobfinder.turnprofragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.visoft.jobfinder.Constants;
import com.visoft.jobfinder.ProUser;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.TurnProActivity;


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

        cbEmail = view.findViewById(R.id.cbEmail);
        etTel1 = view.findViewById(R.id.etTel1);
        etTel2 = view.findViewById(R.id.etTel2);
        sFecha1 = view.findViewById(R.id.sFecha1);
        sFecha2 = view.findViewById(R.id.sFecha2);
        sHora1 = view.findViewById(R.id.sHora1);
        sHora2 = view.findViewById(R.id.sHora2);

        String hrA[] = new String[24];
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hrA[i] = "0" + i + ":00";
            } else {
                hrA[i] = i + ":00";
            }
        }

        String diasA[] = getResources().getStringArray(R.array.dias);

        sFecha1.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, diasA));
        sFecha2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, diasA));
        sFecha2.setSelection(4);

        sHora1.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, hrA));
        sHora1.setSelection(8);
        sHora2.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, hrA));
        sHora2.setSelection(18);

        Button btnNext = getActivity().findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etTel1.getText().toString().trim().equals("") && !etTel2.getText().toString().trim().equals("")) {
                    ProUser user = ((TurnProActivity) getActivity()).getProUser();
                    user.setShowEmail(cbEmail.isChecked());
                    user.setTelefono1(etTel1.getText().toString());
                    user.setTelefono2(etTel2.getText().toString());
                    user.setDiasAtencion(
                            "De " + sFecha1.getSelectedItem().toString() + " a " + sFecha2.getSelectedItem().toString());
                    user.setHoraAtencion(
                            sHora1.getSelectedItem().toString() + " a " + sHora2.getSelectedItem().toString());

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    CVFragment fragment = new CVFragment();
                    transaction
                            .replace(R.id.ContainerTurnProFragments, fragment, Constants.CV_FRAGMENT_TAG)
                            .addToBackStack(Constants.CONTACTO_FRAGMENT_TAG)
                            .commit();
                } else {
                    //animate views
                }
            }
        });
    }
}

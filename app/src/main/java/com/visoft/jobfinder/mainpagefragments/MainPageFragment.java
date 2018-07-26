package com.visoft.jobfinder.mainpagefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.visoft.jobfinder.R;
import com.visoft.jobfinder.Util.Constants;

public class MainPageFragment extends Fragment implements View.OnClickListener {


    //Componentes gráficas
    private Button btnHogar, btnFamilia, btnObra, btnEventual, btnEmergencia, btnOtros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnHogar = view.findViewById(R.id.button1);
        btnFamilia = view.findViewById(R.id.button2);
        btnObra = view.findViewById(R.id.button3);
        btnEventual = view.findViewById(R.id.button4);
        btnEmergencia = view.findViewById(R.id.button5);
        btnOtros = view.findViewById(R.id.button6);

        btnHogar.setOnClickListener(this);
        btnFamilia.setOnClickListener(this);
        btnObra.setOnClickListener(this);
        btnEventual.setOnClickListener(this);
        btnEmergencia.setOnClickListener(this);
        btnOtros.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new SubRubrosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("viewTag", (String) v.getTag());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.ContainerMainFragments, fragment, Constants.SUB_RUBROS_FRAGMENT_TAG)
                .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                .commit();
    }
}

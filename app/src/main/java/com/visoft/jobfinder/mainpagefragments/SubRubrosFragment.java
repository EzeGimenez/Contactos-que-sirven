package com.visoft.jobfinder.mainpagefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visoft.jobfinder.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SubRubrosFragment extends Fragment {
    //Constants
    private static int MAX_ITEMS;
    private String tag;
    private ArrayList<String> subRubrosID, subRubros;
    //Componentes gráficas
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tag = getArguments().getString("viewTag");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_rubros, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Variables initialization
        viewPager = view.findViewById(R.id.ViewPager);
        TabLayout dots = view.findViewById(R.id.tabDots);
        dots.setupWithViewPager(viewPager, true);

        setup();

        viewPager.setAdapter(new FragmentViewPagerAdapter(getChildFragmentManager()));
    }

    private void setup() {
        TextView tvSubRubro = getView().findViewById(R.id.tvSubRubro);
        switch (tag) {
            case "button1":
                tvSubRubro.setText(getString(R.string.hogarMantenimiento));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hogarMantenimientoID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.hogarMantenimiento)));
                break;
            case "button2":
                tvSubRubro.setText(getString(R.string.serviciosFamilia));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.serviciosFamiliaID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.serviciosFamilia)));
                break;
            case "button3":
                tvSubRubro.setText(getString(R.string.obraConstruccion));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.obraConstruccionID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.obraConstruccion)));
                break;
            case "button4":
                tvSubRubro.setText(getString(R.string.personalEventual));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.personalEventualID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.personalEventual)));
                break;
            case "button5":
                tvSubRubro.setText(getString(R.string.emergencias));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.emergenciasID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.emergencias)));
                break;
            case "button6":
                tvSubRubro.setText(getString(R.string.otros));
                subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.otrosID)));
                subRubros = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.otros)));
                break;
        }
    }

    private class FragmentViewPagerAdapter extends FragmentPagerAdapter {

        public FragmentViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new RubroEspecificoMainFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("listID", subRubrosID);
            bundle.putStringArrayList("list", subRubros);
            bundle.putInt("index", position * 6); // at which index to start the buttons
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            //The amount of screens I will show
            return ((subRubrosID.size() - 1) / 6) + 1;
        }
    }
}

package com.visoft.network.mainpagefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.Util.VerticalViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class SubRubrosFragment extends Fragment {
    //Constants
    private String tag;
    private ArrayList<String> subRubrosID;

    //Componentes gráficas
    private VerticalViewPager viewPager;

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

        int id = getResources().getIdentifier(tag + "ID",
                "array",
                getActivity().getPackageName());
        subRubrosID = new ArrayList<>(Arrays.asList(getResources().getStringArray(id)));

        id = getResources().getIdentifier(tag,
                "string",
                getActivity().getPackageName());
        tvSubRubro.setText(getString(id));
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

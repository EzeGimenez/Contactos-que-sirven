package com.visoft.network.mainpagefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.R;
import com.visoft.network.Util.Constants;

public class MainPageFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);
        view.findViewById(R.id.button3).setOnClickListener(this);
        view.findViewById(R.id.button4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new SubRubrosFragment();
        Bundle bundle = new Bundle();
        bundle.putString("viewTag", (String) v.getTag());
        bundle.putBoolean("isSubSubRubro", false);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.ContainerRubroFragments, fragment, Constants.SUB_RUBROS_FRAGMENT_TAG)
                .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                .commit();
    }
}

package com.visoft.network.mainpagefragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.visoft.network.R;
import com.visoft.network.Util.Constants;

import java.util.ArrayList;

public class RubroEspecificoMainFragment extends Fragment {
    private ArrayList<String> subRubrosID;
    private int index;
    private ArrayList<Button> buttons;

    //Graphical components

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        subRubrosID = getArguments().getStringArrayList("listID");
        index = getArguments().getInt("index");
        buttons = new ArrayList<>();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rubro_especifico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttons.add((Button) view.findViewById(R.id.btn1));
        buttons.add((Button) view.findViewById(R.id.btn2));
        buttons.add((Button) view.findViewById(R.id.btn3));
        buttons.add((Button) view.findViewById(R.id.btn4));
        buttons.add((Button) view.findViewById(R.id.btn5));
        buttons.add((Button) view.findViewById(R.id.btn6));

        for (Button button : buttons) {
            button.setVisibility(View.INVISIBLE);
        }

        int j = 0;
        int i = index;

        boolean seguir = true;
        while (seguir) {
            Button button = buttons.get(j);
            button.setVisibility(View.VISIBLE);

            int id = getResources().getIdentifier(subRubrosID.get(i),
                    "string",
                    getActivity().getPackageName());
            button.setText(getString(id));
            final int finalI = i;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment shownFrag = getParentFragment().getFragmentManager().findFragmentById(R.id.ContainerRubroFragments);
                    if (shownFrag.getTag().equals(Constants.SUB_RUBROS_FRAGMENT_TAG2)) {
                        Fragment fragment = new SearchResultFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("searchQuery", subRubrosID.get(finalI));
                        bundle.putBoolean("isRubro", true);
                        fragment.setArguments(bundle);
                        getParentFragment().getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.ContainerRubroFragments, fragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                                .addToBackStack(Constants.SUB_RUBROS_FRAGMENT_TAG)
                                .commit();
                    } else {
                        Fragment fragment = new SubRubrosFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("viewTag", subRubrosID.get(finalI));
                        fragment.setArguments(bundle);
                        getParentFragment().getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.ContainerRubroFragments, fragment, Constants.SUB_RUBROS_FRAGMENT_TAG2)
                                .addToBackStack(Constants.SUB_RUBROS_FRAGMENT_TAG)
                                .commit();
                    }
                }
            });

            i++;
            j++;
            seguir = i < index + 6 && i < subRubrosID.size();
        }
    }
}

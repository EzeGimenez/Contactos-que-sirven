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
    private ArrayList<String> subRubrosID, subRubros;
    private int index;
    private ArrayList<Button> buttons;

    //Graphical components

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        subRubrosID = getArguments().getStringArrayList("listID");
        subRubros = getArguments().getStringArrayList("list");
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
            button.setText(subRubros.get(i));
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new SearchResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("subRubroID", subRubrosID.get(finalI));
                    bundle.putString("subRubro", subRubros.get(finalI));
                    fragment.setArguments(bundle);
                    (getActivity()).getSupportFragmentManager().beginTransaction()
                            .addToBackStack(Constants.SUB_RUBROS_FRAGMENT_TAG)
                            .replace(R.id.ContainerMainFragments, fragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                            .commit();
                }
            });

            i++;
            j++;
            seguir = i < index + 6 && i < subRubrosID.size();
        }
    }
}

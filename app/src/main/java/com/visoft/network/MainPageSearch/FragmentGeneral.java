package com.visoft.network.MainPageSearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.Objects.RubroGeneral;
import com.visoft.network.R;

import java.util.ArrayList;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class FragmentGeneral extends FragmentFirstTab {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fragment_specific1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        t.setActual(getTag());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        String[] aux = getResources().getStringArray(R.array.rubrosGeneralesID);
        final ArrayList<RubroGeneral> list = new ArrayList<>();
        for (String a : aux) {
            list.add(new RubroGeneral(getContext(), a));
        }

        FlexibleAdapter adapter = new FlexibleAdapter<>(list);

        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("rubro", list.get(position));

                t.advance(bundle);

                return true;
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}

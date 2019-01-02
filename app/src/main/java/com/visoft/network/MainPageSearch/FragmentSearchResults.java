package com.visoft.network.MainPageSearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.Objects.UserPro;
import com.visoft.network.Profiles.ProfileActivity;
import com.visoft.network.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentSearchResults extends FragmentFirstTab {

    private FlexibleAdapter adapter;
    private List<UserPro> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    private void populate() {
        for (UserPro p : list) {
            int id = getResources().getIdentifier(p.getRubroEspecificoEspecifico(), "string", getContext().getPackageName());
            p.setRubroNombre(getString(id));
        }

        t.setActual(getTag());

        if (getArguments() != null) {
            String rubro = getArguments().getString("rubro");
            Iterator<UserPro> it = list.iterator();
            while (it.hasNext()) {
                UserPro p = it.next();
                if (!p.filter(rubro)) {
                    it.remove();
                }
            }
        }

        adapter = new FlexibleAdapter<>(list);
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewSearchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);

        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user", list.get(position));

                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = new ArrayList<>(HolderFirstTab.getProUsers());

        populate();
    }

    public void filter(String a) {
        adapter.setFilter(a);
        adapter.filterItems();
    }

    public static class ViewHolderProUser extends FlexibleViewHolder {

        public CircleImageView img;
        public TextView tvUsername, tvRubro, tvNumReviews;
        public SimpleRatingBar ratingBar;

        public ViewHolderProUser(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            img = view.findViewById(R.id.ivProfilePic);
            tvRubro = view.findViewById(R.id.tvRubro);
            tvUsername = view.findViewById(R.id.tvUsername);
            tvNumReviews = view.findViewById(R.id.tvNumReviews);
            ratingBar = view.findViewById(R.id.ratingBar);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        list = new ArrayList<>(HolderFirstTab.getProUsers());
        setArguments(null);
        populate();
    }
}

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

import com.google.android.gms.maps.model.LatLng;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.Objects.UserPro;
import com.visoft.network.Profiles.ProfileActivity;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.AccountManager;
import com.visoft.network.funcionalidades.AccountManagerFirebase;
import com.visoft.network.funcionalidades.SearcherProUser;

import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentSearchResults extends FragmentFirstTab {

    private FlexibleAdapter adapter;
    private SearcherProUser searcherProUser;

    public void setLocation(LatLng l) {
        searcherProUser = new SearcherProUser(l);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<UserPro> list = searcherProUser.getProUsers();

        AccountManager accountManager = AccountManagerFirebase.getInstance(null);
        Iterator<UserPro> it = list.iterator();
        while (it.hasNext()) {
            UserPro p = it.next();

            int id = getResources().getIdentifier(p.getRubroEspecificoEspecifico(), "string", getContext().getPackageName());
            p.setRubroNombre(getString(id));

            if (p.equals(accountManager.getCurrentUser(1))) {
                it.remove();
            }
        }

        t.setActual(getTag());

        adapter = new FlexibleAdapter<>(list);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (getArguments() != null) {
            filter(getArguments().getString("rubro"));
        }

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
}

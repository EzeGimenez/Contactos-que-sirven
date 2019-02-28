package com.visoft.network.tab_search;

import android.content.Context;
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
import com.visoft.network.R;
import com.visoft.network.funcionalidades.HolderCurrentAccountManager;
import com.visoft.network.funcionalidades.LoadingScreen;
import com.visoft.network.funcionalidades.SearcherProUser;
import com.visoft.network.objects.UserPro;
import com.visoft.network.profiles.ProfileActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FragmentSearchResults extends FragmentFirstTab {

    private FlexibleAdapter<UserPro> adapter;
    private List<UserPro> list;
    private RecyclerView recyclerView;
    private LoadingScreen loadingScreen;
    private boolean fromRubro;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    private void populate() {

        t.setActual(getTag());

        loadingScreen = new LoadingScreen(getContext(), (ViewGroup) getView().findViewById(R.id.rootView));
        adapter = null;
        recyclerView = getView().findViewById(R.id.recyclerViewSearchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args.getString("rubro") != null) {

            fromRubro = true;
            filter(args.getString("rubro"));

        } else if (args.getString("name") != null) {

            fromRubro = false;
            filter(args.getString("name"));

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populate();
    }

    public void filter(final String a) {
        loadingScreen.show();
        if (adapter == null && fromRubro) {
            t.getSearcherProUser().getFromDatabase(new SearcherProUser.OnFinishListenerUserPro() {
                @Override
                public void onFinish(List<UserPro> l) {
                    loadingScreen.hide();

                    list = l;
                    removeCurrentUserFromList(list);
                    adapter = new FlexibleAdapter<>(list);
                    if (list.isEmpty()) {
                        int id = getResources().getIdentifier(
                                a, "string", getActivity().getPackageName()
                        );
                        noResults(getString(id));
                    } else {
                        if (isVisible()) {
                            yesResults();
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
                    }
                }
            }, a);
        } else if (adapter == null) {
            t.getSearcherProUser().getFromDatabase(new SearcherProUser.OnFinishListenerUserPro() {
                @Override
                public void onFinish(final List<UserPro> list) {
                    loadingScreen.hide();
                    removeCurrentUserFromList(list);
                    adapter = new FlexibleAdapter<>(list);
                    adapter.setFilter(a);
                    adapter.filterItems();
                    recyclerView.setAdapter(adapter);

                    if (adapter.getCurrentItems().isEmpty()) {
                        noResults(a);
                    } else {
                        yesResults();
                    }

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
            }, null);
        } else {
            loadingScreen.hide();
            adapter.setFilter(a);
            adapter.filterItems();
            if (adapter.getCurrentItems().isEmpty()) {
                noResults(a);
            } else {
                yesResults();
            }
        }
    }

    private void removeCurrentUserFromList(List<UserPro> l) {
        UserPro toremove = null;
        for (UserPro p : l) {
            if (p.getUid().equals("pro" + HolderCurrentAccountManager.getCurrent(null).getCurrentUser(1).getUid())) {
                toremove = p;
                break;
            }
        }
        l.remove(toremove);
    }

    private void noResults(String a) {
        recyclerView.setVisibility(View.GONE);
        TextView tv = getView().findViewById(R.id.tvNoResults);
        tv.setVisibility(View.VISIBLE);
        tv.setText(getString(R.string.no_results) + " '" + a + "'");
    }

    private void yesResults() {
        recyclerView.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.tvNoResults).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        setArguments(null);
    }

    public static class ViewHolderProUser extends FlexibleViewHolder {

        public CircleImageView img;
        public TextView tvUsername, tvRubro, tvNumReviews;
        public SimpleRatingBar ratingBar;
        private Context context;

        public ViewHolderProUser(Context ctx, View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.context = ctx;
            img = view.findViewById(R.id.ivProfilePic);
            tvRubro = view.findViewById(R.id.tvRubro);
            tvUsername = view.findViewById(R.id.tvUsername);
            tvNumReviews = view.findViewById(R.id.tvNumReviews);
            ratingBar = view.findViewById(R.id.ratingBar);
        }

        public Context getContext() {
            return context;
        }
    }
}
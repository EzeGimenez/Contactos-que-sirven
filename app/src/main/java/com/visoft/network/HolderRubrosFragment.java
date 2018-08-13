package com.visoft.network;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visoft.network.Util.Constants;
import com.visoft.network.mainpagefragments.MainPageFragment;
import com.visoft.network.mainpagefragments.SearchResultFragment;

public class HolderRubrosFragment extends Fragment {

    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_holder_rubros, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);

        Fragment fm = getChildFragmentManager().findFragmentByTag(Constants.MAIN_PAGE_FRAGMENT_TAG);
        if (fm == null || !fm.isAdded()) {
            if (fm == null) {
                fm = new MainPageFragment();
            }
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.ContainerRubroFragments, fm, Constants.MAIN_PAGE_FRAGMENT_TAG)
                    .commit();
        }

        final SearchResultFragment fragment = new SearchResultFragment();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (newText.length() > 0) {
                    Fragment fragment1 = fragmentManager.findFragmentById(R.id.ContainerRubroFragments);
                    if (!(fragment1 instanceof SearchResultFragment) || !fragment.isVisible()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("searchQuery", newText);
                        fragment.setArguments(bundle);
                        fragment.resetSearch();
                        fragmentManager.beginTransaction()
                                .replace(R.id.ContainerRubroFragments, fragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                                .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                                .commit();
                    } else {
                        fragment.searchForQuery(newText);
                    }
                    return true;
                }
                Fragment fragment1 = fragmentManager.findFragmentByTag(Constants.MAIN_PAGE_FRAGMENT_TAG);
                if (fragment1 == null) {
                    fragment1 = new MainPageFragment();
                }
                if (!fragment1.isAdded()) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.ContainerRubroFragments, fragment1, Constants.MAIN_PAGE_FRAGMENT_TAG)
                            .commit();
                }

                return false;
            }
        });
    }
}

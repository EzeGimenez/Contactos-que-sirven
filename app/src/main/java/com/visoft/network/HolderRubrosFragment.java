package com.visoft.network;

import android.content.Context;
import android.content.SharedPreferences;
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
        setRetainInstance(true);
        searchView = view.findViewById(R.id.searchView);
        final FragmentManager childFragmentManager = getChildFragmentManager();

        Fragment fm = childFragmentManager.findFragmentByTag(Constants.MAIN_PAGE_FRAGMENT_TAG);
        if (fm == null) {
            fm = new MainPageFragment();
        }
        if (!fm.isAdded()) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.ContainerRubroFragments, fm, Constants.MAIN_PAGE_FRAGMENT_TAG)
                    .commit();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean hasSearched = sharedPreferences.getBoolean("hasSearched", false);
        if (hasSearched) {
            Fragment searchResultFragment = new SearchResultFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchQuery", sharedPreferences.getString("searchQuery", ""));
            bundle.putBoolean("isRubro", sharedPreferences.getBoolean("isRubro", true));
            searchResultFragment.setArguments(bundle);

            childFragmentManager.beginTransaction()
                    .replace(R.id.ContainerRubroFragments, searchResultFragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                    .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                    .commit();
        }

        final SearchResultFragment fragment = new SearchResultFragment();
        final Fragment finalFm = fm;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    Fragment fragment1 = childFragmentManager.findFragmentById(R.id.ContainerRubroFragments);
                    if (!(fragment1 instanceof SearchResultFragment) || !fragment.isVisible()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("searchQuery", newText);
                        fragment.setArguments(bundle);
                        fragment.resetSearch();
                        childFragmentManager.beginTransaction()
                                .replace(R.id.ContainerRubroFragments, fragment, Constants.SEARCH_RESULT_FRAGMENT_TAG)
                                .addToBackStack(Constants.MAIN_PAGE_FRAGMENT_TAG)
                                .commit();
                    } else {
                        fragment.searchForQuery(newText);
                    }
                    return true;
                } else {
                    if (!finalFm.isAdded()) {
                        childFragmentManager.beginTransaction()
                                .replace(R.id.ContainerRubroFragments, finalFm, Constants.MAIN_PAGE_FRAGMENT_TAG)
                                .commit();
                    }
                }

                return false;
            }
        });
    }
}

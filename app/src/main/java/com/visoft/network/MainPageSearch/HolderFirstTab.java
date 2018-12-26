package com.visoft.network.MainPageSearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.visoft.network.R;

public class HolderFirstTab extends Fragment {

    private final int containerId = R.id.ContainerFirstTab;
    private FragmentManager fragmentManager;

    private FragmentFirstTab fragmentGeneral, fragmentEspecifico1, fragmentEspecifico2, fragmentSearchResult;
    private String actual;

    private String idGeneral = "general";
    private String idSearchResult = "search";
    private SearchView.OnQueryTextListener listenerSearchView;

    public void setLocation(LatLng l) {
        iniciarFragments();
        ((FragmentSearchResults) fragmentSearchResult).setLocation(l);
    }

    public void iniciarFragments() {
        if (fragmentSearchResult == null) {
            fragmentSearchResult = new FragmentSearchResults();
            fragmentEspecifico1 = new FragmentSpecific1();
            fragmentGeneral = new FragmentGeneral();
            fragmentEspecifico2 = new FragmentSpecific2();

            fragmentSearchResult.setHolder(this);
            fragmentGeneral.setHolder(this);
            fragmentEspecifico2.setHolder(this);
            fragmentEspecifico1.setHolder(this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getChildFragmentManager();
        iniciarFragments();

        listenerSearchView = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (s.length() > 0) {

                    if (actual != null && !actual.equals(idSearchResult)) {

                        Bundle bundle = new Bundle();

                        fragmentSearchResult.setArguments(bundle);

                        fragmentManager
                                .beginTransaction()
                                .addToBackStack(actual)
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(containerId, fragmentSearchResult, idSearchResult)
                                .commit();

                    } else {
                        ((FragmentSearchResults) fragmentSearchResult).filter(s);
                    }
                } else {

                    fragmentManager.popBackStack();

                }

                return false;
            }
        };

        fragmentManager.beginTransaction()
                .add(containerId, fragmentGeneral, idGeneral).commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_holder_first_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BACK PRESS HANDLING
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    back();
                    return true;
                }
                return false;
            }
        });

        //SEARCH VIEW
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(listenerSearchView);
    }

    public void back() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            getActivity().onBackPressed();
        }

    }

    public void advance(Bundle bundle) {
        String idEspecifico1 = "esp1";
        String idEspecifico2 = "esp2";
        if (actual.equals(idGeneral)) {

            fragmentEspecifico1.setArguments(bundle);

            fragmentManager
                    .beginTransaction()
                    .addToBackStack(idGeneral)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(containerId, fragmentEspecifico1, idEspecifico1)
                    .commit();

        } else if (actual.equals(idEspecifico1)) {

            fragmentEspecifico2.setArguments(bundle);

            fragmentManager
                    .beginTransaction()
                    .addToBackStack(idEspecifico1)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(containerId, fragmentEspecifico2, idEspecifico2)
                    .commit();

        } else if (actual.equals(idEspecifico2)) {

            fragmentSearchResult.setArguments(bundle);

            fragmentManager
                    .beginTransaction()
                    .addToBackStack(idEspecifico2)
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(containerId, fragmentSearchResult, idSearchResult)
                    .commit();
        }
    }

    public void setActual(String actual) {
        this.actual = actual;
    }
}

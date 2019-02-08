package com.visoft.network.turnpro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.visoft.network.R;
import com.visoft.network.objects.RubroEspecifico;

import java.util.ArrayList;
import java.util.Iterator;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class ConfiguratorRubro extends ConfiguratorTurnPro {

    private final String TAG_GENERAL = "GENERAL_TAG", TAG_ESP_1 = "ESP1_TAG";

    private RecyclerView rvSelectedRubros;
    private TabLayout tabLayout;
    private int current;
    private FlexibleAdapter<RubroEspecifico> adapterSelectedRubros;

    private RubroGeneralFragment rubroGeneral;
    private RubroEspecificoFragment rubroEspecifico1;

    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rubroGeneral = new RubroGeneralFragment();
        rubroEspecifico1 = new RubroEspecificoFragment();

        rubroGeneral.setParent(this);
        rubroEspecifico1.setParent(this);

        fragmentManager = getChildFragmentManager();

        return inflater.inflate(R.layout.configurator_rubro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSelectedRubros = view.findViewById(R.id.rvSelectedRubros);
        tabLayout = view.findViewById(R.id.tabLayout);
        current = 0;
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        fragmentManager.beginTransaction()
                .add(R.id.ContainerSelector, rubroGeneral, TAG_GENERAL)
                .commit();
    }

    @Override
    protected void iniciar() {
        if (adapterSelectedRubros == null) {
            ArrayList<RubroEspecifico> rubros = new ArrayList<>();

            for (String s : user.getRubros()) {
                RubroEspecifico r = new RubroEspecifico(getContext(), s);
                r.setLayoutRes(R.layout.user_selected_rubros);
                rubros.add(r);
            }

            adapterSelectedRubros = new FlexibleAdapter<>(rubros);
            adapterSelectedRubros.addListener(new FlexibleAdapter.OnItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position) {
                    adapterSelectedRubros.removeItem(position);

                    return true;
                }
            });


        }

        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSelectedRubros.setLayoutManager(lm);
        rvSelectedRubros.setAdapter(adapterSelectedRubros);
    }

    @Override
    public boolean canContinue() {

        if (adapterSelectedRubros.getItemCount() > 0) {
            return true;
        }

        Toast.makeText(getContext(), getString(R.string.selecciona_un_rubro), Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public String getDescriptor() {
        return "selecciona_area_trabajo";
    }

    @Override
    public boolean handleBackPress() {

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            tabLayout.getTabAt(--current).select();
            return true;
        }

        return false;
    }

    @Override
    protected void finalizar() {
        ArrayList<String> aux = new ArrayList<>();
        for (RubroEspecifico r : adapterSelectedRubros.getCurrentItems()) {
            aux.add(r.getId());
        }
        user.setRubro(aux);

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }

    public void avanzar(Bundle bundle) {
        Fragment current = getChildFragmentManager().findFragmentById(R.id.ContainerSelector);
        switch (current.getTag()) {
            case TAG_GENERAL:

                rubroEspecifico1.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(TAG_GENERAL)
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.ContainerSelector, rubroEspecifico1, TAG_ESP_1)
                        .commit();

                this.current = 1;
                tabLayout.getTabAt(1).select();

                break;
            case TAG_ESP_1:

                String id = bundle.getString("rubro");
                Iterator<RubroEspecifico> it = adapterSelectedRubros.getCurrentItems().iterator();
                boolean found = false;
                while (!found && it.hasNext()) {
                    if (it.next().getId().equals(id)) {
                        found = true;
                    }
                }

                if (!found) {
                    RubroEspecifico r = new RubroEspecifico(getContext(), id);
                    r.setLayoutRes(R.layout.user_selected_rubros);
                    adapterSelectedRubros.addItem(r);
                    rvSelectedRubros.smoothScrollToPosition(adapterSelectedRubros.getItemCount());

                    fragmentManager.popBackStack();
                    fragmentManager.popBackStack();

                    this.current = 0;
                    tabLayout.getTabAt(0).select();
                }
                break;
        }
    }
}

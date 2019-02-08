package com.visoft.network.turnpro;

import android.support.v4.app.Fragment;

import com.visoft.network.objects.UserPro;

public abstract class ConfiguratorTurnPro extends Fragment {

    protected UserPro user;

    public void setUser(UserPro u) {
        this.user = u;
    }

    @Override
    public void onResume() {
        super.onResume();
        iniciar();
    }

    @Override
    public void onPause() {
        super.onPause();
        finalizar();
    }

    protected abstract void finalizar();

    protected abstract void iniciar();

    public abstract boolean canContinue();

    public abstract String getDescriptor();

    public abstract boolean handleBackPress();
}

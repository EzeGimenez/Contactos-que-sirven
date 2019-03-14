package com.visoft.network.funcionalidades;

public class HolderCurrentAccountManager {
    private static AccountManager current;

    public static AccountManager getCurrent(AccountManager.ListenerRequestResult l) {
        current.addListener(l);
        return current;
    }

    public static void setCurrent(AccountManager current) {
        HolderCurrentAccountManager.current = current;
    }
}

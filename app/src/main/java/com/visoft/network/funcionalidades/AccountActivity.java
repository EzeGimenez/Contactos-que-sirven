package com.visoft.network.funcionalidades;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class AccountActivity extends AppCompatActivity {

    public abstract void onRequestResult(boolean result, int requestCode, Bundle data);
}

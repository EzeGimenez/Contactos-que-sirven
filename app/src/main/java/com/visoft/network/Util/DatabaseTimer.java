package com.visoft.network.Util;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DatabaseTimer {
    private CountDownTimer timer;
    private int time;
    private Activity act;
    private FirebaseAuth mAuth;
    private boolean signOut;

    public DatabaseTimer(int secs, final Activity act, final boolean signOut) {
        this.act = act;
        this.time = secs * 1000;
        mAuth = FirebaseAuth.getInstance();
        this.signOut = signOut;

        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (signOut)
                    mAuth.signOut();
                Toast.makeText(act, "Revisa tu conexión", Toast.LENGTH_SHORT).show();
                act.finish();
            }
        }.start();
    }

    public void cancel() {
        timer.cancel();
    }

}

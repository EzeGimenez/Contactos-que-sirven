package com.visoft.network;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.visoft.network.sign_in.SignInActivity;
import com.visoft.network.sign_in.SignInChoose;
import com.visoft.network.util.Constants;

public class WelcomeScreen extends Fragment implements View.OnClickListener {

    private TextView tvCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCount = view.findViewById(R.id.tvCounter);

        FirebaseDatabase.getInstance().getReference().child(Constants.COUNTER_CONTRACTS).child("cant")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = dataSnapshot.getValue(Integer.class);

                        ValueAnimator animator = new ValueAnimator();
                        animator.setObjectValues(Integer.parseInt(tvCount.getText().toString()), count);// here you set the range, from 0 to "count" value
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                tvCount.setText(String.valueOf(animation.getAnimatedValue()));
                            }
                        });
                        animator.setDuration(3000); // here you set the duration of the anim
                        animator.start();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        view.findViewById(R.id.btnSignIn).setOnClickListener(this);
        view.findViewById(R.id.btnTutorial).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment;

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction
                .addToBackStack(getTag());

        switch (view.getId()) {
            case R.id.btnSignIn:
                fragment = new SignInChoose();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(SignInActivity.containerID, fragment)
                        .commit();
                break;
            case R.id.btnTutorial:

                Intent intent = new Intent(getActivity(), TutorialActivity.class);
                startActivity(intent);
                break;
        }
    }

}
package com.visoft.jobfinder.mainpagefragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.visoft.jobfinder.ProUser;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.misc.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String subRubroID, subRubro;
    private DatabaseReference database;
    private DatabaseReference databaseUsers;
    private ArrayList<ProUser> results;
    private CountDownTimer timer;
    private boolean finishedGettingUser, finishedLooking;

    //UI components
    private ListView listView;
    private TextView tvResultsFor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        databaseUsers = database.child(Constants.FIREBASE_USERS_CONTAINER_NAME);

        Bundle args = getArguments();
        if (args != null) {
            subRubroID = args.getString("subRubroID");
            subRubro = args.getString("subRubro");
            // results = blablalbb;
        }


        //UI comoponents initialization
        listView = view.findViewById(R.id.ListViewResult);
        tvResultsFor = view.findViewById(R.id.tvResultsFor);

        tvResultsFor.setText(getString(R.string.resultsFor) + " " + subRubro);

        if (results == null && subRubroID != null) {
            getResults();
        }
    }

    private void getResults() {
        results = new ArrayList<>();
        showLoadingScreen();
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                hideLoadingScreen();
                showSnackBar(getString(R.string.revisaConexion));
                getActivity().onBackPressed();
            }
        };
        finishedGettingUser = false;
        finishedLooking = false;
        database.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME).child(subRubroID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timer.cancel();
                hideLoadingScreen();
                HashMap<String, Boolean> map = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (map != null) {
                    for (String k : map.keySet()) {
                        getProUserFromUID(k);
                    }
                    finishedLooking = true;
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getProUserFromUID(String uid) {
        finishedGettingUser = false;
        databaseUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProUser user = dataSnapshot.getValue(ProUser.class);
                results.add(user);
                finishedGettingUser = true;
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        if (finishedLooking && finishedGettingUser) {
            listView.setAdapter(new ListViewSearchAdapter(getContext(), R.layout.profile_search_result_row, results));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    private void showLoadingScreen() {
        ConstraintLayout progressBar = getView().findViewById(R.id.progressBarContainer);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        ConstraintLayout progressBar = getView().findViewById(R.id.progressBarContainer);
        progressBar.setVisibility(View.GONE);
    }

    private void showSnackBar(String msg) {
        Snackbar.make(getActivity().findViewById(R.id.rootContainer),
                msg, Snackbar.LENGTH_SHORT).show();
    }

    private class ListViewSearchAdapter extends ArrayAdapter<ProUser> {


        public ListViewSearchAdapter(@NonNull Context context, int resource, @NonNull List<ProUser> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.profile_search_result_row, null);
            TextView tvUsername = view.findViewById(R.id.tvUsername);
            TextView tvRubro = view.findViewById(R.id.tvRubro);
            RatingBar ratingBar = view.findViewById(R.id.ratingBar);
            ProUser user = results.get(position);
            tvUsername.setText(user.getUsername());
            tvRubro.setText(subRubro);
            ratingBar.setRating(user.getRating());

            return view;
        }
    }
}

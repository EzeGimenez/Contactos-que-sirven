package com.visoft.jobfinder.mainpagefragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.jobfinder.MainActivity;
import com.visoft.jobfinder.Objects.ProUser;
import com.visoft.jobfinder.Objects.User;
import com.visoft.jobfinder.ProfileActivity;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.misc.Constants;
import com.visoft.jobfinder.misc.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SearchResultFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String subRubroID, subRubro, searchQuery;
    private DatabaseReference database;
    private DatabaseReference databaseUsers;
    private ArrayList<ProUser> results;
    private CountDownTimer timer;
    private LocationManager locationManager;
    private Location location;
    private int i = 0;
    private int j = 0;
    private SharedPreferences sharedPref;
    private SearchableAdapter adapter;
    private boolean isRunning;

    //UI components
    private ListView listView;
    private TextView tvResultsFor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null) {
            location = ((MainActivity) getActivity()).getLocation();
        }

        sharedPref = getContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = Database.getDatabase().getReference();
        databaseUsers = database.child(Constants.FIREBASE_USERS_CONTAINER_NAME);

        isRunning = true;

        Bundle args = getArguments();
        if (args != null) {
            subRubroID = args.getString("subRubroID");
            subRubro = args.getString("subRubro");
            searchQuery = args.getString("searchQuery");
        }

        //UI comoponents initialization
        listView = view.findViewById(R.id.ListViewResult);
        tvResultsFor = view.findViewById(R.id.tvResultsFor);


        if (subRubroID != null) {
            getResultsFromSubArea();
            tvResultsFor.setText(getString(R.string.resultsFor) + " " + subRubro);
        } else if (searchQuery != null) {
            searchForQuery(searchQuery);
        }
    }

    public void resetSearch() {
        adapter = null;
    }

    public void searchForQuery(final String a) {
        tvResultsFor.setText(getString(R.string.resultsFor) + " " + a);

        sharedPref.edit().putString("searchRequest", a).putBoolean("isRubro", false).commit();

        if (adapter == null) {
            results = new ArrayList<ProUser>();
            databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if (user == null || user.getIsPro()) {
                            ProUser proUser = ds.getValue(ProUser.class);
                            if (proUser != null && !proUser.getUid().equals(mAuth.getCurrentUser().getUid()) && proUser.getUsername().toLowerCase().contains(a.toLowerCase())) {
                                results.add(proUser);
                            }
                        }
                    }
                    i = j = 0;
                    setAdapter();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            adapter.getFilter().filter(a);
        }
    }

    private void getResultsFromSubArea() {
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

        database.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME).child(subRubroID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timer.cancel();
                String uid = mAuth.getCurrentUser().getUid();
                HashMap<String, Boolean> map = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (map != null) {
                    for (String k : map.keySet()) {
                        if (!k.equals(uid)) {
                            getProUserFromUID(k);
                            i++;
                        }
                    }
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getProUserFromUID(String uid) {
        databaseUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                j++;
                ProUser user = dataSnapshot.getValue(ProUser.class);
                if (user != null) {
                    results.add(user);
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAdapter() {
        if (i == j) {
            if (results.size() > 0) {
                Collections.sort(results, new ProUserComparator());
                if (subRubroID != null) {
                    sharedPref.edit().putString("searchRequest", subRubroID).putBoolean("isRubro", true).commit();
                } else {
                    sharedPref.edit().putString("searchRequest", searchQuery).putBoolean("isRubro", false).commit();
                }
                adapter = new SearchableAdapter(getContext(), results);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("user", results.get(position));
                        startActivity(intent);
                    }
                });
            }
            if (isRunning) {
                hideLoadingScreen();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle args = getArguments();
        if (args != null) {
            subRubroID = args.getString("subRubroID");
            subRubro = args.getString("subRubro");
            searchQuery = args.getString("searchQuery");
        }
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

    private class ProUserComparator implements Comparator<ProUser> {

        @Override
        public int compare(ProUser p1, ProUser p2) {
            int score = 0;


            if (p2.getNumberReviews() + 10 < p1.getNumberReviews()) {
                score--;
            } else if (p1.getNumberReviews() + 10 <= p2.getNumberReviews()) {
                score++;
            }

            if (p2.getRating() + 0.5 < p1.getRating()) {
                score--;
            } else if (p1.getRating() + 0.5 < p2.getRating()) {
                score++;
            }

            if (location != null) {
                float[] distanceP1 = new float[1];
                float[] distanceP2 = new float[1];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), p1.getMapCenterLat(), p1.getMapCenterLng(), distanceP1);
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), p2.getMapCenterLat(), p2.getMapCenterLng(), distanceP2);

                int minDistance = Constants.MIN_DISTANCE;
                boolean distanciaP1 = distanceP1[0] <= minDistance;
                boolean distanciaP2 = distanceP2[0] <= minDistance;

                if (!distanciaP1 && distanciaP2) {
                    return 1;
                } else if (distanciaP1 && !distanciaP2) {
                    return -1;
                }

                if (distanceP2[0] - 5 * 1000 > distanceP1[0]) {
                    score--;
                } else if (distanceP1[0] - 5 * 1000 > distanceP2[0]) {
                    score++;
                }
            }
            return score;


            //return (int) ((p1.getNumberReviews() - p2.getNumberReviews()) + 5 * (p1.getCalidad() - p2.getCalidad()));
        }
    }

    private class SearchableAdapter extends BaseAdapter implements Filterable {

        private List<ProUser> originalData;
        private List<ProUser> filteredData;
        private LayoutInflater inflater;
        private ItemFilter mFilter = new ItemFilter();

        public SearchableAdapter(Context context, List<ProUser> data) {
            this.filteredData = data;
            this.originalData = data;
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return filteredData.size();
        }

        public Object getItem(int position) {
            return filteredData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.profile_search_result_row, null);

                holder = new ViewHolder();
                holder.tvUsername = convertView.findViewById(R.id.tvUsername);
                holder.tvRubro = convertView.findViewById(R.id.tvRubro);
                holder.tvNumReviews = convertView.findViewById(R.id.tvNumReviews);
                holder.ratingBar = convertView.findViewById(R.id.ratingBar);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ProUser user = filteredData.get(position);
            int id = getResources().getIdentifier(user.getRubroEspecifico(),
                    "string",
                    getActivity().getPackageName());
            String subRubro = getResources().getString(id);
            holder.tvUsername.setText(user.getUsername());
            holder.tvRubro.setText(subRubro);
            holder.tvNumReviews.setText(user.getNumberReviews() + " " + getString(R.string.reviews));
            holder.ratingBar.setRating(user.getRating());

            return convertView;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ViewHolder {
            TextView tvUsername, tvRubro, tvNumReviews;
            SimpleRatingBar ratingBar;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<ProUser> list = originalData;

                int count = list.size();
                final ArrayList<ProUser> nlist = new ArrayList<ProUser>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getUsername();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<ProUser>) results.values;
                notifyDataSetChanged();
            }

        }
    }
}

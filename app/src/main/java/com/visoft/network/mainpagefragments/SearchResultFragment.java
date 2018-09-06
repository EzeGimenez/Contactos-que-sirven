package com.visoft.network.mainpagefragments;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Pair;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.visoft.network.MainActivity;
import com.visoft.network.Objects.ProUser;
import com.visoft.network.Objects.User;
import com.visoft.network.ProfileActivity;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.Util.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchResultFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String searchQuery;
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
    private boolean isRunning, isRubro;

    //UI components
    private ListView listView;

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
            isRubro = args.getBoolean("isRubro");
            searchQuery = args.getString("searchQuery");
        }

        //UI components initialization
        listView = view.findViewById(R.id.ListViewResult);

        if (isRubro) {
            getResultsFromSubArea();
        } else {
            searchForQuery(searchQuery);
        }
    }

    public void searchForQuery(final String a) {
        if (adapter == null) {
            results = new ArrayList<ProUser>();
            databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        User user = ds.getValue(User.class);

                        if (user == null || user.getIsPro()) {
                            ProUser proUser = ds.getValue(ProUser.class);
                            if (proUser != null) {
                                int id = getResources().getIdentifier(proUser.getRubroEspecificoEspecifico(),
                                        "string",
                                        getActivity().getPackageName());
                                String rubro = getString(id).toLowerCase();

                                boolean noUsuario = !proUser.getUid().equals(mAuth.getCurrentUser().getUid());
                                boolean contieneRubro = rubro.contains(a.toLowerCase());
                                boolean contieneNombre = proUser.getUsername().toLowerCase().contains(a.toLowerCase());
                                if (noUsuario && (contieneNombre || contieneRubro)) {
                                    results.add(proUser);
                                }
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
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                showSnackBar(getString(R.string.revisaConexion));
                getActivity().onBackPressed();
            }
        };

        database.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME).child(searchQuery).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        if (i > Constants.MAX_RESULTS_SIZE) {
                            break;
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
        if (i == j && isRunning) {
            if (results.size() > 0) {

                LinkedHashSet<ProUser> linkedHashSet = new LinkedHashSet<>();
                linkedHashSet.addAll(results);
                results.clear();
                results.addAll(linkedHashSet);
                Collections.sort(results, new ProUserComparator());

                sharedPref.edit().putString("searchQuery", searchQuery).putBoolean("isRubro", isRubro).commit();
                adapter = new SearchableAdapter(getContext(), results);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("user", results.get(position));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    Pair.create(view.findViewById(R.id.ratingBar), "ratingBar"),
                                    Pair.create(view.findViewById(R.id.tvUsername), "username"),
                                    Pair.create(view.findViewById(R.id.ivProfilePic), "ivPic"));

                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                });
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
            isRubro = args.getBoolean("isRubro");
            searchQuery = args.getString("searchQuery");
        }
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
                    score -= 2;
                } else if (distanceP1[0] - 5 * 1000 > distanceP2[0]) {
                    score += 2;
                }
            }
            return score;
        }
    }

    public void resetSearch() {
        results = new ArrayList<ProUser>();
        adapter = null;
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
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.profile_search_result_row, null);

                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.ivProfilePic);
                holder.tvUsername = convertView.findViewById(R.id.tvUsername);
                holder.tvRubro = convertView.findViewById(R.id.tvRubro);
                holder.tvNumReviews = convertView.findViewById(R.id.tvNumReviews);
                holder.ratingBar = convertView.findViewById(R.id.ratingBar);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ProUser user = filteredData.get(position);
            String packageName = Objects.requireNonNull(getActivity()).getPackageName();
            String subRubroEsp = "";
            String subRubroEspEsp = "";

            if (packageName != null) {
                int id = getResources().getIdentifier(user.getRubroEspecifico(),
                        "string",
                        packageName);
                subRubroEsp = getResources().getString(id);

                id = getResources().getIdentifier(user.getRubroEspecificoEspecifico(),
                        "string",
                        packageName);
                subRubroEspEsp = getResources().getString(id);
            }

            holder.tvUsername.setText(user.getUsername());
            holder.tvRubro.setText(subRubroEsp + " - " + subRubroEspEsp);
            holder.tvNumReviews.setText(user.getNumberReviews() + " " + getString(R.string.reviews));
            holder.ratingBar.setRating(user.getRating());

            if (user.getHasPic()) {
                StorageReference storage = FirebaseStorage.getInstance().getReference();

                StorageReference userRef = storage.child(Constants.FIREBASE_USERS_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");
                GlideApp.with(getContext())
                        .load(userRef)
                        .into(holder.ivPic);
            } else {
                holder.ivPic.setImageDrawable(getResources().getDrawable(R.drawable.profile_pic));
            }

            return convertView;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ViewHolder {
            CircleImageView ivPic;
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

                String username, rubro;
                int rubroID;

                for (int i = 0; i < count; i++) {
                    username = list.get(i).getUsername();
                    rubro = list.get(i).getRubroEspecifico();
                    rubroID = getResources().getIdentifier(rubro,
                            "string",
                            getActivity().getPackageName());
                    rubro = getResources().getString(rubroID).toLowerCase();

                    String[] name = username.toLowerCase().split(" ");
                    String[] aSplitted = filterString.toLowerCase().split(" ");
                    boolean contieneNombre = false;

                    int x = 0, y;

                    while (!contieneNombre && x < name.length) {
                        y = 0;
                        while (!contieneNombre && y < aSplitted.length) {
                            contieneNombre |= name[x].contains(aSplitted[y]);
                            y++;
                        }
                        x++;
                    }

                    if (contieneNombre || rubro.toLowerCase().contains(filterString)) {
                        if (!nlist.contains(list.get(i))) {
                            nlist.add(list.get(i));
                        }
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

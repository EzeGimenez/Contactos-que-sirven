package com.visoft.network.funcionalidades;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.visoft.network.objects.User;
import com.visoft.network.objects.UserPro;
import com.visoft.network.util.Constants;
import com.visoft.network.util.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearcherProUser {

    private DatabaseReference ref;
    private Gson gson;
    private LatLng location;

    public SearcherProUser() {
        gson = GsonerUser.getGson();

        ref = Database.getDatabase()
                .getReference();
    }

    public void getFromDatabase(final OnFinishListenerUserPro listener, final String rubroId) {
        final String mock = "data refresh";

        if (rubroId != null) {
            ref.child(Constants.FIREBASE_RUBRO_CONTAINER_NAME).child(rubroId).child("mock").setValue(mock).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ref.child("mock").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ref
                                    .child(Constants.FIREBASE_RUBRO_CONTAINER_NAME)
                                    .child(rubroId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final List<String> proUsersUID = new ArrayList<>();
                                            final List<UserPro> proUsers = new ArrayList<>();

                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                if (d != null) {
                                                    String aux = d.getKey();
                                                    if (!aux.equals("mock")) {
                                                        proUsersUID.add(aux);
                                                    }
                                                }
                                            }

                                            if (proUsersUID.isEmpty()) {
                                                listener.onFinish(new ArrayList<UserPro>());
                                            } else {
                                                final int[] fix = {0};
                                                for (String aux : proUsersUID) {
                                                    ref
                                                            .child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME)
                                                            .child(aux)
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    String json = dataSnapshot.getValue(String.class);
                                                                    UserPro u = (UserPro) gson.fromJson(json, User.class);
                                                                    if (u != null) {
                                                                        proUsers.add(u);
                                                                    } else {
                                                                        fix[0]++;
                                                                    }
                                                                    if (proUsers.size() == proUsersUID.size() - fix[0]) {

                                                                        Collections.sort(proUsers, new ProUserComparator());
                                                                        listener.onFinish(proUsers);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    });
                }
            });
        } else {
            ref.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME)
                    .child("mock")
                    .setValue(mock)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ref.child("mock")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ref
                                                    .child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            ArrayList<UserPro> list = new ArrayList<>();
                                                            String json;
                                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                                json = d.getValue(String.class);
                                                                if (!json.equals(mock)) {
                                                                    list.add((UserPro) gson.fromJson(d.getValue(String.class), User.class));
                                                                }
                                                            }
                                                            Collections.sort(list, new ProUserComparator());
                                                            listener.onFinish(list);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
        }
    }

    public void setLocation(LatLng l) {
        location = l;
    }

    public interface OnFinishListenerUserPro {
        void onFinish(List<UserPro> list);
    }

    private class ProUserComparator implements Comparator<UserPro> {

        @Override
        public int compare(UserPro p1, UserPro p2) {
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
                Location.distanceBetween(location.latitude, location.longitude, p1.getMapCenterLat(), p1.getMapCenterLng(), distanceP1);
                Location.distanceBetween(location.latitude, location.longitude, p2.getMapCenterLat(), p2.getMapCenterLng(), distanceP2);

                int minDistance = Constants.MIN_DISTANCE;
                boolean distanciaP1 = distanceP1[0] <= minDistance;
                boolean distanciaP2 = distanceP2[0] <= minDistance;

                if (!distanciaP1 && distanciaP2) {
                    return -1;
                } else if (distanciaP1 && !distanciaP2) {
                    return 1;
                }

                if (distanceP2[0] - 5 * 1000 > distanceP1[0]) {
                    score += 2;
                } else if (distanceP1[0] - 5 * 1000 > distanceP2[0]) {
                    score -= 2;
                }
            }
            return score;
        }
    }
}

package com.visoft.network.objects;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.visoft.network.R;

public class ViewHolderChats extends RecyclerView.ViewHolder {
    private TextView tvTimeStamp;
    private TextView textView;
    private MapView mapView;
    private GoogleMap map;
    private Marker marker;

    private LatLng pos;

    public ViewHolderChats(View itemView) {
        super(itemView);

        mapView = itemView.findViewById(R.id.mapView);
        textView = itemView.findViewById(R.id.tvText);
        tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);

        mapView.onCreate(null);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (pos != null) {
                    marker = map.addMarker(new MarkerOptions().position(pos));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 11));
                } else {
                    marker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
                }
            }
        });
    }

    public void setPosition(LatLng pos) {
        if (map != null) {
            Log.e("AAAA", pos.toString());
            marker = map.addMarker(new MarkerOptions().position(pos));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 11));
            marker.setPosition(pos);
        } else {
            this.pos = pos;
        }
    }

    public void setTimeStamp(long timeStamp) {
        tvTimeStamp.setText(DateFormat.format("HH:mm",
                timeStamp));
    }

    public void setText(String txt) {
        textView.setText(txt);
    }

    public void enableText() {
        textView.setVisibility(View.VISIBLE);
    }

    public void enableMap() {
        mapView.onResume();
        mapView.setVisibility(View.VISIBLE);
    }

    public void disableText() {
        textView.setVisibility(View.GONE);
    }

    public void disableMap() {
        mapView.onPause();
        mapView.setVisibility(View.GONE);
    }
}
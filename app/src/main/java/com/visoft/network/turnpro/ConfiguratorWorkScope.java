package com.visoft.network.turnpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.MapHighlighter;

public class ConfiguratorWorkScope extends ConfiguratorTurnPro implements OnMapReadyCallback {
    private GoogleMap map;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.work_scope_fragment, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        MapHighlighter mapHighlighter = new MapHighlighter(getContext(), map);
        mapHighlighter.highlightMap(null);
        iniciar();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void finalizar() {
        user.setMapCenterLat(map.getCameraPosition().target.latitude);
        user.setMapCenterLng(map.getCameraPosition().target.longitude);
        user.setMapZoom(map.getCameraPosition().zoom);
    }

    @Override
    protected void iniciar() {
        if (map != null) {
            LatLng current = new LatLng(user.getMapCenterLat(), user.getMapCenterLng());
            if (current.latitude == 0 && current.longitude == 0) {
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-36.581373, -65.517662)));
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, user.getMapZoom()));
            }
        }
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public String getDescriptor() {
        return "selecciona_rango_trabajo";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}

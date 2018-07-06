package com.visoft.jobfinder.turnprofragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.visoft.jobfinder.MapHighlighter;
import com.visoft.jobfinder.ProUser;
import com.visoft.jobfinder.R;

public class WorkScopeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private MapView mapView;
    private Bundle savedInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.work_scope_fragment, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText("Selecciona el rango de trabajo");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        savedInstance = new Bundle();
        double latStart = map.getCameraPosition().target.latitude;
        double lngStart = map.getCameraPosition().target.longitude;
        float zoom = map.getCameraPosition().zoom;
        savedInstance.putDouble("latStart", latStart);
        savedInstance.putDouble("lngStart", lngStart);
        savedInstance.putFloat("zoomStart", zoom);

    }

    private void iniciarUI() {
        MapHighlighter mapHighlighter = new MapHighlighter(getContext(), map);
        mapHighlighter.highlightMap(getResources().getColor(R.color.primaryAccentTransparent));
        if (savedInstance != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(savedInstance.getDouble("latStart"),
                            savedInstance.getDouble("lngStart")),
                    savedInstance.getFloat("zoomStart")));
        }
    }


    public void setCameraBounds(ProUser user) {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        user.setMapBound1Lat(bounds.southwest.latitude);
        user.setMapBound1Long(bounds.southwest.longitude);
        user.setMapBound2Lat(bounds.northeast.latitude);
        user.setMapBound2Long(bounds.northeast.longitude);
        user.setMapZoom(map.getCameraPosition().zoom);
        user.setMapCenterLat(bounds.getCenter().latitude);
        user.setMapCenterLng(bounds.getCenter().longitude);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        iniciarUI();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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

package com.visoft.jobfinder.turnprofragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.visoft.jobfinder.Constants;
import com.visoft.jobfinder.ProUser;
import com.visoft.jobfinder.R;
import com.visoft.jobfinder.TurnProActivity;

public class WorkScopeFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap map;
    private MapView mapView;

    private static final int EARTH_RADIUS = 6371;

    private static LatLng getCoords(double lat, double lng, GoogleMap map) {
        LatLng latLng = new LatLng(lat, lng);

        Projection proj = map.getProjection();
        Point p = proj.toScreenLocation(latLng);
        p.set(p.x, p.y + 0);

        return proj.fromScreenLocation(p);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.work_scope_fragment, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    private void iniciarUI() {
        highlightMap(map, getResources().getColor(R.color.primaryAccentTransparent));
    }

    public void highlightMap(final GoogleMap map, int fillColor) {

        if (map != null) {

            double lat = map.getCameraPosition().target.latitude;
            double lng = map.getCameraPosition().target.longitude;

            MarkerOptions options = new MarkerOptions();
            options.position(getCoords(lat, lng, map)).anchor(0.5f, 0.5f);
            options.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(fillColor)));
            options.alpha(1);

            final Marker marker = map.addMarker(options);
            marker.setDraggable(true);

            // I used this listener to make align the circle center
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition paramCameraPosition) {
                    LatLng centerOfMap = map.getCameraPosition().target;
                    marker.setPosition(centerOfMap);
                }
            });
        }
    }

    private Bitmap getBitmap(int fillColor) {

        // fill color
        Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint1.setColor(fillColor);
        paint1.setStyle(Paint.Style.FILL);

        // stroke color
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(getResources().getColor(R.color.primaryAccent));
        paint2.setStyle(Paint.Style.STROKE);

        // circle radius - 200 meters
        int radius = 300;

        // create empty bitmap
        Bitmap b = Bitmap
                .createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        c.drawCircle(radius, radius, radius, paint1);
        c.drawCircle(radius, radius, radius, paint2);

        return b;
    }

    private void setCameraBounds() {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        ProUser user = ((TurnProActivity) getActivity()).getProUser();
        user.setMapBound1Lat(bounds.southwest.latitude);
        user.setMapBound1Long(bounds.southwest.longitude);
        user.setMapBound2Lat(bounds.northeast.latitude);
        user.setMapBound2Long(bounds.northeast.longitude);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnNext = getActivity().findViewById(R.id.btnNext);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameraBounds();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ContactoFragment fragment = new ContactoFragment();

                transaction
                        .replace(R.id.ContainerTurnProFragments, fragment, Constants.CONTACTO_FRAGMENT_TAG)
                        .addToBackStack(Constants.WORK_SCOPE_FRAGMENT_TAG)
                        .commit();
            }
        });
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

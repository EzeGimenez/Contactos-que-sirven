package com.visoft.jobfinder.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.visoft.jobfinder.R;

public class MapHighlighter {
    private Context context;
    private GoogleMap map;

    public MapHighlighter(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
    }

    private static LatLng getCoords(double lat, double lng, GoogleMap map) {
        LatLng latLng = new LatLng(lat, lng);

        Projection proj = map.getProjection();
        Point p = proj.toScreenLocation(latLng);
        p.set(p.x, p.y + 0);

        return proj.fromScreenLocation(p);
    }

    public void highlightMap(LatLng target) {
        if (map != null) {

            MarkerOptions options;
            if (target == null) {
                double lat = map.getCameraPosition().target.latitude;
                double lng = map.getCameraPosition().target.longitude;

                options = new MarkerOptions();
                options.position(getCoords(lat, lng, map)).anchor(0.5f, 0.5f);
            } else {
                options = new MarkerOptions();
                options.position(target).anchor(0.5f, 0.5f);
            }

            int px = context.getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
            Bitmap mapCircleBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mapCircleBitmap);
            Drawable shape = context.getResources().getDrawable(R.drawable.map_circle);
            shape.setBounds(0, 0, mapCircleBitmap.getWidth(), mapCircleBitmap.getHeight());
            shape.draw(canvas);

            options.icon(BitmapDescriptorFactory.fromBitmap(mapCircleBitmap));
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
}

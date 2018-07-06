package com.visoft.jobfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    public void highlightMap(int fillColor) {
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
        paint2.setColor(context.getResources().getColor(R.color.primaryAccent));
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
}

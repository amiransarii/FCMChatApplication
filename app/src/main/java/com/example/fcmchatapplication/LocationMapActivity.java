package com.example.fcmchatapplication;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationMapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent_Map;

    @Override
    protected int getContentView() {
        return R.layout.activity_location_map;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        showBackArrow();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        intent_Map = getIntent();
        if (intent_Map != null) {
            double currentUserLat = intent_Map.getDoubleExtra(getResources().getString(R.string.current_user_latitude), 0.0);
            double currentUserlng = intent_Map.getDoubleExtra(getResources().getString(R.string.current_user_longitude), 0.0);
            double chatUserLat = intent_Map.getDoubleExtra(getResources().getString(R.string.chat_user_latitude), 0.0);
            double chatUserLng = intent_Map.getDoubleExtra(getResources().getString(R.string.chat_user_longitude), 0.0);
            String chatuser = intent_Map.getStringExtra(getResources().getString(R.string.const_chat_user));
            drawRoute(currentUserLat, currentUserlng, chatUserLat, chatUserLng, chatuser);
        }

    }

    private void drawRoute(double currentUserlat, double currentUserlng, double chatUserlat, double chatUserlng, String chatUser) {
        LatLng currentUserlatLng = new LatLng(currentUserlat, currentUserlng);
        Marker marker_CurrentUser = mMap.addMarker(new MarkerOptions().position(new LatLng(currentUserlat, currentUserlng)).anchor(0.5f, 0.5f)
                .title("You")
                .snippet("I am current ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        marker_CurrentUser.showInfoWindow();


        Marker marker_ChatUser = mMap.addMarker(new MarkerOptions().position(new LatLng(chatUserlat, chatUserlng)).anchor(0.5f, 0.5f)
                .title(chatUser)
                .snippet("I am Chat User ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        marker_ChatUser.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentUserlatLng, 14.0f));

         if(isNetworkConnected()){
             LatLng point1 = new LatLng(currentUserlat, currentUserlng);
             LatLng point2 = new LatLng(chatUserlat, chatUserlng);
             PolylineOptions polylineOptions = new PolylineOptions();
             polylineOptions.color(Color.BLUE);
             polylineOptions.width(3);

             List<LatLng> points = new ArrayList<>();
             points.add(point1);
             points.add(point2);
             polylineOptions.addAll(points);
             mMap.addPolyline(polylineOptions);
         }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}

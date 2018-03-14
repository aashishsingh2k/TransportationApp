package pkali.transportationapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private String currAdd = "";
    private Marker m;
    private GoogleMap mMap;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap = googleMap;
        LatLng sydney = new LatLng(37.7766048, -122.3943629);

        /*googleMap.setMyLocationEnabled(true);
        Location myLocation = googleMap.getMyLocation();  //Nullpointer exception.........
        LatLng myLatLng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());*/

        m = googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in SF").draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        geocoder = new Geocoder(this, Locale.getDefault());
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {


            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker = m;
                LatLng currPos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                Log.v("lol", "ended");
                LatLng currentPosition = marker.getPosition();

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); //1 num of possible location returned
                } catch (IOException e) {
                    System.out.println("No such address!");
                }

                if(addresses != null) {
                    String add = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    currAdd = add;
                }

                marker.setTitle(currAdd);
                marker.showInfoWindow();
                //mMap.addMarker(new MarkerOptions().position(currPos)
                // .title(currAdd).draggable(true));

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker = m;
                helper();
            }
        });

    }

    public void helper() {
        Intent i = new Intent(this, nextMapActivity.class);
        i.putExtra("current address", currAdd);
        i.putExtra("lat", m.getPosition().latitude);
        i.putExtra("lon", m.getPosition().longitude);
        startActivity(i);
    }


    /*@Override
    public void onMarkerDragStart(Marker marker) {
        marker = m;
        LatLng currPos = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
        mMap.addMarker(new MarkerOptions().position(currPos)
                .draggable(true));

        Log.v("lol", "started");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        marker = m;
        LatLng currPos = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
        mMap.addMarker(new MarkerOptions().position(currPos)
                .draggable(true));
        Log.v("lol", "dragging...");

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        marker = m;
        LatLng currPos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        Log.v("lol", "ended");
        LatLng currentPosition = marker.getPosition();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); //1 num of possible location returned
        } catch (IOException e) {
            System.out.println("No such address!");
        }

        String add = "check addresses list!";
        if(addresses != null) {
            add = addresses.get(0).toString();
        }
        currAdd = add;
        marker.setTitle(currAdd);
        marker.showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(currPos)
                .title(currAdd).draggable(true));

    }*/
}


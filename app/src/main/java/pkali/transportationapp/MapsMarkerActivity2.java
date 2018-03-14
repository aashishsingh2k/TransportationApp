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
public class MapsMarkerActivity2 extends AppCompatActivity
        implements OnMapReadyCallback {

    private String currAdd = "";
    private Marker m;
    private GoogleMap mMap;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps2);
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
        Double LatSrc = getIntent().getDoubleExtra("lat", 1);
        Double LonSrc = getIntent().getDoubleExtra("lon", 1);
        LatLng sydney = new LatLng(LatSrc, LonSrc);

        /*googleMap.setMyLocationEnabled(true);
        Location myLocation = googleMap.getMyLocation();  //Nullpointer exception.........
        LatLng myLatLng = new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude());*/

        m = googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("current location").draggable(true));
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

                if (addresses != null) {
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
        Intent inte = getIntent();
        Double LatSrc = inte.getDoubleExtra("lat", 1);
        Double LonSrc = inte.getDoubleExtra("lon", 1);
        String src = inte.getStringExtra("current src");
        Intent i = new Intent(this, nextnextMapActivity.class);
        i.putExtra("current dest", currAdd);
        i.putExtra("LatSrc", LatSrc);
        i.putExtra("LonSrc", LonSrc);
        i.putExtra("LatDest", m.getPosition().latitude);
        i.putExtra("LonDest", m.getPosition().longitude);
        i.putExtra("current src", src);
        startActivity(i);
    }
}





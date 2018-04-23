package pkali.transportationapp.Price;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DirectionsResult;
import com.google.android.gms.maps.model.LatLng;
import android.location.Address;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.List;

import pkali.transportationapp.R;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class PublicTransitActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private String currAdd = "";
    private Marker m;
    private GoogleMap mMap;
    Geocoder geocoder;
    private String src;
    private String dest;
    private String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_public_transit);

        geocoder = new Geocoder(this);

        Intent i  = getIntent();

        src = i.getStringExtra("Source");
        dest = i.getStringExtra("Destination");
        resultText = i.getStringExtra("Result");

        TextView tv = (TextView) findViewById(R.id.textView2);

        tv.setText(resultText);

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

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        try {
            DirectionsResult results = PublicTransport.getTransitResult(src, dest);
            m = PublicTransport.addMarkersToMap(results, mMap);
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {


                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });

            List<Address> lst = geocoder.getFromLocationName(src,1);

            LatLng srcLatLng = new LatLng(lst.get(0).getLatitude(), lst.get(0).getLongitude());

            Log.v("Prasanna bef", srcLatLng.toString());

            List<Address> lstDest = geocoder.getFromLocationName(src,1);

            LatLng destLatLng = new LatLng(lstDest.get(0).getLatitude(), lstDest.get(0).getLongitude());

            LatLngBounds bnd = new LatLngBounds(srcLatLng, destLatLng);

            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(bnd.getCenter(), 15));

//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                    new LatLng(srcLatLng.latitude(),
//                            mLastKnownLocation.getLongitude()), 15));


            Log.v("Prasanna", Double.toString(srcLatLng.latitude) + Double.toString(srcLatLng.longitude));
//
//            List<Address> lst = geocoder.getFromLocationName(src,1);
//            List<Address> lst2 = geocoder.getFromLocationName(dest,1);
//
//            float latDiff = (float) Math.abs(lst.get(0).getLatitude() - lst2.get(0).getLatitude());
//            float lonDiff = (float) Math.abs(lst.get(0).getLongitude() - lst2.get(0).getLongitude());
//
//            LatLngBounds bnd = new LatLngBounds(new LatLng(lst.get(0).getLatitude(),
//                    lst.get(0).getLongitude()), new LatLng(lst2.get(0).getLatitude(),
//                    lst2.get(0).getLongitude()));
//
//            Log.v("Prasanna",
//                    Double.toString(lst2.get(0).getLatitude() + lst2.get(0).getLongitude()));
//
//            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(bnd.getCenter(), 1.0f));

        } catch(Exception e){

        }

    }

}


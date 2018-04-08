package pkali.transportationapp.Price;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.maps.model.DirectionsResult;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import pkali.transportationapp.R;

/**
 * Created by aashishsingh on 2/25/18.
 */

public class PriceActivity extends AppCompatActivity {
    private String src;
    private String dest;
    private Double latitudeSrc;
    private Double longitudeSrc;
    private Double latitudeDest;
    private Double longitudeDest;
    private String SrcAdd;
    private String DestAdd;
    private final int mainActivity_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sortby_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("JzHNYTU35r0S")
                .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                .build();

        Intent i = getIntent();
        Double LatSrc = i.getDoubleExtra("LatSrc", 1);
        latitudeSrc = LatSrc;
        Double LonSrc = i.getDoubleExtra("LonSrc", 1);
        longitudeSrc = LonSrc;
        Double LatDest = i.getDoubleExtra("LatDest", 1);
        latitudeDest = LatDest;
        Double LonDest = i.getDoubleExtra("LonDest", 1);
        longitudeDest = LonDest;
        src = i.getStringExtra("current src");
        SrcAdd = src;
        dest = i.getStringExtra("current dest");
        DestAdd = dest;

        LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button);
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(LatSrc, LonSrc) //4th st SF
                .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();

        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("lOY0hMpn4LNLlK-Si1Jjis7UITCe9Hi7")
                // required for enhanced button features
                .setServerToken("T_g5Hpuy-W40l99PSxwF_894ICvzNqzKf1CJidNU")
                // required for implicit grant authentication
                .setRedirectUri("http://localhost:8000")
                // optional: set sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);

        // get the context by invoking ``getApplicationContext()``, ``getContext()``, ``getBaseContext()`` or ``this`` when in the activity class
        RideRequestButton requestButton = new RideRequestButton(getApplicationContext());
        // get your layout, for instance:
        ConstraintLayout layout =  findViewById(R.id.layout_main);
        layout.addView(requestButton);

        RideParameters rideParams = new RideParameters.Builder()
                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                .setDropoffLocation(
                        LatSrc, LatDest, src, src)
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(LonSrc, LonDest, dest, dest)
                .build();
        /*.setDropoffLocation(
                        LatDest, LonDest, dest, dest)
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(LatSrc, LonSrc, src, src)
                .build();*/
        // set parameters for the RideRequestButton instance
        requestButton.setRideParameters(rideParams);

        ServerTokenSession session = new ServerTokenSession(config);
        requestButton.setSession(session);

        requestButton.loadRideInformation();

        TextView tv = (TextView) findViewById(R.id.src_dest_text);
        tv.setText("Source address: " + src + "; Destination Address: " + dest);
    }

    public void OnClickBack(View view) {
        finish();
    }

    public void onStartButtonClick(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String level = spinner.getSelectedItem().toString();
        if(level.equals("Price")) {
            Intent i = new Intent(this, PriceSortActivity.class);
            i.putExtra("current src", SrcAdd);
            i.putExtra("current dest", DestAdd);
            i.putExtra("LatDest", latitudeDest);
            i.putExtra("LonDest", longitudeDest);
            i.putExtra("LatSrc", latitudeSrc);
            i.putExtra("LonSrc", longitudeSrc);
            startActivityForResult(i, mainActivity_ID);
        }
        if(level.equals("ETA")) {
            Intent i = new Intent(this, ETASortActivity.class);
            i.putExtra("current src", SrcAdd);
            i.putExtra("current dest", DestAdd);
            i.putExtra("LatDest", latitudeDest);
            i.putExtra("LonDest", longitudeDest);
            i.putExtra("LatSrc", latitudeSrc);
            i.putExtra("LonSrc", longitudeSrc);
            startActivityForResult(i, mainActivity_ID);
        }

    }

    public void OnClickTransit(View view){
        DirectionsResult result = PublicTransport.getTransitResult(src, dest);

        Button transit = findViewById(R.id.transit);

        try {
            transit.setText(PublicTransport.getEndLocationTitle(result));
//            Intent i = new Intent(this, PublicTransitActivity.class);
//            i.putExtra("Source", src);
//            i.putExtra("Destination", dest);
//            startActivity(i);
        } catch(Exception e){
            transit.setText("Oops! no public transport available at that destination!");
            e.printStackTrace();
        }
    }

}

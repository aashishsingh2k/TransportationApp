package pkali.transportationapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

/**
 * Created by aashishsingh on 2/25/18.
 */

public class PriceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("JzHNYTU35r0S")
                .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                .build();

        Intent i = getIntent();
        Double LatSrc = i.getDoubleExtra("LatSrc", 1);
        Double LonSrc = i.getDoubleExtra("LonSrc", 1);
        Double LatDest = i.getDoubleExtra("LatDest", 1);
        Double LonDest = i.getDoubleExtra("LonDest", 1);
        String src = i.getStringExtra("current src");
        String dest = i.getStringExtra("current dest");

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
                        LatDest, LonDest, dest, dest)
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(LatSrc, LonSrc, src, src)
                .build();
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

}

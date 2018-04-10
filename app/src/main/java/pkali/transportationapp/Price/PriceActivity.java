package pkali.transportationapp.Price;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.maps.model.DirectionsResult;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.lyft.networking.LyftApiFactory;
import com.lyft.networking.apiObjects.CostEstimate;
import com.lyft.networking.apiObjects.CostEstimateResponse;
import com.lyft.networking.apiObjects.Eta;
import com.lyft.networking.apiObjects.EtaEstimateResponse;
import com.lyft.networking.apiObjects.RideTypesResponse;
import com.lyft.networking.apis.LyftPublicApi;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.TimeEstimate;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pkali.transportationapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by aashishsingh on 2/25/18.
 */

public class PriceActivity extends AppCompatActivity {
    private String src;
    private String dest;
    private ArrayList<Ride> rideOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Intent i = getIntent();
        double LatSrc = i.getDoubleExtra("LatSrc", 1);
        double LonSrc = i.getDoubleExtra("LonSrc", 1);
        double LatDest = i.getDoubleExtra("LatDest", 1);
        double LonDest = i.getDoubleExtra("LonDest", 1);
        src = i.getStringExtra("current src");
        dest = i.getStringExtra("current dest");

        initializeLyft(LatDest, LonDest, LatSrc, LonSrc);
        initializeUber(LatDest, LonDest, LatSrc, LonSrc);

        TextView tv = (TextView) findViewById(R.id.src_dest_text);
        tv.setText("Source address: " + src + "; Destination Address: " + dest);
    }

    private void initializeUber(double LatDest, double LonDest, double LatSrc, double LonSrc) {
        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("lOY0hMpn4LNLlK-Si1Jjis7UITCe9Hi7")
                // required for enhanced button features
                .setServerToken("T_g5Hpuy-W40l99PSxwF_894ICvzNqzKf1CJidNU")
                // required for implicit grant authentication
                .setRedirectUri("http://localhost:8000")
                // optional: set sandbox as operating environment
                //.setEnvironment(SessionConfiguration.Environment.SANDBOX)
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

        RidesService service = UberRidesApi.with(session).build().createService();
        Response<PriceEstimatesResponse> priceResponse;
        Response<TimeEstimatesResponse> timeResponse;
        //Response<List<Product>> response = service.getProducts().execute();
        try {

            priceResponse = service.getPriceEstimates((float) LonSrc, (float) LatSrc, (float) LatDest, (float) LonDest).execute();
            List<PriceEstimate> prices = priceResponse.body().getPrices();
            int highEst, lowEst;
            for (PriceEstimate p : prices) {
                timeResponse = service.getPickupTimeEstimate((float) LatSrc, (float) LonSrc, p.getProductId()).execute();
                TimeEstimate rideEta = timeResponse.body().getTimes().get(0);
                if (p.getHighEstimate() == null) {
                    highEst = 0;
                } else {
                    highEst = p.getHighEstimate() * 100;
                }
                if (p.getLowEstimate() == null) {
                    lowEst = 0;
                } else {
                    lowEst = p.getLowEstimate() * 100;
                }
                Log.d("UBER", p.getDisplayName());
                rideOptions.add(new Ride(p.getDisplayName(), p.getDistance(), p.getDuration(), highEst, lowEst, rideEta.getEstimate()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeLyft(double LatDest, double LonDest, final double LatSrc, final double LonSrc) {
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("JzHNYTU35r0S")
                .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                .build();

        LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button);
        lyftButton.setApiConfig(apiConfig);

        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                .setPickupLocation(LatSrc, LonSrc) //4th st SF
                .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.load();

        final LyftPublicApi lyftPublicApi = new LyftApiFactory(apiConfig).getLyftPublicApi();

        Call<CostEstimateResponse> costEstimateCall = lyftPublicApi.getCosts(LatSrc, LonDest, null,LatDest, LonDest);

        costEstimateCall.enqueue(new Callback<CostEstimateResponse>() {
            @Override
            public void onResponse(Call<CostEstimateResponse> call, Response<CostEstimateResponse> response) {
                List<CostEstimate> result = response.body().cost_estimates;
                for (final CostEstimate c : result) {
                    Call<EtaEstimateResponse> etaEstimateResponseCall = lyftPublicApi.getEtas(LatSrc, LonSrc, c.ride_type);
                    etaEstimateResponseCall.enqueue(new Callback<EtaEstimateResponse>() {
                        @Override
                        public void onResponse(Call<EtaEstimateResponse> call, Response<EtaEstimateResponse> response) {
                            List<Eta> etaResult = response.body().eta_estimates;
                            Log.d("Lyft", c.display_name);
                            rideOptions.add(new Ride(c.display_name, (float) c.estimated_distance_miles.doubleValue(), c.estimated_duration_seconds,
                                    c.estimated_cost_cents_max, c.estimated_cost_cents_min, etaResult.get(0).eta_seconds));
                        }

                        @Override
                        public void onFailure(Call<EtaEstimateResponse> call, Throwable t) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<CostEstimateResponse> call, Throwable t) {}
        });

    }

    public void OnClickBack(View view) {
        finish();
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

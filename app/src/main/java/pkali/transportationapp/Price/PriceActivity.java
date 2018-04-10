package pkali.transportationapp.Price;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import com.lyft.networking.LyftApiFactory;
import com.lyft.networking.apiObjects.CostEstimate;
import com.lyft.networking.apiObjects.CostEstimateResponse;
import com.lyft.networking.apiObjects.Eta;
import com.lyft.networking.apiObjects.EtaEstimateResponse;
import com.lyft.networking.apis.LyftPublicApi;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.model.TimeEstimate;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pkali.transportationapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HEAD;

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
    private ArrayList<Ride> rideOptions = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sortby_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

        initializeLyft(LatDest, LonDest, LatSrc, LonSrc);
        initializeUber(LatDest, LonDest, LatSrc, LonSrc);

        TextView tv = (TextView) findViewById(R.id.src_dest_text);
        tv.setText("Source address: " + src + "; Destination Address: " + dest);
    }

    private void initializeUber(final double LatDest, final double LonDest, final double LatSrc, final double LonSrc) {
        final SessionConfiguration config = new SessionConfiguration.Builder()
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
        //RideRequestButton requestButton = new RideRequestButton(getApplicationContext());
        // get your layout, for instance:
        //ConstraintLayout layout =  findViewById(R.id.layout_main);
        //layout.addView(requestButton);

//        RideParameters rideParams = new RideParameters.Builder()
//                // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
//                .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
//                // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
//                .setDropoffLocation(
//                        LatSrc, LatDest, src, src)
//                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
//                .setPickupLocation(LonSrc, LonDest, dest, dest)
//                .build();
        /*.setDropoffLocation(
                        LatDest, LonDest, dest, dest)
                // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                .setPickupLocation(LatSrc, LonSrc, src, src)
                .build();*/
        // set parameters for the RideRequestButton instance
        //requestButton.setRideParameters(rideParams);

        //Response<List<Product>> response = service.getProducts().execute();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerTokenSession session = new ServerTokenSession(config);
                    //requestButton.setSession(session);
                    //requestButton.loadRideInformation();
                    RidesService service = UberRidesApi.with(session).build().createService();
                    Response<PriceEstimatesResponse> priceResponse;
                    Response<TimeEstimatesResponse> timeResponse;

                    priceResponse = service.getPriceEstimates((float) LatSrc, (float) LonSrc, (float) LatDest, (float) LonDest).execute();
                    //timeResponse = service.getPickupTimeEstimate((float) LatSrc, (float) LonSrc, null).execute();
                    Log.v("(((((((((((((((((", "size" + + priceResponse.code() +(priceResponse.body().getPrices().size()));
                    List<PriceEstimate> prices = priceResponse.body().getPrices();
                    int highEst, lowEst;
                    for (PriceEstimate p : prices) {
                        Log.v("###################", p.getDisplayName() + p.getEstimate());
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
                        rideOptions.add(new Ride("uber", p.getDisplayName(), p.getProductId(), p.getDistance(), p.getDuration(), highEst, lowEst, rideEta.getEstimate()));
                        Log.v("&&&&&&&", rideOptions.size() + "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
            for(Ride r : rideOptions) {
                String s = r.getCompany() + ";" + r.getProductId() + ";" + r.getPrice();
                Log.v("SEE THIS", s);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void initializeLyft(final double LatDest, final double LonDest, final double LatSrc, final double LonSrc) {
        final ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("JzHNYTU35r0S")
                .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                .build();

        //LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button);
        //lyftButton.setApiConfig(apiConfig);

        //RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                //.setPickupLocation(LatSrc, LonSrc) //4th st SF
                //.setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376
        //rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);

        //lyftButton.setRideParams(rideParamsBuilder.build());
        //lyftButton.load();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final LyftPublicApi lyftPublicApi = new LyftApiFactory(apiConfig).getLyftPublicApi();

                Call<CostEstimateResponse> costEstimateCall = lyftPublicApi.getCosts(LatSrc, LonDest, null, LatDest, LonDest);

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
                                    Log.v("&&&&&&&", response.body().toString());
                                    int etaResultValue = 0;
                                    if (etaResult.get(0).eta_seconds != null) {
                                        etaResultValue = etaResult.get(0).eta_seconds;
                                    }
                                    rideOptions.add(new Ride("lyft", c.display_name, c.ride_type, (float) c.estimated_distance_miles.doubleValue(), c.estimated_duration_seconds,
                                            c.estimated_cost_cents_max,
                                            c.estimated_cost_cents_min,
                                            etaResultValue));
                                    Log.v("&&&&&&&", rideOptions.size() + "");
                                }

                                @Override
                                public void onFailure(Call<EtaEstimateResponse> call, Throwable t) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<CostEstimateResponse> call, Throwable t) {
                    }
                });
            }
        });
        t.start();
        try {
            t.join();

            for(Ride r : rideOptions) {
                String s = r.getCompany() + ";" + r.getProductId() + ";" + r.getPrice();
                Log.v("SEE THIS", s);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void OnClickBack(View view) {
        finish();
    }

    public String[] createPriceArray() {
        String[] priceArr = new String[rideOptions.size()];
        Iterator i = rideOptions.iterator();
        int j = 0;
        while(i.hasNext()) {
            Ride r = (Ride) i.next();
            priceArr[j] = r.getPrice() + ";" + r.getProductId() + ";" + r.getCompany();
            j++;
        }
        return priceArr;
    }

    public String[] createETAArray() {
        String[] ETAArr = new String[rideOptions.size()];
        Iterator i = rideOptions.iterator();
        int j = 0;
        while(i.hasNext()) {
            Ride r = (Ride) i.next();
            int f = r.getDuration() + r.getEta();
            ETAArr[j] = f + ";" + r.getProductId() + ";" + r.getCompany();
            j++;
        }
        return ETAArr;
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
            i.putExtra("priceArray", createPriceArray());
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
            i.putExtra("ETAArray", createETAArray());
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

package pkali.transportationapp.Price;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private TextView btn;
    private GridLayout gridLayout;
    private boolean first = true;
    private boolean isFirstTime = true;

    private String[] priceArr = new String[10];
    private static final Comparator<Ride> MY_ORDER_1 = new PriceActivity.myOrder1();
    private String[] ETAArr;
    private static final Comparator<Ride> MY_ORDER_2 = new PriceActivity.myOrder2();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);


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
        priceArr = new String[rideOptions.size()];
        gridLayout = findViewById(R.id.gridViewPrice);
        gridLayout.setColumnCount(1);

        btn = (TextView) findViewById(R.id.instruction);
        btn.setTextSize(20);
        btn.setTextColor(Color.RED);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MenuItem mi = findViewById(R.id.menu_price);
                onOptionsItemSelected(mi);
            }
        }, 2000);



    }

    private void reinitialize(final String delayMessage) {
        Context context = getApplicationContext();
        CharSequence text = delayMessage;
        int duration = Toast.LENGTH_LONG;
        Toast delay = Toast.makeText(context, text, duration);
        delay.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeLyft(latitudeDest, longitudeDest, latitudeSrc, longitudeSrc);
                initializeUber(latitudeDest, longitudeDest, latitudeSrc, longitudeSrc);

            }
        }, 2500);
    }


    @SuppressLint("NewApi")
    public void buildETASort() {
        reinitialize("Finding rides...");
        rideOptions.sort(MY_ORDER_2);
        Iterator i = rideOptions.iterator();
        int j = 0;
        GridLayout gridLayout = findViewById(R.id.gridViewPrice);
        gridLayout.setRowCount(13);
        gridLayout.setColumnCount(1);


        while(i.hasNext() && j < 13) {

            Ride r = (Ride) i.next();
            int lyftCount = 0;

            String productID = r.getProductId();
            String company = r.getCompany();
            Log.v("car type is " + r.getName(), " ETA is " + r.getEta());
            if(company.equals("lyft")) {
                final ApiConfig apiConfig = new ApiConfig.Builder()
                        .setClientId("JzHNYTU35r0S")
                        .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                        .build();

                if(productID.equals("lyft_line")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_line);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j, GridLayout.FILL),
                            GridLayout.spec(0, GridLayout.FILL)));

                    lyftButton.setVisibility(View.VISIBLE);
                    lyftButton.setApiConfig(apiConfig);
                    Log.v("ETA j value for" + r.getName() + " is ", "" + j);
                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                            .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.LINE);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.load();
                }

                else if(productID.equals("lyft")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_classic);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j, GridLayout.FILL),
                            GridLayout.spec(0, GridLayout.FILL)));
                    lyftButton.setVisibility(View.VISIBLE);
                    lyftButton.setApiConfig(apiConfig);
                    Log.v("ETA j value for" + r.getName() + " is ", "" + j);
                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                            .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.setRideParams(rideParamsBuilder.build());

                    lyftButton.load();
                }
                else if(productID.equals("lyft_plus")) {

                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_plus);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j, GridLayout.FILL),
                            GridLayout.spec(0, GridLayout.FILL)));
                    lyftButton.setVisibility(View.VISIBLE);
                    lyftButton.setApiConfig(apiConfig);
                    Log.v("ETA j value for" + r.getName() + " is ", "" + j);
                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                            .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.PLUS);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.setRideParams(rideParamsBuilder.build());

                    lyftButton.load();
                    lyftCount++;
                }



            }
            else if(company.equals("uber")) {
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

                RideRequestButton requestButton = new RideRequestButton(getApplicationContext());
                // get your layout, for instance:
                gridLayout.addView(requestButton, new GridLayout.LayoutParams(
                        GridLayout.spec(j, GridLayout.FILL),
                        GridLayout.spec(0, GridLayout.FILL)));
                Log.v("ETA j value for" + r.getName() + " is ", "" + j);
                RideParameters rideParams = new RideParameters.Builder()
                        // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                        .setProductId(productID)
                        // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                        .setDropoffLocation(
                                latitudeDest, longitudeDest, DestAdd, DestAdd)
                        // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                        .setPickupLocation(latitudeSrc, longitudeSrc, SrcAdd, SrcAdd)
                        .build();
                // set parameters for the RideRequestButton instance
                requestButton.setRideParameters(rideParams);
                ServerTokenSession session = new ServerTokenSession(config);
                requestButton.setSession(session);
                requestButton.loadRideInformation();
            }
            j++;
        }
    }

    public void buildPriceSort() {
        if(!isFirstTime) {
            reinitialize("Finding rides...");
        }
        isFirstTime = false;
        Collections.sort(rideOptions, MY_ORDER_1);
        GridLayout gridLayout = findViewById(R.id.gridViewPrice);
        gridLayout.setRowCount(13);
        gridLayout.setColumnCount(1);
        gridLayout.setRowOrderPreserved(true);
        gridLayout.setColumnOrderPreserved(true);
        int j = 0;
        Iterator<Ride> i = rideOptions.iterator();
        while(i.hasNext() && j < 13) {
            Ride r = i.next();
            Log.v("car type is " + r.getName(), " price is " + r.getPrice());
            int lyftCount = 0;
            String company = r.getCompany();
            String productID = r.getProductId();
            if(company.equals("lyft")) {
                final ApiConfig apiConfig = new ApiConfig.Builder()
                        .setClientId("JzHNYTU35r0S")
                        .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                        .build();

                if(productID.equals("lyft_line")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_line);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j, GridLayout.FILL),
                            GridLayout.spec(0, GridLayout.FILL)));
                    Log.v("j value for" + r.getName() + " is ", "" + j);
                    lyftButton.setVisibility(View.VISIBLE);
                    lyftButton.setApiConfig(apiConfig);

                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                            .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.LINE);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.load();
                }

                else if(productID.equals("lyft")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_classic);
                    gridLayout.removeView(lyftButton);

                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j, GridLayout.FILL),
                            GridLayout.spec(0, GridLayout.FILL)));

                    Log.v("j value for" + r.getName() + " is ", "" + j);
                    lyftButton.setVisibility(View.VISIBLE);
                    lyftButton.setApiConfig(apiConfig);

                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                            .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.setRideParams(rideParamsBuilder.build());

                    lyftButton.load();
                }
                else if (productID.equals("lyft_plus")) {

                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_plus);

                    if(lyftCount == 0) {
                        gridLayout.removeView(lyftButton);

                        lyftButton.setVisibility(View.VISIBLE);

                        gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                                GridLayout.spec(j, GridLayout.FILL),
                                GridLayout.spec(0, GridLayout.FILL)));
                        Log.v("j value for" + r.getName() + " is ", "" + j);


                        lyftButton.setApiConfig(apiConfig);

                        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                                .setPickupLocation(latitudeSrc, longitudeSrc) //4th st SF
                                .setDropoffLocation(latitudeDest, longitudeDest); //2900 N MacArthur Dr, Tracy, CA 95376

                        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.PLUS);
                        lyftButton.setRideParams(rideParamsBuilder.build());
                        lyftButton.setRideParams(rideParamsBuilder.build());

                        lyftButton.load();
                    }
                    lyftCount++;
                }



            }
            else if(company.equals("uber")) {
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

                RideRequestButton requestButton = new RideRequestButton(getApplicationContext());
                // get your layout, for instance:

                gridLayout.addView(requestButton, new GridLayout.LayoutParams(
                        GridLayout.spec(j, GridLayout.FILL),
                        GridLayout.spec(0, GridLayout.FILL)));

                Log.v("j value for" + r.getName() + " is ", "" + j);

                RideParameters rideParams = new RideParameters.Builder()
                        // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                        .setProductId(productID)
                        // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                        .setDropoffLocation(
                                latitudeDest, longitudeSrc, DestAdd, DestAdd)
                        // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                        .setPickupLocation(latitudeDest, longitudeDest, SrcAdd, SrcAdd)
                        .build();
                // set parameters for the RideRequestButton instance
                requestButton.setRideParameters(rideParams);
                ServerTokenSession session = new ServerTokenSession(config);
                requestButton.setSession(session);
                requestButton.loadRideInformation();
            }
            j++;
        }
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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerTokenSession session = new ServerTokenSession(config);

                    RidesService service = UberRidesApi.with(session).build().createService();
                    Response<PriceEstimatesResponse> priceResponse;
                    Response<TimeEstimatesResponse> timeResponse;

                    priceResponse = service.getPriceEstimates((float) LatSrc, (float) LonSrc, (float) LatDest, (float) LonDest).execute();
                    //timeResponse = service.getPickupTimeEstimate((float) LatSrc, (float) LonSrc, null).execute();
                    Log.v("(((((((((((((((((", "size" + + priceResponse.code() +(priceResponse.body().getPrices().size()));
                    List<PriceEstimate> prices = priceResponse.body().getPrices();
                    int highEst, lowEst;
                    for (PriceEstimate p : prices) {
                        timeResponse = service.getPickupTimeEstimate((float) LatSrc, (float) LonSrc, p.getProductId()).execute();
                        if (!timeResponse.body().getTimes().isEmpty()) {
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
                        }
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
                                    int etaResultValue = 0;
                                    if (etaResult.get(0).eta_seconds != null) {
                                        etaResultValue = etaResult.get(0).eta_seconds;
                                    }
                                    rideOptions.add(new Ride("lyft", c.display_name, c.ride_type, (float) c.estimated_distance_miles.doubleValue(), c.estimated_duration_seconds,
                                            c.estimated_cost_cents_max,
                                            c.estimated_cost_cents_min,
                                            etaResultValue));
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

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void getTransit(){
        DirectionsResult result = PublicTransport.getTransitResult(src, dest);
        Context context = getApplicationContext();
        CharSequence text = "No Public transportation found";
        int duration = Toast.LENGTH_LONG;
        Toast transit = Toast.makeText(context, text, duration);
        try {
            Intent i = new Intent(this, PublicTransitActivity.class);
            i.putExtra("Source", src);
            i.putExtra("Destination", dest);
            i.putExtra("Result", PublicTransport.getEndLocationTitle(result));
            startActivity(i);
        } catch(Exception e){
            transit.show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int x;
        if(item == null) {
            x = R.id.menu_price;
        }
        else {
           x = item.getItemId();
        }

        switch (x) {
            case R.id.menu_ETA:
                if(first) {
                    gridLayout.removeView(btn);
                    first = false;
                }
                buildETASort();
                return true;
            case R.id.menu_transit:
                if(first) {
                    gridLayout.removeView(btn);
                    first = false;
                }
                getTransit();
                return true;
            case R.id.menu_price:
                if(first) {
                    gridLayout.removeView(btn);
                    first = false;
                }
                buildPriceSort();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    private static class myOrder1 implements Comparator<Ride> {
        public int compare(Ride o1, Ride o2) {
            if(o1.getPrice() < o2.getPrice()) return -1;
            else if(o1.getPrice() > o2.getPrice()) return 1;
            else return 0;
        }
    }


    private static class myOrder2 implements Comparator<Ride> {
        public int compare(Ride o1, Ride o2) {
            if (o1.getEta() > o2.getEta()) return 1;
            else if (o1.getEta() < o2.getEta()) return -1;
            else return 0;
        }
    }





}

package pkali.transportationapp.Price;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridLayout;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.rides.client.ServerTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;
import java.util.Comparator;

import pkali.transportationapp.R;

public class PriceSortActivity extends AppCompatActivity {
    private String[] priceArr;
    private static final Comparator<String> MY_ORDER_1 = new myOrder1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_sort);
        Intent i = getIntent();
        Double LatSrc = i.getDoubleExtra("LatSrc", 1);
        Double LonSrc = i.getDoubleExtra("LonSrc", 1);
        Double LatDest = i.getDoubleExtra("LatDest", 1);
        Double LonDest = i.getDoubleExtra("LonDest", 1);
        String SrcAdd = i.getStringExtra("current src");
        String DestAdd = i.getStringExtra("current dest");
        String[] arr = getPriceArray();
        Log.v("------", "" + arr.length);
        for(int j = 0; j < arr.length; j++) {

            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridView);
            gridLayout.setRowCount(18);
            gridLayout.setColumnCount(2);
            String copy = arr[j];
            String copy1 = arr[j];
            String copy2 = arr[j];
            int lyftCount = 0;

            int x = arr[j].indexOf(';');
            Float price = Float.parseFloat(copy.substring(0, x));
            String s = copy1.substring(x + 1);
            int y = s.indexOf(';');
            String productID = s.substring(0, y);
            String company = copy2.substring(x + 1 + y + 1);
            Log.v("company is ", company);
            if(company.equals("lyft")) {
                final ApiConfig apiConfig = new ApiConfig.Builder()
                        .setClientId("JzHNYTU35r0S")
                        .setClientToken("mPXtV/bLryJfivrfsmvE7XrADmIXfmUj9Fm+hBLIMfDOexXRL6Y6VqMnIEhYF17oTrajzeLU6swgDBroJxZ5i/SXdWdSg8HjMxIKFETz7tV9UWYv4zEHeJw=")
                        .build();

                if(productID.equals("lyft_line")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_line);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j + 1, GridLayout.FILL),
                            GridLayout.spec(1, GridLayout.FILL)));


                lyftButton.setApiConfig(apiConfig);

                RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                        .setPickupLocation(LatSrc, LonSrc) //4th st SF
                        .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.LINE);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.load();
                }

                else if(productID.equals("lyft")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_classic);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j + 1, GridLayout.FILL),
                            GridLayout.spec(1, GridLayout.FILL)));

                    lyftButton.setApiConfig(apiConfig);

                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(LatSrc, LonSrc) //4th st SF
                            .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    lyftButton.setRideParams(rideParamsBuilder.build());

                    lyftButton.load();
                }
                else {

                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_plus);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j - lyftCount + 1, GridLayout.FILL),
                            GridLayout.spec(1, GridLayout.FILL)));

                    lyftButton.setApiConfig(apiConfig);

                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(LatSrc, LonSrc) //4th st SF
                            .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376

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
                GridLayout layout =  findViewById(R.id.gridView);
                gridLayout.addView(requestButton, new GridLayout.LayoutParams(
                        GridLayout.spec(j + 1, GridLayout.FILL),
                        GridLayout.spec(1, GridLayout.FILL)));

                RideParameters rideParams = new RideParameters.Builder()
                        // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                        .setProductId(productID)
                        // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                        .setDropoffLocation(
                                LatDest, LonDest, SrcAdd, SrcAdd)
                        // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
                        .setPickupLocation(LatSrc, LonSrc, DestAdd, DestAdd)
                        .build();
                        // set parameters for the RideRequestButton instance
                requestButton.setRideParameters(rideParams);
                ServerTokenSession session = new ServerTokenSession(config);
                requestButton.setSession(session);
                requestButton.loadRideInformation();
            }

        }
    }

    public String[] getPriceArray() {
        Intent i = getIntent();
        priceArr = i.getStringArrayExtra("priceArray");

        Arrays.sort(priceArr, MY_ORDER_1);

        for(int j = 0; j < priceArr.length; j++) {
            Log.v("LOL", priceArr[j]);
        }
        return priceArr;
    }

    private static class myOrder1 implements Comparator<String> {
        public int compare(String o1, String o2) {
            int x1 = o1.indexOf(";");
            String s1 = o1.substring(0, x1);
            float f1 = Float.parseFloat(s1);

            int x2 = o2.indexOf(";");
            String s2 = o2.substring(0, x2);
            float f2 = Float.parseFloat(s2);
            if(f1 < f2) {
                return -1;
            }
            else if (f1 > f2) {
                return 1;
            }
            else return 0;
        }
    }

}

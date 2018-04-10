package pkali.transportationapp.Price;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;

import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;

import java.util.Arrays;
import java.util.Comparator;

import pkali.transportationapp.R;

public class ETASortActivity extends AppCompatActivity {

    private String[] ETAArr;
    private static final Comparator<String> MY_ORDER_1 = new ETASortActivity.myOrder1();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etasort);
        Intent i = getIntent();
        Double LatSrc = i.getDoubleExtra("LatSrc", 1);
        Double LonSrc = i.getDoubleExtra("LonSrc", 1);
        Double LatDest = i.getDoubleExtra("LatDest", 1);
        Double LonDest = i.getDoubleExtra("LonDest", 1);
        String[] arr = getETAArray();
        //Log.v("------", "" + arr.length);
        for(int j = 0; j < arr.length; j++) {

            GridLayout gridLayout = (GridLayout) findViewById(R.id.gridView);
            gridLayout.setRowCount(10);
            gridLayout.setColumnCount(4);
            String copy = arr[j];
            String copy1 = arr[j];
            String copy2 = arr[j];
            int lyftCount = 0;

            int x = arr[j].indexOf(';');
            int eta = Integer.parseInt(copy.substring(0, x));
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
                            GridLayout.spec(j + 1, GridLayout.CENTER),
                            GridLayout.spec(1, GridLayout.CENTER)));


                    lyftButton.setApiConfig(apiConfig);

                    RideParams.Builder rideParamsBuilder = new RideParams.Builder()
                            .setPickupLocation(LatSrc, LonSrc) //4th st SF
                            .setDropoffLocation(LatDest, LonDest); //2900 N MacArthur Dr, Tracy, CA 95376

                    rideParamsBuilder.setRideTypeEnum(RideTypeEnum.LINE);
                    lyftButton.setRideParams(rideParamsBuilder.build());
                    /*RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    rel_btn.leftMargin = 50;
                    rel_btn.topMargin = ((j+1) * 300);

                    lyftButton.setLayoutParams(rel_btn);*/


                    lyftButton.load();
                }

                else if(productID.equals("lyft")) {
                    LyftButton lyftButton = (LyftButton) findViewById(R.id.lyft_button_classic);
                    gridLayout.removeView(lyftButton);
                    gridLayout.addView(lyftButton, new GridLayout.LayoutParams(
                            GridLayout.spec(j + 1, GridLayout.CENTER),
                            GridLayout.spec(1, GridLayout.CENTER)));

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
                            GridLayout.spec(j - lyftCount + 1, GridLayout.CENTER),
                            GridLayout.spec(1, GridLayout.CENTER)));

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

            }

        }
    }

    public String[] getETAArray() {
        Intent i = getIntent();
        ETAArr = i.getStringArrayExtra("ETAArray");
        Log.v("------", "" + ETAArr.length);

        Arrays.sort(ETAArr, MY_ORDER_1);

        for(int j = 0; j < ETAArr.length; j++) {
            Log.v("LOL", ETAArr[j]);
        }
        return ETAArr;
    }

    private static class myOrder1 implements Comparator<String> {
        public int compare(String o1, String o2) {
            int x1 = o1.indexOf(";");
            String s1 = o1.substring(0, x1);
            int f1 = Integer.parseInt(s1);

            int x2 = o2.indexOf(";");
            String s2 = o2.substring(0, x2);
            int f2 = Integer.parseInt(s2);
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

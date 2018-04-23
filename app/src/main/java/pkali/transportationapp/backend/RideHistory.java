package pkali.transportationapp.backend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import pkali.transportationapp.R;

/**
 * Created by aashishsingh on 2/25/18.
 */

public class RideHistory extends AppCompatActivity {
    DynamoDBMapper dynamoDBMapper;
    String name;
    private ExpandableListView listView;
    private MyExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private List<NamedLocation> locations;
    private int latestGroup = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_layout);
        Log.v("Ride History Activity:", "entered");


        AWSConfiguration ac = new AWSConfiguration(getApplicationContext());
        CognitoUserPool cup = new CognitoUserPool(getApplicationContext(), ac);
        CognitoUser c = cup.getCurrentUser();
        name = c.getUserId();

        listView = (ExpandableListView) findViewById(R.id.expLV);
        initData();
        listAdapter = new MyExpandableListAdapter(this, listDataHeader, listHash, locations);
        listView.setAdapter(listAdapter);

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (latestGroup != -1 && groupPosition != latestGroup) {
                    listView.collapseGroup(latestGroup);
                }
                latestGroup = groupPosition;
            }
        });

        Log.v("Ride History Activity:", "finished");
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        locations = new ArrayList<>();

        final AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        Log.v("RIDE HISTORY CLIENT:", dynamoDBClient.toString());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        queryRides();
    }

    // populates listDataHeader, listHash, and locations by querying the database
    public void queryRides() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                RideTableDO ride_table = new RideTableDO();
                ride_table.setUserId(name);

                Condition rangeKeyCondition = new Condition();

                DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                        .withHashKeyValues(ride_table)
                        .withRangeKeyCondition("", rangeKeyCondition)
                        .withConsistentRead(false);

                DynamoDBQueryExpression<RideTableDO> q = new DynamoDBQueryExpression<RideTableDO>()
                        .withHashKeyValues(ride_table);

                PaginatedList<RideTableDO> result = dynamoDBMapper.query(RideTableDO.class, q);
                Gson gson = new Gson();
                String stringBuilder = "";



                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    //String jsonFormOfItem = gson.toJson(result.get(i));
                    String jsonFormOfItem = "From: " + result.get(i).getSrcAddr() + "\n\n";
                    jsonFormOfItem += "To: " + result.get(i).getDstAddr() + "\n\n\n";
                    Log.v("SRC:","src is" +  result.get(i).getSrcAddr());
                    stringBuilder += jsonFormOfItem;
                    Date d = new Date(-1*result.get(i).getTimestamp());
                    String[] header = d.toString().split("00:00");
                    listDataHeader.add(header[0].substring(0, header[0].length()-1) + header[1]);


                    LatLng srcCoord = new LatLng(result.get(i).getSrcLat(), result.get(i).getSrcLon());
                    LatLng dstCoord = new LatLng(result.get(i).getDstLat(), result.get(i).getDstLon());
                    Log.v("NAMEDLOCATION:", result.get(i).getSrcLat().toString() + ", " + result.get(i).getSrcLon().toString());
                    NamedLocation curr = new NamedLocation(result.get(i).getSrcAddr(), srcCoord, result.get(i).getDstAddr(), dstCoord);
                    locations.add(curr);
                }

                if (result.size() < 1) {
                    return;
                }

                String resString = stringBuilder;//.toString();
                String[] split = resString.split("\n\n\n");
                Log.v("split length", Integer.toString(split.length));
                Log.v("RESULT:", resString);
                Log.v("Query result: ", stringBuilder.toString());
                for (int i = 0; i < split.length; i++) {
                    List<String> values = new ArrayList<>();
                    Log.v("Split[i]", split[i]);
                    values.add(split[i]);
                    listHash.put(listDataHeader.get(i), values);
                }

                // Add your code here to deal with the data result

                if (result.isEmpty()) {
                    // There were no items matching your query.
                    Log.v("Query result: ", "EMPTY");
                }

                // Loop through query results
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {}
    }
}
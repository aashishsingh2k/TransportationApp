package pkali.transportationapp.backend;


import android.os.Bundle;
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

import java.util.ArrayList;
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
        listAdapter = new MyExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        Log.v("Ride History Activity:", "finished");
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        final AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        Log.v("RIDE HISTORY CLIENT:", dynamoDBClient.toString());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();


        //final RideTableDO ridesItem = new RideTableDO();
        PaginatedList<RideTableDO> result_copy;
        new Thread(new Runnable() {
            @Override
            public void run() {
                RideTableDO ride_table = new RideTableDO();
                ride_table.setUserId(name);

                Condition rangeKeyCondition = new Condition();

                DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                        .withHashKeyValues(ride_table)
                        .withRangeKeyCondition("", rangeKeyCondition)
                        .withConsistentRead(false);

//                String partitionKey = name;
//                Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
//                eav.put(":val1", new AttributeValue().withS(partitionKey));
//                DynamoDBQueryExpression<RideTableDO> qExp = new DynamoDBQueryExpression<RideTableDO>()
//                        .withKeyConditionExpression("userId = :val1").withExpressionAttributeValues(eav);

                DynamoDBQueryExpression<RideTableDO> q = new DynamoDBQueryExpression<RideTableDO>()
                        .withHashKeyValues(ride_table);

                PaginatedList<RideTableDO> result = dynamoDBMapper.query(RideTableDO.class, q);
                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();



                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    String jsonFormOfItem = "From: (" + gson.toJson(result.get(i).getSrcLat()) + ", " + gson.toJson(result.get(i).getSrcLon() + ")\n");
                    jsonFormOfItem += "To: (" + gson.toJson(result.get(i).getDstLat()) + ", " + gson.toJson(result.get(i).getDstLon()) + ")\n\n";
                    stringBuilder.append(jsonFormOfItem);
                    //listDataHeader.add(result.get(i).getDate());
                    listDataHeader.add(result.get(i).getDate());

                }
/*
                for (int i = 0; i < result.size(); i++) {
                    List<String> values = new ArrayList<>();
                    values.add(gson.toJson(result.get(i)));
                    listHash.put(listDataHeader.get(i), values);
                }
*/
                String resString = stringBuilder.toString();
                String[] split = resString.split("\n\n");
                Log.v("split length", Integer.toString(split.length));
                Log.v("RESULT:", resString);
                Log.v("Query result: ", stringBuilder.toString());

                for (int i = 0; i < split.length; i++) {
                    //listDataHeader.add(Integer.toString(i));
                }
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
        }).start();

        /*
        listDataHeader.add("exp");
        listDataHeader.add("poo");


        List<String> exp = new ArrayList<>();
        exp.add("This is Expandable ListView");

        List<String> poo = new ArrayList<>();
        poo.add("1");
        poo.add("2");
        poo.add("3");
        poo.add("4");
        listHash.put(listDataHeader.get(0), exp);
        listHash.put(listDataHeader.get(1), poo);
        */
        try {
            wait(10000);
        } catch (Exception e) {

        }
       for (int i = 0; i < listDataHeader.size(); i++) {
           Log.v("DATAHEADER:", listDataHeader.get(i));
           Log.v("HASHMAP", listHash.get(listDataHeader.get(i)).get(0));
       }
    }
}

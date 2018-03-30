package pkali.transportationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by aashishsingh on 2/17/18.
 */

public class NextActivity extends AppCompatActivity {
    public static final int FormActivity_ID = 1;
    public static final int FormActivity_ID1 = 2;
    public static final int FormActivity_RideID = 3;
    private Context context;
    DynamoDBMapper dynamoDBMapper;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity);

        AWSConfiguration ac = new AWSConfiguration(getApplicationContext());
        CognitoUserPool cup = new CognitoUserPool(getApplicationContext(), ac);
        CognitoUser c = cup.getCurrentUser();

        name = c.getUserId();
        TextView msg = findViewById(R.id.text_welcome);
        msg.setText("Welcome to your transportation app account " + name + "!");
        msg.setTextSize(20);
        /*Intent it = getIntent();
        String name = it.getStringExtra("name");
        TextView msg = (TextView) findViewById(R.id.text_welcome);
        msg.setText("Welcome " + name + "!");*/

        /*IdentityManager idm = new IdentityManager(getApplicationContext(),
                new AWSConfiguration(getApplicationContext()));
        IdentityManager.setDefaultIdentityManager(idm);
        String name = idm.getCachedUserID();
        TextView msg = (TextView) findViewById(R.id.text_welcome);
        msg.setText("Welcome " + name + "!");*/



    }

    public void OnClickSignOut(View view){
        AWSConfiguration ac = new AWSConfiguration(getApplicationContext());
        CognitoUserPool cup = new CognitoUserPool(getApplicationContext(), ac);
        CognitoUser c = cup.getCurrentUser();

        c.signOut();

        Intent it = new Intent(this, AuthenticatorActivity.class);
        startActivityForResult(it, FormActivity_ID);

    }

    public void OnClickPrice(View view) {
        Intent it1 = new Intent(this, PriceActivity.class);
        startActivityForResult(it1, FormActivity_ID1);

    }

    public void OnClickEnterSrcMapButton(View view){
        Intent i = new Intent(this, MapsMarkerActivity.class);
        startActivity(i);
    }

    public void onClickQuery(View view) {
        Log.v("ONCLICKQUERY: ", "CLICKED QUERY BUTTON");

        /* Redirect to Ride History Activity */
        Intent rideHist = new Intent(this, RideHistory.class);
        startActivityForResult(rideHist, FormActivity_RideID);
    }

    private static void getName(DynamoDBMapper mapper, int id) throws Exception {
        Log.v("Query Method", "getName");
    }

    public void OnClickGpsButton(View view) {
        Intent i = new Intent(this, MapsActivityCurrentPlace.class);
        startActivity(i);
    }
}

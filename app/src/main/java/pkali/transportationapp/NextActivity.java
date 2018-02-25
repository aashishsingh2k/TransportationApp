package pkali.transportationapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.os.Bundle;import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityHandler;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.core.IdentityProvider;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobile.auth.userpools.SignUpActivity;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.google.gson.Gson;




import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.auth.policy.Principal.WebIdentityProviders.Amazon;

/**
 * Created by aashishsingh on 2/17/18.
 */

public class NextActivity extends AppCompatActivity {
    public static final int FormActivity_ID = 1;
    public static final int FormActivity_ID1 = 2;
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

    public void onClickQuery(View view) {
        Log.v("ONCLICKQUERY: ", "CLICKED QUERY BUTTON");
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(AWSMobileClient.getInstance().getCredentialsProvider());
        Log.v("CLIENT:", dynamoDBClient.toString());
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                .build();

        final RidesDO ridesItem = new RidesDO();
        ridesItem.setUserId(name);
        ridesItem.setTime("02-25-2018");
        ridesItem.setSrcLat(37.7766048);
        ridesItem.setSrcLon(-122.3943629);
        ridesItem.setDestLat(37.759234);
        ridesItem.setDestLon(-121.4135125);

        Log.v("HI", "HELLO WORLD");

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamoDBMapper.save(ridesItem);
                // Item saved
            }
        }).start();

        /*try {s
            getName(dynamoDBMapper, 0);
        } catch(Exception e) {
        }*/
    }

    private static void getName(DynamoDBMapper mapper, int id) throws Exception {
        Log.v("Query Method", "getName");
    }
}

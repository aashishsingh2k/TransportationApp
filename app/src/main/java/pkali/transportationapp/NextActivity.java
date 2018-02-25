package pkali.transportationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;

/**
 * Created by aashishsingh on 2/17/18.
 */

public class NextActivity extends AppCompatActivity {
    public static final int FormActivity_ID = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity);

        AWSConfiguration ac = new AWSConfiguration(getApplicationContext());
        CognitoUserPool cup = new CognitoUserPool(getApplicationContext(), ac);
        CognitoUser c = cup.getCurrentUser();

        String name = c.getUserId();
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
}

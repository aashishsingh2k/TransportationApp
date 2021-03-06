package pkali.transportationapp.LoginAndMenu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amazonaws.mobile.auth.ui.AuthUIConfiguration;
import com.amazonaws.mobile.auth.ui.SignInUI;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import pkali.transportationapp.Maps.MapsActivityCurrentPlace;
import pkali.transportationapp.R;

public class AuthenticatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_authenticator);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);


        AWSMobileClient.getInstance().initialize(this).execute();
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                AuthUIConfiguration config =
                        new AuthUIConfiguration.Builder()
                                .userPools(true)  // true? show the Email and Password UI
                                .logoResId(R.drawable.logo) // Change the logo
                                .backgroundColor(Color.WHITE) // Change the backgroundColor
                                .isBackgroundColorFullScreen(true) // Full screen backgroundColor the backgroundColor full screenff
                                .fontFamily("sans-serif-light") // Apply sans-serif-light as the global font
                                .canCancel(true)
                                .build();


                SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
                signin.login(AuthenticatorActivity.this, MapsActivityCurrentPlace.class).authUIConfiguration(config).execute();


            }
        }).execute();


    }

    //if the user is already logged in earlier give him option to take him directly to MainMenuActivity
    public void OnClick1(View view) {

                AWSMobileClient.getInstance().initialize(this).execute();

                AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {

                    @Override
                    public void onComplete(AWSStartupResult awsStartupResult) {
                        SignInUI signin = (SignInUI) AWSMobileClient.getInstance().getClient(AuthenticatorActivity.this, SignInUI.class);
                        signin.login(AuthenticatorActivity.this, MapsActivityCurrentPlace.class).execute();

                    }
                }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

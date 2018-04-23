package pkali.transportationapp.LoginAndMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

import pkali.transportationapp.R;

/**
 * Created by shivasuri on 4/21/18.
 */

public class SettingsActivity extends AppCompatActivity {

    CognitoUser user;
    boolean deleted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        AWSConfiguration ac = new AWSConfiguration(getApplicationContext());
        CognitoUserPool cup = new CognitoUserPool(getApplicationContext(), ac);
        user = cup.getCurrentUser();
    }

    public void onClickDelete(View v) {
        Thread delThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GenericHandler handler = new GenericHandler() {

                    @Override
                    public void onSuccess() {
                        // Delete was successful!
                        deleted = true;

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        exception.printStackTrace();
                        Log.d("FAILED TO DELETE", exception.toString());
                        // Delete failed, probe exception for details

                    }
                };
                user.deleteUser(handler);

            }
        });

        delThread.start();
        try {
            delThread.join();
        } catch (InterruptedException e) {
        }

        if (deleted) {
            Log.v("DELETE", "SUCCESSFULLY DELETED");
            Intent i = new Intent(this, DeleteActivity.class);
            startActivity(i);
        } else {
            Log.v("DELETE", "FAILED TO DELETE");
            Toast t = Toast.makeText(getApplicationContext(), "Failed to delete account\nPlease contact customer support", Toast.LENGTH_LONG);
            t.show();
        }
    }

    public void onClickChangePassword(View v) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Change Password");
        alert.setMessage("Enter old password");

        // Set an EditText view to get user input
        final EditText old = new EditText(this);
        alert.setView(old);

        String oldPassword = "";

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               // oldPassword = dialog.toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();


    }
}

package pkali.transportationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aashishsingh on 3/5/18.
 */

public class nextMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_next);
        Intent it = getIntent();
        String currAdd = it.getStringExtra("current address");
        TextView tv = (TextView) findViewById(R.id.source_address);
        tv.setTextSize(18);
        tv.setText("Your source address is " + currAdd);
    }

    public void OnClickWrongSrc(View view) {
        finish();
    }

    public void OnClickNextFromSrc1(View view) {
        Intent inte = getIntent();
        Double Lat = inte.getDoubleExtra("lat", 1);
        Double Lon = inte.getDoubleExtra("lon", 1);
        String src = inte.getStringExtra("current address");
        Intent i = new Intent(this, MapsMarkerActivity2.class);
        i.putExtra("lat", Lat);
        i.putExtra("lon", Lon);
        i.putExtra("current src", src);
        Log.v("lat, lon, current src ", Lat + ";" + Lon + ";" + src);
        startActivity(i);

    }
}

package pkali.transportationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aashishsingh on 3/5/18.
 */

public class nextnextMapActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_next_next);
        Intent it = getIntent();
        String currAdd = it.getStringExtra("current src");
        TextView tv = (TextView) findViewById(R.id.dest_address);
        String dest1 = getIntent().getStringExtra("current dest");
        tv.setTextSize(18);
        tv.setText("Your destination address is " + dest1);
    }

    public void OnClickWrongDest(View view) {
        finish();
    }

    public void OnClickNextFromDest(View view) {
        Intent inte = getIntent();
        Double LatSrc = inte.getDoubleExtra("LatSrc", 1);
        Double LonSrc = inte.getDoubleExtra("LonSrc", 1);
        Double LatDest = inte.getDoubleExtra("LatDest", 1);
        Double LonDest = inte.getDoubleExtra("LonDest", 1);
        String src = inte.getStringExtra("current src");
        String dest = inte.getStringExtra("current dest");
        Intent i = new Intent(this, PriceActivity.class);
        i.putExtra("LatSrc", LatSrc);
        i.putExtra("LonSrc", LonSrc);
        i.putExtra("LatDest", LatDest);
        i.putExtra("LonDest", LonDest);
        i.putExtra("current src", src);
        i.putExtra("current dest", dest);
        startActivity(i);

    }
}

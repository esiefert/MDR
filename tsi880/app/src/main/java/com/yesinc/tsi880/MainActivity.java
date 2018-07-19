package com.yesinc.tsi880;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //sets UI when opened
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //
        int id = item.getItemId();
        if (id == R.id.navigation_home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.navigation_telemetry) {
            Intent t_intent = new Intent(MainActivity.this, TelemetryActivity.class);
            startActivity(t_intent);
            return true;
        } else if (id == R.id.navigation_help) {
            Intent h_intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(h_intent);
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

}

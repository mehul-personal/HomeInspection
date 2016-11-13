package com.homeinspection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button InventoryCheckIn, InventoryCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InventoryCheckIn = (Button) findViewById(R.id.btnInventoryCheckin);
        InventoryCheckout = (Button) findViewById(R.id.btnInventoryCheckout);

        InventoryCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, NavigationMainActivity.class);
                startActivity(i);
            }
        });
    }
}

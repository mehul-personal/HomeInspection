package com.homeinspection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OutsideSpacesActivity extends AppCompatActivity {

    Button btnRoomDescription, btnFrontOfProperty, btnSideOfProperty, btnRearOfProperty, btnOutBuilding, btnGarage;
    FrameLayout flBack;
TextView header;
    String call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_spaces);
        flBack = (FrameLayout) findViewById(R.id.flBack);
header=(TextView) findViewById(R.id.txvHeader);
        btnRoomDescription = (Button) findViewById(R.id.btnRoomDescription);
        btnFrontOfProperty = (Button) findViewById(R.id.btnFrontOfProperty);
        btnSideOfProperty = (Button) findViewById(R.id.btnSideOfProperty);
        btnRearOfProperty = (Button) findViewById(R.id.btnRearOfProperty);
        btnOutBuilding = (Button) findViewById(R.id.btnOutBuilding);
        btnGarage = (Button) findViewById(R.id.btnGarage);

        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btnRoomDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER",  "Outside Spaces Room");
                startActivity(i);
            }
        });
        btnFrontOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER", "Front Of Property");
                startActivity(i);
            }
        });
        btnSideOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER", "Side Of Property");
                startActivity(i);
            }
        });
        btnRearOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER", "Rear Of Property");
                startActivity(i);
            }
        });
        btnOutBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER", "Out Of Building");
                startActivity(i);
            }
        });
        btnGarage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OutsideSpacesActivity.this, OutsideSpacesCheckInOutActivity.class);
                i.putExtra("HEADER", "Garage");
                startActivity(i);
            }
        });

    }
}

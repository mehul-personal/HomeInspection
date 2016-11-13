package com.homeinspection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class OutsideSpacesCheckInOutActivity extends AppCompatActivity {
    FrameLayout flBack;
    Button btnCheckInDone, btnCheckOutDone;
    TextView txvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_order_check_in_out);

        txvHeader = (TextView) findViewById(R.id.txvHeader);
        flBack = (FrameLayout) findViewById(R.id.flBack);
        btnCheckInDone = (Button) findViewById(R.id.btnCheckInDone);
        btnCheckOutDone = (Button) findViewById(R.id.btnCheckOutDone);
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCheckInDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCheckOutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent i = getIntent();
        txvHeader.setText(i.getStringExtra("HEADER"));
    }
}

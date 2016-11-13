package com.homeinspection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MeterReadingsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    Button Electric, Gas, Water;
    FrameLayout flBack;
    ImageView imvMeterReadingImage;
    TextView txvMeterReadingAddress, txvMeterReadingAddress2City;
    public static MeterReadingsFragment newInstance(String param1, String param2) {
        MeterReadingsFragment fragment = new MeterReadingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_meter_readings, container, false);
        flBack = (FrameLayout) view.findViewById(R.id.flBack);
        Electric = (Button) view.findViewById(R.id.btnElectric);
        Gas = (Button) view.findViewById(R.id.btnGas);
        Water = (Button) view.findViewById(R.id.btnWater);
        imvMeterReadingImage=(ImageView) view.findViewById(R.id.imvMeterReadingImage);
        txvMeterReadingAddress=(TextView) view.findViewById(R.id.txvMeterReadingAddress);
        txvMeterReadingAddress2City=(TextView) view.findViewById(R.id.txvMeterReadingAddress2City);

        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        txvMeterReadingAddress.setText(preferences.getString("ADDRESS1", ""));
        txvMeterReadingAddress2City.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
        if (!preferences.getString("IMAGE", "").isEmpty()) {
        Picasso.with(getActivity())
                .load(preferences.getString("IMAGE", "")).resize(500,500)
                .into(imvMeterReadingImage);}


       /* flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        Electric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ElectricMeterActivity.class);
                startActivity(i);
            }
        });
        Gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GasMeterActivity.class);
                startActivity(i);
            }
        });
        Water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), WaterMeterActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}

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

public class TenancyDetailsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    Button AgentContact, TenantNames, InventoryProvider;
    FrameLayout back;
    ImageView imvPropertyImage;
    TextView txvPropertyAddress, txvPropertyAddress2City;

    public TenancyDetailsFragment() {
    }

    public static TenancyDetailsFragment newInstance(String param1, String param2) {
        TenancyDetailsFragment fragment = new TenancyDetailsFragment();
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
        View view = inflater.inflate(R.layout.activity_tenancy_details, container, false);
        AgentContact = (Button) view.findViewById(R.id.btnAgentContact);
        TenantNames = (Button) view.findViewById(R.id.btnTenantNames);
        InventoryProvider = (Button) view.findViewById(R.id.btnInventoryProvider);
        back = (FrameLayout) view.findViewById(R.id.flBack);
        imvPropertyImage = (ImageView) view.findViewById(R.id.imvPropertyImage);
        txvPropertyAddress = (TextView) view.findViewById(R.id.txvPropertyAddress);
        txvPropertyAddress2City = (TextView) view.findViewById(R.id.txvPropertyAddress2City);

        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        txvPropertyAddress.setText(preferences.getString("ADDRESS1", ""));
        txvPropertyAddress2City.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
        if (!preferences.getString("IMAGE", "").isEmpty()) {
        Picasso.with(getActivity())
                .load(preferences.getString("IMAGE", "")).resize(500,500)
                .into(imvPropertyImage);}

      /*  back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        AgentContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AgentListingActivity.class);
                startActivity(i);
            }
        });
        TenantNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TenantNamesActivity.class);
                startActivity(i);
            }
        });
        InventoryProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), InventoryProviderListingActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}

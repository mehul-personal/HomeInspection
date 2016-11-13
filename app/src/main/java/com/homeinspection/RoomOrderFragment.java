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

public class RoomOrderFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    Button btnStandardRoom, btnKitchen, btnBathroom, btnStairsAndLanding, btnOutsideSpaces;
    FrameLayout flBack;
    ImageView imvRoomOrderImage;
    TextView txvRoomOrderAddress, txvRoomOrderAddress2City;

    public RoomOrderFragment() {
    }

    public static RoomOrderFragment newInstance(String param1, String param2) {
        RoomOrderFragment fragment = new RoomOrderFragment();
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
        View view = inflater.inflate(R.layout.activity_room_order, container, false);
        flBack = (FrameLayout) view.findViewById(R.id.flBack);
        btnStandardRoom = (Button) view.findViewById(R.id.btnStandardRoom);
        btnKitchen = (Button) view.findViewById(R.id.btnKitchen);
        btnBathroom = (Button)view. findViewById(R.id.btnBathroom);
        btnStairsAndLanding = (Button) view.findViewById(R.id.btnStairsAndLanding);
        btnOutsideSpaces = (Button) view.findViewById(R.id.btnOutsideSpaces);

        imvRoomOrderImage = (ImageView) view.findViewById(R.id.imvRoomOrderImage);
        txvRoomOrderAddress = (TextView) view.findViewById(R.id.txvRoomOrderAddress);
        txvRoomOrderAddress2City = (TextView) view.findViewById(R.id.txvRoomOrderAddress2City);

        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        txvRoomOrderAddress.setText(preferences.getString("ADDRESS1", ""));
        txvRoomOrderAddress2City.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
        if (!preferences.getString("IMAGE", "").isEmpty()) {
            Picasso.with(getActivity())
                    .load(preferences.getString("IMAGE", "")).resize(500, 500)
                    .into(imvRoomOrderImage);
        }
        /*flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        btnStandardRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RoomTypeActivity.class);
                i.putExtra("CALL", "Standard Room");
                startActivity(i);
            }
        });
        btnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RoomTypeActivity.class);
                i.putExtra("CALL", "Kitchen");
                startActivity(i);
            }
        });
        btnBathroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RoomTypeActivity.class);
                i.putExtra("CALL", "Bathroom");
                startActivity(i);
            }
        });
        btnStairsAndLanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RoomTypeActivity.class);
                i.putExtra("CALL", "Stairs & Landing");
                startActivity(i);
            }
        });
        btnOutsideSpaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RoomTypeActivity.class);
                i.putExtra("CALL", "Outside Spaces");
                startActivity(i);
            }
        });
        return view;
    }
}

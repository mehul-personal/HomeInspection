package com.homeinspection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SafetyAndGeneralFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button btnSmokeAlarms, btnCarbonMonoxideAlarms, btnGasSafetyCertificate, btnFuseBoard,
            btnMainsStopCock, btnInstructions, btnFurnishingsSafety, btnKeys, btnOther;
    FrameLayout flBack;
    ImageView imvPropertyImage;
    TextView txvPropertyAddress, txvPropertyAddressCity;
    String in_smoke_alarams = "", in_smoke_alarams_additional = "", in_carbon_alarms = "", in_carbon_alarms_additional = "",
            in_gas_safety = "", in_gas_safety_additional = "", in_fuse_board = "", in_fuse_board_additional = "", in_mains_cock = "",
            in_mains_cock_additional = "", in_instructions_manuals = "", in_instructions_manuals_additional = "",
            in_furnishings_safety = "", in_furnishings_safety_additional = "", in_keys = "", in_keys_additional = "", in_other = "",
            in_other_additional = "";
    String out_smoke_alarams = "", out_smoke_alarams_additional = "", out_carbon_alarms = "", out_carbon_alarms_additional = "",
            out_gas_safety = "", out_gas_safety_additional = "", out_fuse_board = "", out_fuse_board_additional = "", out_mains_cock = "",
            out_mains_cock_additional = "", out_instructions_manuals = "", out_instructions_manuals_additional = "",
            out_furnishings_safety = "", out_furnishings_safety_additional = "", out_keys = "", out_keys_additional = "", out_other = "",
            out_other_additional = "";
    private String mParam1;
    private String mParam2;

    public SafetyAndGeneralFragment() {
    }

    public static SafetyAndGeneralFragment newInstance(String param1, String param2) {
        SafetyAndGeneralFragment fragment = new SafetyAndGeneralFragment();
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
        View view = inflater.inflate(R.layout.activity_safety_and_general, container, false);
        flBack = (FrameLayout) view.findViewById(R.id.flBack);
        btnSmokeAlarms = (Button) view.findViewById(R.id.btnSmokeAlarms);
        btnCarbonMonoxideAlarms = (Button) view.findViewById(R.id.btnCarbonMonoxideAlarms);
        btnGasSafetyCertificate = (Button) view.findViewById(R.id.btnGasSafetyCertificate);
        btnFuseBoard = (Button) view.findViewById(R.id.btnFuseBoard);
        btnMainsStopCock = (Button) view.findViewById(R.id.btnMainsStopCock);
        btnInstructions = (Button) view.findViewById(R.id.btnInstructions);
        btnFurnishingsSafety = (Button) view.findViewById(R.id.btnFurnishingsSafety);
        btnKeys = (Button) view.findViewById(R.id.btnKeys);
        btnOther = (Button) view.findViewById(R.id.btnOther);
        imvPropertyImage = (ImageView) view.findViewById(R.id.imvPropertyImage);
        txvPropertyAddress = (TextView) view.findViewById(R.id.txvPropertyAddress);
        txvPropertyAddressCity = (TextView) view.findViewById(R.id.txvPropertyAddressCity);

        SharedPreferences preferences = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        txvPropertyAddress.setText(preferences.getString("ADDRESS1", ""));
        txvPropertyAddressCity.setText(preferences.getString("ADDRESS2", "") + "          " + preferences.getString("CITY", ""));
        if (!preferences.getString("IMAGE", "").isEmpty()) {
            Picasso.with(getActivity())
                    .load(preferences.getString("IMAGE", "")).resize(500, 500)
                    .into(imvPropertyImage);
        }

       /* flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
        btnSmokeAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Smoke Alarms");
                i.putExtra("APICALL", "smoke_alarams");
                i.putExtra("IN_DESC", "" + in_smoke_alarams);
                i.putExtra("IN_ADDITIONAL", "" + in_smoke_alarams_additional);
                i.putExtra("OUT_DESC", "" + out_smoke_alarams);
                startActivityForResult(i, 10);
            }
        });
        btnCarbonMonoxideAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Carbon Monoxide Alarms");
                i.putExtra("APICALL", "carbon_alarms");
                i.putExtra("IN_DESC", "" + in_carbon_alarms);
                i.putExtra("IN_ADDITIONAL", "" + in_carbon_alarms_additional);
                i.putExtra("OUT_DESC", "" + out_carbon_alarms);
                startActivityForResult(i, 10);
            }
        });
        btnGasSafetyCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Gas Safety Certificate");
                i.putExtra("APICALL", "gas_safety");
                i.putExtra("IN_DESC", "" + in_gas_safety);
                i.putExtra("IN_ADDITIONAL", "" + in_gas_safety_additional);
                i.putExtra("OUT_DESC", "" + out_gas_safety);
                startActivityForResult(i, 10);
            }
        });
        btnFuseBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Fuse Board");
                i.putExtra("APICALL", "fuse_board");
                i.putExtra("IN_DESC", "" + in_fuse_board);
                i.putExtra("IN_ADDITIONAL", "" + in_fuse_board_additional);
                i.putExtra("OUT_DESC", "" + out_fuse_board);
                startActivityForResult(i, 10);
            }
        });
        btnMainsStopCock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Mains Stop Cock");
                i.putExtra("APICALL", "mains_cock");
                i.putExtra("IN_DESC", "" + in_mains_cock);
                i.putExtra("IN_ADDITIONAL", "" + in_mains_cock_additional);
                i.putExtra("OUT_DESC", "" + out_mains_cock);
                startActivityForResult(i, 10);
            }
        });
        btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Instructions / User Manuals");
                i.putExtra("APICALL", "instructions_manuals");
                i.putExtra("IN_DESC", "" + in_instructions_manuals);
                i.putExtra("IN_ADDITIONAL", "" + in_instructions_manuals_additional);
                i.putExtra("OUT_DESC", "" + out_instructions_manuals);
                startActivityForResult(i, 10);
            }
        });

        btnFurnishingsSafety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Furnishings Safety");
                i.putExtra("APICALL", "furnishings_safety");
                i.putExtra("IN_DESC", "" + in_furnishings_safety);
                i.putExtra("IN_ADDITIONAL", "" + in_furnishings_safety_additional);
                i.putExtra("OUT_DESC", "" + out_furnishings_safety);
                startActivityForResult(i, 10);
            }
        });
        btnKeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Keys");
                i.putExtra("APICALL", "keys");
                i.putExtra("IN_DESC", "" + in_keys);
                i.putExtra("IN_ADDITIONAL", "" + in_keys_additional);
                i.putExtra("OUT_DESC", "" + out_keys);
                startActivityForResult(i, 10);
            }
        });
        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SafetyAndGeneralCheckInOutActivity.class);
                i.putExtra("HEADER", "Other");
                i.putExtra("APICALL", "other");
                i.putExtra("IN_DESC", "" + in_other);
                i.putExtra("IN_ADDITIONAL", "" + in_other_additional);
                i.putExtra("OUT_DESC", "" + out_other);
                startActivityForResult(i, 10);
            }
        });
        getSafetyAndGeneralList();
        return view;
    }

   /* @Override
    protected void onRestart() {
        super.onRestart();
        getSafetyAndGeneralList();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 10) {
                getSafetyAndGeneralList();
            }
        }
    }

    public void getSafetyAndGeneralList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_safetygeneral";

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_safetygeneral", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {
                                JSONArray safetyGeneralCheckIn = dataObject.getJSONArray("safetygeneralcheckin");
                                JSONArray safetyGeneralCheckOut = dataObject.getJSONArray("safetygeneralcheckout");
                                if (safetyGeneralCheckIn.length() > 0) {
                                    JSONObject checkInObject = safetyGeneralCheckIn.getJSONObject(0);

                                    in_smoke_alarams = checkInObject.getString("smoke_alarams");
                                    in_smoke_alarams_additional = checkInObject.getString("smoke_alarams_additional");
                                    in_carbon_alarms = checkInObject.getString("carbon_alarms");
                                    in_carbon_alarms_additional = checkInObject.getString("carbon_alarms_additional");
                                    in_gas_safety = checkInObject.getString("gas_safety");
                                    in_gas_safety_additional = checkInObject.getString("gas_safety_additional");
                                    in_fuse_board = checkInObject.getString("fuse_board");
                                    in_fuse_board_additional = checkInObject.getString("fuse_board_additional");
                                    in_mains_cock = checkInObject.getString("mains_cock");
                                    in_mains_cock_additional = checkInObject.getString("mains_cock_additional");
                                    in_instructions_manuals = checkInObject.getString("instructions_manuals");
                                    in_instructions_manuals_additional = checkInObject.getString("instructions_manuals_additional");
                                    in_furnishings_safety = checkInObject.getString("furnishings_safety");
                                    in_furnishings_safety_additional = checkInObject.getString("furnishings_safety_additional");
                                    in_keys = checkInObject.getString("keys");
                                    in_keys_additional = checkInObject.getString("keys_additional");
                                    in_other = checkInObject.getString("other");
                                    in_other_additional = checkInObject.getString("other_additional");
                                }
                                if (safetyGeneralCheckOut.length() > 0) {
                                    JSONObject checkOutObject = safetyGeneralCheckOut.getJSONObject(0);
                                    out_smoke_alarams = checkOutObject.getString("smoke_alarams");
                                    out_smoke_alarams_additional = checkOutObject.getString("smoke_alarams_additional");
                                    out_carbon_alarms = checkOutObject.getString("carbon_alarms");
                                    out_carbon_alarms_additional = checkOutObject.getString("carbon_alarms_additional");
                                    out_gas_safety = checkOutObject.getString("gas_safety");
                                    out_gas_safety_additional = checkOutObject.getString("gas_safety_additional");
                                    out_fuse_board = checkOutObject.getString("fuse_board");
                                    out_fuse_board_additional = checkOutObject.getString("fuse_board_additional");
                                    out_mains_cock = checkOutObject.getString("mains_cock");
                                    out_mains_cock_additional = checkOutObject.getString("mains_cock_additional");
                                    out_instructions_manuals = checkOutObject.getString("instructions_manuals");
                                    out_instructions_manuals_additional = checkOutObject.getString("instructions_manuals_additional");
                                    out_furnishings_safety = checkOutObject.getString("furnishings_safety");
                                    out_furnishings_safety_additional = checkOutObject.getString("furnishings_safety_additional");
                                    out_keys = checkOutObject.getString("keys");
                                    out_keys_additional = checkOutObject.getString("keys_additional");
                                    out_other = checkOutObject.getString("other");
                                    out_other_additional = checkOutObject.getString("other_additional");
                                }
                            } else {
//                                Toast.makeText(SafetyAndGeneralFragment.this, "Oops! We can't found your previous History",
//                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_safetygeneral Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getActivity().getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}

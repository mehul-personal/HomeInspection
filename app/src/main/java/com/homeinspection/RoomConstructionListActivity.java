package com.homeinspection;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RoomConstructionListActivity extends AppCompatActivity {

    static String call, Room_id;
    Button btnFrame, btnDoor, btnCeiling, btnLightFittings, btnWalls, btnFittings,
            btnSwitchesAndSockets, btnWindows, btnWindowDressings, btnCupboard, btnSkirtings,
            btnHeating, btnFlooring, btnFurnitureAndOtherItem,
            btnKitchen, btnWorkSurface, btnSink, btnWallTiling, btnAddAppliances, btnAddSanitaryWare,
            btnBanisters, btnFrontOfProperty, btnSideOfProperty, btnRearOfProperty, btnOutBuildings, btnGarage;
    FrameLayout flBack;
    TextView header;
    String selectedCheckInImage1 = "", selectedCheckInImage2 = "", selectedCheckInImage3 = "", selectedCheckInImage4 = "",
            selectedCheckOutImage1 = "", selectedCheckOutImage2 = "", selectedCheckOutImage3 = "", selectedCheckOutImage4 = "";
    boolean FrameDuplicateRoom = true, DoorDuplicateRoom = true, CeilingDuplicateRoom = true, LightFittingsDuplicateRoom = true, WallsDuplicateRoom = true, FittingsDuplicateRoom = true,
            SwitchesAndSocketsDuplicateRoom = true, WindowsDuplicateRoom = true, WindowDressingsDuplicateRoom = true, CupboardDuplicateRoom = true, SkirtingsDuplicateRoom = true,
            HeatingDuplicateRoom = true, FlooringDuplicateRoom = true, FurnitureAndOtherItemDuplicateRoom = true,
            KitchenDuplicateRoom = true, WorkSurfaceDuplicateRoom = true, SinkDuplicateRoom = true, WallTilingDuplicateRoom = true, AddAppliancesDuplicateRoom = true, AddSanitaryWareDuplicateRoom = true,
            BanistersDuplicateRoom = true, FrontOfPropertyDuplicateRoom = true, SideOfPropertyDuplicateRoom = true, RearOfPropertyDuplicateRoom = true, OutBuildingsDuplicateRoom = true, GarageDuplicateRoom = true;
    String imagecall, DUPLICATE_ROOM = "NO", PREVIOUS_ROOM_ID = "", API_CALL = "";
    JSONArray ROOM_DESC, ROOM_FURNITURE, ROOM_APPLIANCES, ROOM_SANITARY;
    private Uri mFileUri;

    public static void createDirectory(String filePath) {
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (!isKitKat) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
                // handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_construction_list);

        flBack = (FrameLayout) findViewById(R.id.flBack);
        header = (TextView) findViewById(R.id.txvHeader);
        btnFrame = (Button) findViewById(R.id.btnFrame);
        btnDoor = (Button) findViewById(R.id.btnDoor);
        btnCeiling = (Button) findViewById(R.id.btnCeiling);
        btnLightFittings = (Button) findViewById(R.id.btnLightFittings);
        btnWalls = (Button) findViewById(R.id.btnWalls);
        btnFittings = (Button) findViewById(R.id.btnFittings);
        btnSwitchesAndSockets = (Button) findViewById(R.id.btnSwitchesAndSockets);
        btnWindows = (Button) findViewById(R.id.btnWindows);
        btnWindowDressings = (Button) findViewById(R.id.btnWindowDressings);
        btnCupboard = (Button) findViewById(R.id.btnCupboard);
        btnSkirtings = (Button) findViewById(R.id.btnSkirtings);
        btnHeating = (Button) findViewById(R.id.btnHeating);
        btnFlooring = (Button) findViewById(R.id.btnFlooring);
        btnFurnitureAndOtherItem = (Button) findViewById(R.id.btnFurnitureAndOtherItem);

        btnAddSanitaryWare = (Button) findViewById(R.id.btnAddSanitaryWare);

        btnKitchen = (Button) findViewById(R.id.btnKitchen);
        btnWorkSurface = (Button) findViewById(R.id.btnWorkSurface);
        btnSink = (Button) findViewById(R.id.btnSink);
        btnWallTiling = (Button) findViewById(R.id.btnWallTiling);
        btnAddAppliances = (Button) findViewById(R.id.btnAddAppliances);

        btnBanisters = (Button) findViewById(R.id.btnBanisters);
        btnFrontOfProperty = (Button) findViewById(R.id.btnFrontOfProperty);
        btnSideOfProperty = (Button) findViewById(R.id.btnSideOfProperty);
        btnRearOfProperty = (Button) findViewById(R.id.btnRearOfProperty);
        btnOutBuildings = (Button) findViewById(R.id.btnOutBuildings);
        btnGarage = (Button) findViewById(R.id.btnGarage);

        FrameDuplicateRoom = true;
        DoorDuplicateRoom = true;
        CeilingDuplicateRoom = true;
        LightFittingsDuplicateRoom = true;
        WallsDuplicateRoom = true;
        FittingsDuplicateRoom = true;
        SwitchesAndSocketsDuplicateRoom = true;
        WindowsDuplicateRoom = true;
        WindowDressingsDuplicateRoom = true;
        CupboardDuplicateRoom = true;
        SkirtingsDuplicateRoom = true;
        HeatingDuplicateRoom = true;
        FlooringDuplicateRoom = true;
        FurnitureAndOtherItemDuplicateRoom = true;
        KitchenDuplicateRoom = true;
        WorkSurfaceDuplicateRoom = true;
        SinkDuplicateRoom = true;
        WallTilingDuplicateRoom = true;
        AddAppliancesDuplicateRoom = true;
        AddSanitaryWareDuplicateRoom = true;
        BanistersDuplicateRoom = true;
        FrontOfPropertyDuplicateRoom = true;
        SideOfPropertyDuplicateRoom = true;
        RearOfPropertyDuplicateRoom = true;
        OutBuildingsDuplicateRoom = true;
        GarageDuplicateRoom = true;

        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent i = getIntent();
        header.setText(i.getStringExtra("HEADER"));
        call = i.getStringExtra("CALL");
        Room_id = i.getStringExtra("ROOM_ID");
        DUPLICATE_ROOM = i.getStringExtra("DUPLICATE");
        API_CALL = i.getStringExtra("API_CALL");
        try {
            ROOM_DESC = new JSONArray(i.getStringExtra("ROOM_DESC"));
            ROOM_FURNITURE = new JSONArray(i.getStringExtra("ROOM_FURNITURE"));
            ROOM_APPLIANCES = new JSONArray(i.getStringExtra("ROOM_APPLIANCES"));
            ROOM_SANITARY = new JSONArray(i.getStringExtra("ROOM_SANITARY"));
            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                PREVIOUS_ROOM_ID = i.getStringExtra("PREVIOUS_ROOM_ID");
//                if (call.equalsIgnoreCase("STANDARD")) {
//                    getStandardRoomList();
//                } else if (call.equalsIgnoreCase("KITCHEN")) {
//                    getKitchenRoomList();
//                } else if (call.equalsIgnoreCase("BATHROOM")) {
//                    getBathRoomList();
//                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
//                    getStairsAndListing();
//                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
//                    getOutsideSpaceListing();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ROOM_DESC = new JSONArray();
            ROOM_FURNITURE = new JSONArray();
            ROOM_APPLIANCES = new JSONArray();
            ROOM_SANITARY = new JSONArray();
        }
        if (call.equalsIgnoreCase("STANDARD")) {
            btnBanisters.setVisibility(View.GONE);
            btnAddSanitaryWare.setVisibility(View.GONE);
            btnKitchen.setVisibility(View.GONE);
            btnWorkSurface.setVisibility(View.GONE);
            btnSink.setVisibility(View.GONE);
            btnWallTiling.setVisibility(View.GONE);
            btnAddAppliances.setVisibility(View.GONE);
            btnFrontOfProperty.setVisibility(View.GONE);
            btnSideOfProperty.setVisibility(View.GONE);
            btnRearOfProperty.setVisibility(View.GONE);
            btnOutBuildings.setVisibility(View.GONE);
            btnGarage.setVisibility(View.GONE);
        } else if (call.equalsIgnoreCase("KITCHEN")) {
            btnBanisters.setVisibility(View.GONE);
            btnAddSanitaryWare.setVisibility(View.GONE);
            btnFrontOfProperty.setVisibility(View.GONE);
            btnSideOfProperty.setVisibility(View.GONE);
            btnRearOfProperty.setVisibility(View.GONE);
            btnOutBuildings.setVisibility(View.GONE);
            btnGarage.setVisibility(View.GONE);
        } else if (call.equalsIgnoreCase("BATHROOM")) {
            btnBanisters.setVisibility(View.GONE);
            btnKitchen.setVisibility(View.GONE);
            btnWorkSurface.setVisibility(View.GONE);
            btnSink.setVisibility(View.GONE);
            btnWallTiling.setVisibility(View.GONE);
            btnAddAppliances.setVisibility(View.GONE);
            btnFrontOfProperty.setVisibility(View.GONE);
            btnSideOfProperty.setVisibility(View.GONE);
            btnRearOfProperty.setVisibility(View.GONE);
            btnOutBuildings.setVisibility(View.GONE);
            btnGarage.setVisibility(View.GONE);
        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {

            btnFrame.setVisibility(View.GONE);
            btnDoor.setVisibility(View.GONE);
            btnAddSanitaryWare.setVisibility(View.GONE);
            btnKitchen.setVisibility(View.GONE);
            btnWorkSurface.setVisibility(View.GONE);
            btnSink.setVisibility(View.GONE);
            btnWallTiling.setVisibility(View.GONE);
            btnAddAppliances.setVisibility(View.GONE);
            btnFrontOfProperty.setVisibility(View.GONE);
            btnSideOfProperty.setVisibility(View.GONE);
            btnRearOfProperty.setVisibility(View.GONE);
            btnOutBuildings.setVisibility(View.GONE);
            btnGarage.setVisibility(View.GONE);
        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
            btnFrame.setVisibility(View.GONE);
            btnDoor.setVisibility(View.GONE);
            btnBanisters.setVisibility(View.GONE);
            btnCeiling.setVisibility(View.GONE);
            btnLightFittings.setVisibility(View.GONE);
            btnWalls.setVisibility(View.GONE);
            btnFittings.setVisibility(View.GONE);
            btnSwitchesAndSockets.setVisibility(View.GONE);
            btnWindows.setVisibility(View.GONE);
            btnWindowDressings.setVisibility(View.GONE);
            btnCupboard.setVisibility(View.GONE);
            btnSkirtings.setVisibility(View.GONE);
            btnHeating.setVisibility(View.GONE);
            btnFlooring.setVisibility(View.GONE);
            btnAddSanitaryWare.setVisibility(View.GONE);
            btnFurnitureAndOtherItem.setVisibility(View.GONE);
            btnKitchen.setVisibility(View.GONE);
            btnWorkSurface.setVisibility(View.GONE);
            btnSink.setVisibility(View.GONE);
            btnWallTiling.setVisibility(View.GONE);
            btnAddAppliances.setVisibility(View.GONE);
        }

        btnBanisters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("banisters", btnBanisters.getText().toString(), Room_id, BanistersDuplicateRoom);
               /* String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, flag = 0;//,previousindex = 0, previousflag = 0;
                String MATCHID = "",DESC_ID="";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        DESC_ID=jsob.getString("id");
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("banisters") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
//                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
//                            previousflag++;
//                            previousindex = j;
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES") && BanistersDuplicateRoom) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

//                    final int finalPreviousflag = previousflag;
//                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    final String finalDESC_ID = DESC_ID;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                            PREVIOUS_ROOM_ID, Room_id, finalDESC_ID,"banisters");
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "banisters", btnBanisters.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "banisters", btnBanisters.getText().toString());
                }*/
            }
        });
        btnFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("frame", btnFrame.getText().toString(), Room_id, FrameDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("frame") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "frame", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnFrame.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "frame", btnFrame.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "frame", btnFrame.getText().toString());
                }*/

            }
        });
        btnDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("door", btnDoor.getText().toString(), Room_id, DoorDuplicateRoom);
               /* String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("door") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "door", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnDoor.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "door", btnDoor.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "door", btnDoor.getText().toString());
                }*/

            }
        });
        btnCeiling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("ceiling", btnCeiling.getText().toString(), Room_id, CeilingDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("ceiling") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "ceiling", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnCeiling.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "ceiling", btnCeiling.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "ceiling", btnCeiling.getText().toString());
                }*/

            }
        });
        btnLightFittings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("light_fitting", btnLightFittings.getText().toString(), Room_id, LightFittingsDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("light_fitting") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "light_fitting", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnLightFittings.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "light_fitting", btnLightFittings.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "light_fitting", btnLightFittings.getText().toString());
                }
*/
            }
        });
        btnWalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("walls", btnWalls.getText().toString(), Room_id, WallsDuplicateRoom);
               /* String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("walls") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "walls", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnWalls.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "walls", btnWalls.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "walls", btnWalls.getText().toString());
                }*/

            }
        });
        btnFittings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("fittings", btnFittings.getText().toString(), Room_id, FittingsDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("fittings") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "fittings", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnFittings.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "fittings", btnFittings.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "fittings", btnFittings.getText().toString());
                }*/
            }
        });
        btnSwitchesAndSockets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("switch_sockets", btnSwitchesAndSockets.getText().toString(), Room_id, SwitchesAndSocketsDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("switch_sockets") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "switch_sockets", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnSwitchesAndSockets.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "switch_sockets", btnSwitchesAndSockets.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "switch_sockets", btnSwitchesAndSockets.getText().toString());
                }*/

            }
        });
        btnWindows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("window", btnWindows.getText().toString(), Room_id, WindowsDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("window") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "window", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnWindows.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "window", btnWindows.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "window", btnWindows.getText().toString());
                }*/

            }
        });
        btnWindowDressings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("window_dressing", btnWindowDressings.getText().toString(), Room_id, WindowDressingsDuplicateRoom);
               /* String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("window_dressing") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "window_dressing", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnWindowDressings.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "window_dressing", btnWindowDressings.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "window_dressing", btnWindowDressings.getText().toString());
                }
*/

            }
        });
        btnCupboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("cupboard", btnCupboard.getText().toString(), Room_id, CupboardDuplicateRoom);
                /*String apicall = "";
                if (call.equalsIgnoreCase("STANDARD")) {
                    apicall = "insertstandard_room_desc";
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    apicall = "insertkitchen_room_desc";
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    apicall = "insertbathroom_desc";
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    apicall = "insertstairs_landing_desc";
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    apicall = "insertoutside_space_desc";
                }
                int index = 0, previousindex = 0, flag = 0, previousflag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("cupboard") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                        if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                            previousflag++;
                            previousindex = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
                    builder.setCancelable(false);

                    if (call.equalsIgnoreCase("STANDARD")) {
                        builder.setMessage("Do you want to duplicate previous Room data?");
                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                        builder.setMessage("Do you want to duplicate previous Kitchen data?");
                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                        builder.setMessage("Do you want to duplicate previous Bathroom data?");
                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                        builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                        builder.setMessage("Do you want to duplicate previous Outside space data?");
                    }

                    final int finalPreviousflag = previousflag;
                    final int finalPreviousindex = previousindex;
                    final int finalFlag = flag;
                    final int finalIndex = index;
                    final String finalApicall = apicall;
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject prevjsob = null;
                            String desc = "", add_comment = "";
                            if (DUPLICATE_ROOM.equalsIgnoreCase("YES")) {
                                if (finalPreviousflag > 0) {
                                    try {
                                        prevjsob = ROOM_DESC.getJSONObject(finalPreviousindex);
                                        desc = prevjsob.getString("general_desc").equalsIgnoreCase("null") ? "" : prevjsob.getString("general_desc");
                                        add_comment = prevjsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : prevjsob.getString("additional_comm");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            copyRoomDetails(finalApicall, Room_id, "cupboard", desc, add_comment, "", "", "", "",
                                    getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                                    finalIndex, finalFlag, btnCupboard.getText().toString());
                            dialog.dismiss();
                        }
                    });


                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callNextPage(finalApicall, finalIndex, finalFlag, "cupboard", btnCupboard.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    callNextPage(apicall, index, flag, "cupboard", btnCupboard.getText().toString());
                }*/


            }
        });
        btnSkirtings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("skirting", btnSkirtings.getText().toString(), Room_id, SkirtingsDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnSkirtings.getText().toString());
                i.putExtra("TITLE", "skirting");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("skirting") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnHeating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("heating", btnHeating.getText().toString(), Room_id, HeatingDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnHeating.getText().toString());
                i.putExtra("TITLE", "heating");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("heating") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("kitchen", btnKitchen.getText().toString(), Room_id, KitchenDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnKitchen.getText().toString());
                i.putExtra("TITLE", "kitchen");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("kitchen") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnWorkSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("worksurface", btnWorkSurface.getText().toString(), Room_id, WorkSurfaceDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnWorkSurface.getText().toString());
                i.putExtra("TITLE", "worksurface");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("worksurface") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnSink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("sink", btnSink.getText().toString(), Room_id, SinkDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnSink.getText().toString());
                i.putExtra("TITLE", "sink");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("sink") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnWallTiling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonData("walltiling", btnWallTiling.getText().toString(), Room_id, WallTilingDuplicateRoom);
               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnWallTiling.getText().toString());
                i.putExtra("TITLE", "walltiling");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("walltiling") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnFlooring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("flooring", btnFlooring.getText().toString(), Room_id, FlooringDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnFlooring.getText().toString());
                i.putExtra("TITLE", "flooring");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("flooring") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnFrontOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("front_of_property", btnFrontOfProperty.getText().toString(), Room_id, FrontOfPropertyDuplicateRoom);
               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnFrontOfProperty.getText().toString());
                i.putExtra("TITLE", "front_of_property");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("front_of_property") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnSideOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("side_of_property", btnSideOfProperty.getText().toString(), Room_id, SideOfPropertyDuplicateRoom);
               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnSideOfProperty.getText().toString());
                i.putExtra("TITLE", "side_of_property");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("side_of_property") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnRearOfProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("rear_of_property", btnRearOfProperty.getText().toString(), Room_id, RearOfPropertyDuplicateRoom);
               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnRearOfProperty.getText().toString());
                i.putExtra("TITLE", "rear_of_property");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("rear_of_property") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnOutBuildings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("out_buildings", btnOutBuildings.getText().toString(), Room_id, OutBuildingsDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnOutBuildings.getText().toString());
                i.putExtra("TITLE", "out_buildings");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("out_buildings") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnGarage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonData("garage", btnGarage.getText().toString(), Room_id, GarageDuplicateRoom);
                /*Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
                i.putExtra("HEADER", "" + btnGarage.getText().toString());
                i.putExtra("TITLE", "garage");
                if (call.equalsIgnoreCase("STANDARD")) {
                    i.putExtra("API_CALL", "insertstandard_room_desc");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    i.putExtra("API_CALL", "insertkitchen_room_desc");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_desc");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    i.putExtra("API_CALL", "insertstairs_landing_desc");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    i.putExtra("API_CALL", "insertoutside_space_desc");
                }
                i.putExtra("ROOM_ID", Room_id + "");
                int index = 0, flag = 0;
                String MATCHID = "";
                for (int j = 0; j < ROOM_DESC.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(j);
                        if (call.equalsIgnoreCase("STANDARD")) {
                            MATCHID = jsob.getString("sid");
                        } else if (call.equalsIgnoreCase("KITCHEN")) {
                            MATCHID = jsob.getString("kid");
                        } else if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID = jsob.getString("bid");
                        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                            MATCHID = jsob.getString("slid");
                        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                            MATCHID = jsob.getString("osid");
                        }
                        if (jsob.getString("title").equalsIgnoreCase("garage") && Room_id.equalsIgnoreCase(MATCHID)) {
                            flag++;
                            index = j;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                if (flag > 0) {
                    try {
                        JSONObject jsob = ROOM_DESC.getJSONObject(index);
                        i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                        i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                        i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                        i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                        i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                        i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i.putExtra("DESCRIPTION", "");
                    i.putExtra("ADDITIONAL", "");
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                }
                startActivity(i);*/
            }
        });
        btnFurnitureAndOtherItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String apicall = "";
//                if (call.equalsIgnoreCase("STANDARD")) {
//                    apicall= "insertstandard_room_furniture";
//                } else if (call.equalsIgnoreCase("KITCHEN")) {
//                    apicall="insertkitchen_room_furniture";
//                } else if (call.equalsIgnoreCase("BATHROOM")) {
//                    apicall= "insertbathroom_furniture";
//                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
//                    apicall= "insertstairs_landing_furniture";
//                }
                callFurniture();
            }
        });
        btnAddAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAppliances();
            }
        });
        btnAddSanitaryWare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callSanitry();

               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomListFurnitureActivity.class);
                i.putExtra("HEADER", "" + "Sanitary");

                if (call.equalsIgnoreCase("BATHROOM")) {
                    i.putExtra("API_CALL", "insertbathroom_sanitary");
                } else {
                    i.putExtra("API_CALL", "");
                }
                i.putExtra("CALL", call);
                i.putExtra("API_CALL", "insertbathroom_sanitary");
                i.putExtra("TITLE", "");
                i.putExtra("ROOM_ID", Room_id + "");
                int flag2 = 0;
                String MATCHID2 = "";
                for (int j = 0; j < ROOM_SANITARY.length(); j++) {
                    try {
                        JSONObject jsob = ROOM_SANITARY.getJSONObject(j);
                        if (call.equalsIgnoreCase("BATHROOM")) {
                            MATCHID2 = jsob.getString("bid");
                        }
                        if (Room_id.equalsIgnoreCase(MATCHID2)) {
                            flag2++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (flag2 > 0) {
                    i.putExtra("ROOM_SANITARY", ROOM_SANITARY.toString());
                } else {
                    i.putExtra("ROOM_SANITARY", "[]");
                }
                startActivity(i);*/
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (call.equalsIgnoreCase("STANDARD")) {
            getStandardRoomList(false);
        } else if (call.equalsIgnoreCase("KITCHEN")) {
            getKitchenRoomList(false);
        } else if (call.equalsIgnoreCase("BATHROOM")) {
            getBathRoomList(false);
        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
            getStairsAndListing(false);
        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
            getOutsideSpaceListing(false);
        }
    }

    public void callFurniture() {
        int index = 0, flag = 0, previousindex = 0, previousflag = 0;
        String MATCHID = "", DESC_ID = "", PREVIOUS_DESC_ID = "";
        for (int j = 0; j < ROOM_FURNITURE.length(); j++) {
            try {
                JSONObject jsob = ROOM_FURNITURE.getJSONObject(j);
                if (call.equalsIgnoreCase("STANDARD")) {
                    MATCHID = jsob.getString("sid");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    MATCHID = jsob.getString("kid");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    MATCHID = jsob.getString("bid");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    MATCHID = jsob.getString("swid");
                }
                if (Room_id.equalsIgnoreCase(MATCHID)) {
                    flag++;
                    DESC_ID = jsob.getString("id");
                }
                if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                    previousflag++;
                    previousindex = j;
                    PREVIOUS_DESC_ID = jsob.getString("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (DUPLICATE_ROOM.equalsIgnoreCase("YES") && FurnitureAndOtherItemDuplicateRoom) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
            builder.setCancelable(false);

            if (call.equalsIgnoreCase("STANDARD")) {
                builder.setMessage("Do you want to duplicate previous Room data?");
            } else if (call.equalsIgnoreCase("KITCHEN")) {
                builder.setMessage("Do you want to duplicate previous Kitchen data?");
            } else if (call.equalsIgnoreCase("BATHROOM")) {
                builder.setMessage("Do you want to duplicate previous Bathroom data?");
            } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
            } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                builder.setMessage("Do you want to duplicate previous Outside space data?");
            }
            final String finalDESC_ID = DESC_ID;
            final String finalPREVIOUS_DESC_ID = PREVIOUS_DESC_ID;
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                            PREVIOUS_ROOM_ID, Room_id, finalPREVIOUS_DESC_ID, "", "Furniture");
                    dialog.dismiss();
                }
            });
            final int finalFlag = flag;
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callFurnitureNextPage(finalFlag);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            callFurnitureNextPage(flag);
        }
    }

    public void callAppliances() {
        int index = 0, flag = 0, previousindex = 0, previousflag = 0;
        String MATCHID = "", DESC_ID = "", PREVIOUS_DESC_ID = "";
        for (int j = 0; j < ROOM_APPLIANCES.length(); j++) {
            try {
                JSONObject jsob = ROOM_APPLIANCES.getJSONObject(j);
                if (call.equalsIgnoreCase("KITCHEN")) {
                    MATCHID = jsob.getString("kid");
                }
                if (Room_id.equalsIgnoreCase(MATCHID)) {
                    flag++;
                    DESC_ID = jsob.getString("id");
                }
                if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                    previousflag++;
                    previousindex = j;
                    PREVIOUS_DESC_ID = jsob.getString("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (DUPLICATE_ROOM.equalsIgnoreCase("YES") && AddAppliancesDuplicateRoom) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
            builder.setCancelable(false);

            if (call.equalsIgnoreCase("STANDARD")) {
                builder.setMessage("Do you want to duplicate previous Room data?");
            } else if (call.equalsIgnoreCase("KITCHEN")) {
                builder.setMessage("Do you want to duplicate previous Kitchen data?");
            } else if (call.equalsIgnoreCase("BATHROOM")) {
                builder.setMessage("Do you want to duplicate previous Bathroom data?");
            } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
            } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                builder.setMessage("Do you want to duplicate previous Outside space data?");
            }
            final String finalDESC_ID = DESC_ID;
            final String finalPREVIOUS_DESC_ID = PREVIOUS_DESC_ID;
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                            PREVIOUS_ROOM_ID, Room_id, finalPREVIOUS_DESC_ID, "", "Appliances");
                    dialog.dismiss();
                }
            });
            final int finalFlag = flag;
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callApplianceNextPage(finalFlag);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            callApplianceNextPage(flag);
        }
    }

    public void callSanitry() {
        int index = 0, flag = 0, previousindex = 0, previousflag = 0;
        String MATCHID = "", DESC_ID = "", PREVIOUS_DESC_ID = "";
        for (int j = 0; j < ROOM_SANITARY.length(); j++) {
            try {
                JSONObject jsob = ROOM_SANITARY.getJSONObject(j);
                if (call.equalsIgnoreCase("BATHROOM")) {
                    MATCHID = jsob.getString("bid");
                }
                if (Room_id.equalsIgnoreCase(MATCHID)) {
                    flag++;
                    DESC_ID = jsob.getString("id");
                }
                if (PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                    previousflag++;
                    previousindex = j;
                    PREVIOUS_DESC_ID = jsob.getString("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (DUPLICATE_ROOM.equalsIgnoreCase("YES") && AddSanitaryWareDuplicateRoom) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
            builder.setCancelable(false);

            if (call.equalsIgnoreCase("STANDARD")) {
                builder.setMessage("Do you want to duplicate previous Room data?");
            } else if (call.equalsIgnoreCase("KITCHEN")) {
                builder.setMessage("Do you want to duplicate previous Kitchen data?");
            } else if (call.equalsIgnoreCase("BATHROOM")) {
                builder.setMessage("Do you want to duplicate previous Bathroom data?");
            } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
            } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                builder.setMessage("Do you want to duplicate previous Outside space data?");
            }
            final String finalDESC_ID = DESC_ID;
            final String finalPREVIOUS_DESC_ID = PREVIOUS_DESC_ID;
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                            PREVIOUS_ROOM_ID, Room_id, finalPREVIOUS_DESC_ID, "", "Sanitary");
                    dialog.dismiss();
                }
            });
            final int finalFlag = flag;
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callSanitryNextPage(finalFlag);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            callSanitryNextPage(flag);
        }
    }

    public void callFurnitureNextPage(int flag) {
        Intent i = new Intent(RoomConstructionListActivity.this, RoomListFurnitureActivity.class);
        i.putExtra("HEADER", "Furniture");
        if (call.equalsIgnoreCase("STANDARD")) {
            i.putExtra("API_CALL", "insertstandard_room_furniture");
        } else if (call.equalsIgnoreCase("KITCHEN")) {
            i.putExtra("API_CALL", "insertkitchen_room_furniture");
        } else if (call.equalsIgnoreCase("BATHROOM")) {
            i.putExtra("API_CALL", "insertbathroom_furniture");
        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
            i.putExtra("API_CALL", "insertstairs_landing_furniture");
        }
        i.putExtra("CALL", call);
        i.putExtra("TITLE", "");
        i.putExtra("ROOM_ID", Room_id + "");

        if (flag > 0) {
            i.putExtra("ROOM_FURNITURE", ROOM_FURNITURE.toString());
        } else {
            i.putExtra("ROOM_FURNITURE", "[]");
        }
        startActivity(i);
    }

    public void callApplianceNextPage(int flag) {
        Intent i = new Intent(RoomConstructionListActivity.this, RoomListFurnitureActivity.class);
        i.putExtra("HEADER", "Appliances");
        if (call.equalsIgnoreCase("KITCHEN")) {
            i.putExtra("API_CALL", "insertkitchen_room_appliances");
        } else {
            i.putExtra("API_CALL", "");
        }
        i.putExtra("CALL", call);
        i.putExtra("TITLE", "");
        i.putExtra("ROOM_ID", Room_id + "");

        if (flag > 0) {
            i.putExtra("ROOM_APPLIANCES", ROOM_APPLIANCES.toString());
        } else {
            i.putExtra("ROOM_APPLIANCES", "[]");
        }
        startActivity(i);
    }

    public void callSanitryNextPage(int flag) {

        Intent i = new Intent(RoomConstructionListActivity.this, RoomListFurnitureActivity.class);
        i.putExtra("HEADER", "" + "Sanitary");

        if (call.equalsIgnoreCase("BATHROOM")) {
            i.putExtra("API_CALL", "insertbathroom_sanitary");
        } else {
            i.putExtra("API_CALL", "");
        }
        i.putExtra("CALL", call);
        i.putExtra("TITLE", "");
        i.putExtra("ROOM_ID", Room_id + "");

        if (flag > 0) {
            i.putExtra("ROOM_SANITARY", ROOM_SANITARY.toString());
        } else {
            i.putExtra("ROOM_SANITARY", "[]");
        }
        startActivity(i);

    }

    public void callNextPage(String apicall, int index, int flag, String title, String header, Boolean buttonDuplicateStatus) {
        Intent i = new Intent(RoomConstructionListActivity.this, RoomOrderCheckInOutDescCommentActivity.class);
        i.putExtra("HEADER", header);
        i.putExtra("TITLE", title);
        i.putExtra("API_CALL", apicall);
        i.putExtra("ROOM_ID", Room_id + "");
        if (flag > 0) {
            try {
                JSONObject jsob = ROOM_DESC.getJSONObject(index);
                i.putExtra("DESCRIPTION", jsob.getString("general_desc").equalsIgnoreCase("null") ? "" : jsob.getString("general_desc"));
                i.putExtra("ADDITIONAL", jsob.getString("additional_comm").equalsIgnoreCase("null") ? "" : jsob.getString("additional_comm"));
                if (!buttonDuplicateStatus) {
                    i.putExtra("IMAGE1", "");
                    i.putExtra("IMAGE2", "");
                    i.putExtra("IMAGE3", "");
                    i.putExtra("IMAGE4", "");
                } else {
                    i.putExtra("IMAGE1", jsob.getString("image1").equalsIgnoreCase("null") ? "" : jsob.getString("image1"));
                    i.putExtra("IMAGE2", jsob.getString("image2").equalsIgnoreCase("null") ? "" : jsob.getString("image2"));
                    i.putExtra("IMAGE3", jsob.getString("image3").equalsIgnoreCase("null") ? "" : jsob.getString("image3"));
                    i.putExtra("IMAGE4", jsob.getString("image4").equalsIgnoreCase("null") ? "" : jsob.getString("image4"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            i.putExtra("DESCRIPTION", "");
            i.putExtra("ADDITIONAL", "");
            i.putExtra("IMAGE1", "");
            i.putExtra("IMAGE2", "");
            i.putExtra("IMAGE3", "");
            i.putExtra("IMAGE4", "");
        }
        startActivity(i);
    }

    public void getStandardRoomList(final boolean flag) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_standarroom";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_standarroom", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String IN_ROOM_DESC = "[]", IN_ROOM_FURNITURE = "[]", IN_ROOM_APPLIANCES = "[]", IN_ROOM_SANITARY = "[]",
                                    OUT_ROOM_DESC = "[]", OUT_ROOM_FURNITURE = "[]", OUT_ROOM_APPLIANCES = "[]", OUT_ROOM_SANITARY = "[]";
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("standard_roomcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("standard_roomcheckout");
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    JSONArray standardroom_array = checkInObject.getJSONArray("standard_room");
                                    JSONArray standardroomdesc_array = checkInObject.getJSONArray("standard_room_desc");
                                    JSONArray standardroomfurniture_array = checkInObject.getJSONArray("standard_furniture");
                                    IN_ROOM_DESC = standardroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = standardroomfurniture_array.toString();

                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);

                                }
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    JSONArray standardroom_array = checkOutObject.getJSONArray("standard_room");
                                    JSONArray standardroomdesc_array = checkOutObject.getJSONArray("standard_room_desc");
                                    JSONArray standardroomfurniture_array = checkOutObject.getJSONArray("standard_furniture");
                                    OUT_ROOM_DESC = standardroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = standardroomfurniture_array.toString();

                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standard_roomcheckin.length() > 0) {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standard_roomcheckout.length() > 0) {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    }
                                }

                            } else {
                                ROOM_DESC = new JSONArray();
                                ROOM_FURNITURE = new JSONArray();
                                ROOM_APPLIANCES = new JSONArray();
                                ROOM_SANITARY = new JSONArray();
                            }
                            if (flag) {
                                if (!FurnitureAndOtherItemDuplicateRoom) {
                                    callFurniture();
                                } else if (!AddAppliancesDuplicateRoom) {
                                    callAppliances();
                                } else if (!AddSanitaryWareDuplicateRoom) {
                                    callSanitry();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_standarroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getKitchenRoomList(final boolean flag) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_kitchenroom";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_kitchenroom", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String IN_ROOM_DESC = "[]", IN_ROOM_FURNITURE = "[]", IN_ROOM_APPLIANCES = "[]", IN_ROOM_SANITARY = "[]",
                                    OUT_ROOM_DESC = "[]", OUT_ROOM_FURNITURE = "[]", OUT_ROOM_APPLIANCES = "[]", OUT_ROOM_SANITARY = "[]";
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("kitchenroomcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("kitchenroomcheckout");
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    JSONArray kitchenroom_array = checkInObject.getJSONArray("kitchen_room");
                                    JSONArray kitchenroomdesc_array = checkInObject.getJSONArray("kitchen_room_desc");
                                    JSONArray kitchenroomfurniture_array = checkInObject.getJSONArray("kitchen_furniture");
                                    JSONArray kitchenroomAppliances = checkInObject.getJSONArray("kitchen_appliances");
                                    IN_ROOM_DESC = kitchenroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = kitchenroomfurniture_array.toString();

                                    IN_ROOM_APPLIANCES = kitchenroomAppliances.toString();
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    JSONArray kitchenroom_array = checkOutObject.getJSONArray("kitchen_room");
                                    JSONArray kitchenroomdesc_array = checkOutObject.getJSONArray("kitchen_room_desc");
                                    JSONArray kitchenroomfurniture_array = checkOutObject.getJSONArray("kitchen_furniture");
                                    JSONArray kitchenroomAppliances = checkOutObject.getJSONArray("kitchen_appliances");
                                    OUT_ROOM_DESC = kitchenroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = kitchenroomfurniture_array.toString();

                                    OUT_ROOM_APPLIANCES = kitchenroomAppliances.toString();
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standard_roomcheckin.length() > 0) {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standard_roomcheckout.length() > 0) {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    }
                                }
                            } else {
                                ROOM_DESC = new JSONArray();
                                ROOM_FURNITURE = new JSONArray();
                                ROOM_APPLIANCES = new JSONArray();
                                ROOM_SANITARY = new JSONArray();
                            }
                            if (flag) {
                                if (!FurnitureAndOtherItemDuplicateRoom) {
                                    callFurniture();
                                } else if (!AddAppliancesDuplicateRoom) {
                                    callAppliances();
                                } else if (!AddSanitaryWareDuplicateRoom) {
                                    callSanitry();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_kitchenroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getBathRoomList(final boolean flag) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_bathroom";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_bathroom", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String IN_ROOM_DESC = "[]", IN_ROOM_FURNITURE = "[]", IN_ROOM_APPLIANCES = "[]", IN_ROOM_SANITARY = "[]",
                                    OUT_ROOM_DESC = "[]", OUT_ROOM_FURNITURE = "[]", OUT_ROOM_APPLIANCES = "[]", OUT_ROOM_SANITARY = "[]";
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("bathroomlistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("bathroomlistcheckout");
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    JSONArray bathroom_array = checkInObject.getJSONArray("bathroom");
                                    JSONArray bathroomdesc_array = checkInObject.getJSONArray("bathroom_desc");
                                    JSONArray bathroomfurniture_array = checkInObject.getJSONArray("bathroom_furniture");
                                    JSONArray bathroomsanitary = checkInObject.getJSONArray("bathroom_sanitary");
                                    IN_ROOM_DESC = bathroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = bathroomfurniture_array.toString();

                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = bathroomsanitary.toString();
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    JSONArray bathroom_array = checkOutObject.getJSONArray("bathroom");
                                    JSONArray bathroomdesc_array = checkOutObject.getJSONArray("bathroom_desc");
                                    JSONArray bathroomfurniture_array = checkOutObject.getJSONArray("bathroom_furniture");
                                    JSONArray bathroomsanitary = checkOutObject.getJSONArray("bathroom_sanitary");
                                    OUT_ROOM_DESC = bathroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = bathroomfurniture_array.toString();

                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = bathroomsanitary.toString();
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standard_roomcheckin.length() > 0) {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standard_roomcheckout.length() > 0) {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    }
                                }
                            } else {
                                ROOM_DESC = new JSONArray();
                                ROOM_FURNITURE = new JSONArray();
                                ROOM_APPLIANCES = new JSONArray();
                                ROOM_SANITARY = new JSONArray();
                            }
                            if (flag) {
                                if (!FurnitureAndOtherItemDuplicateRoom) {
                                    callFurniture();
                                } else if (!AddAppliancesDuplicateRoom) {
                                    callAppliances();
                                } else if (!AddSanitaryWareDuplicateRoom) {
                                    callSanitry();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_bathroom Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getStairsAndListing(final boolean flag) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_stairslanding";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_stairslanding", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String IN_ROOM_DESC = "[]", IN_ROOM_FURNITURE = "[]", IN_ROOM_APPLIANCES = "[]", IN_ROOM_SANITARY = "[]",
                                    OUT_ROOM_DESC = "[]", OUT_ROOM_FURNITURE = "[]", OUT_ROOM_APPLIANCES = "[]", OUT_ROOM_SANITARY = "[]";
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("stairs_landinglistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("stairs_landinglistcheckout");
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    JSONArray stairs_landing_array = checkInObject.getJSONArray("stairs_landing");
                                    JSONArray stairs_landingdesc_array = checkInObject.getJSONArray("stairs_landing_desc");
                                    JSONArray stairs_landingfurniture_array = checkInObject.getJSONArray("stairs_landing_furniture");
                                    IN_ROOM_DESC = stairs_landingdesc_array.toString();
                                    IN_ROOM_FURNITURE = stairs_landingfurniture_array.toString();

                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    JSONArray stairs_landing_array = checkOutObject.getJSONArray("stairs_landing");
                                    JSONArray stairs_landingdesc_array = checkOutObject.getJSONArray("stairs_landing_desc");
                                    JSONArray stairs_landingfurniture_array = checkOutObject.getJSONArray("stairs_landing_furniture");
                                    OUT_ROOM_DESC = stairs_landingdesc_array.toString();
                                    OUT_ROOM_FURNITURE = stairs_landingfurniture_array.toString();
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standard_roomcheckin.length() > 0) {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standard_roomcheckout.length() > 0) {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    }
                                }
                            } else {
                                ROOM_DESC = new JSONArray();
                                ROOM_FURNITURE = new JSONArray();
                                ROOM_APPLIANCES = new JSONArray();
                                ROOM_SANITARY = new JSONArray();
                            }
                            if (flag) {
                                if (!FurnitureAndOtherItemDuplicateRoom) {
                                    callFurniture();
                                } else if (!AddAppliancesDuplicateRoom) {
                                    callAppliances();
                                } else if (!AddSanitaryWareDuplicateRoom) {
                                    callSanitry();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_stairslanding Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getOutsideSpaceListing(final boolean flag) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_outsidespace";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_outsidespace", response.toString());

                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            String IN_ROOM_DESC = "[]", IN_ROOM_FURNITURE = "[]", IN_ROOM_APPLIANCES = "[]", IN_ROOM_SANITARY = "[]",
                                    OUT_ROOM_DESC = "[]", OUT_ROOM_FURNITURE = "[]", OUT_ROOM_APPLIANCES = "[]", OUT_ROOM_SANITARY = "[]";
                            if (dataObject.getBoolean("result")) {
                                JSONArray standard_roomcheckin = dataObject.getJSONArray("outsidespacelistcheckin");
                                JSONArray standard_roomcheckout = dataObject.getJSONArray("outsidespacelistcheckout");
                                if (standard_roomcheckin.length() > 0) {
                                    JSONObject checkInObject = standard_roomcheckin.getJSONObject(0);
                                    JSONArray outsideroom_array = checkInObject.getJSONArray("outside_space");
                                    JSONArray outsideroomdesc_array = checkInObject.getJSONArray("outside_space_desc");
                                    IN_ROOM_DESC = outsideroomdesc_array.toString();
                                    IN_ROOM_FURNITURE = "[]";

                                    IN_ROOM_APPLIANCES = "[]";
                                    IN_ROOM_SANITARY = "[]";
//                                    adapter = new StandardRoomListAdapter(IN_SID, IN_ROOMNAME, IN_CHECK_LIST, IN_ROOM_DESC, IN_ROOM_FURNITURE);
//                                    lsvStandardRoom.setAdapter(adapter);


                                }
                                if (standard_roomcheckout.length() > 0) {
                                    JSONObject checkOutObject = standard_roomcheckout.getJSONObject(0);
                                    JSONArray outsideroom_array = checkOutObject.getJSONArray("outside_space");
                                    JSONArray outsideroomdesc_array = checkOutObject.getJSONArray("outside_space_desc");
                                    OUT_ROOM_DESC = outsideroomdesc_array.toString();
                                    OUT_ROOM_FURNITURE = "[]";
                                    OUT_ROOM_APPLIANCES = "[]";
                                    OUT_ROOM_SANITARY = "[]";
                                }
                                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                                    if (standard_roomcheckin.length() > 0) {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    }
                                } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                                    if (standard_roomcheckout.length() > 0) {
                                        ROOM_DESC = new JSONArray(OUT_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(OUT_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(OUT_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(OUT_ROOM_SANITARY);
                                    } else {
                                        ROOM_DESC = new JSONArray(IN_ROOM_DESC);
                                        ROOM_FURNITURE = new JSONArray(IN_ROOM_FURNITURE);
                                        ROOM_APPLIANCES = new JSONArray(IN_ROOM_APPLIANCES);
                                        ROOM_SANITARY = new JSONArray(IN_ROOM_SANITARY);
                                    }
                                }
                            } else {
                                ROOM_DESC = new JSONArray();
                                ROOM_FURNITURE = new JSONArray();
                                ROOM_APPLIANCES = new JSONArray();
                                ROOM_SANITARY = new JSONArray();
                            }
                            if (flag) {
                                if (!FurnitureAndOtherItemDuplicateRoom) {
                                    callFurniture();
                                } else if (!AddAppliancesDuplicateRoom) {
                                    callAppliances();
                                } else if (!AddSanitaryWareDuplicateRoom) {
                                    callSanitry();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_outsidespace Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + "PROPERTY_TYPE_ID_CHECKIN" +
                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0") + "PROPERTY_TYPE_ID_CHECKOUT" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));

                Map<String, String> params = new HashMap<String, String>();
                params.put("propertytypecheckinid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", "0"));
                params.put("propertytypecheckoutid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", "0"));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

   /* public void copyRoomDetails(final String apicall, final String Room_ID, final String title, final String generalDesc, final String additionalComment,
                                final String image1, final String image2, final String image3, final String image4, final String propertyTypeId,
                                final int finalIndex, final int finalFlag, final String header) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + apicall;

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insertstard_room_desc", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONObject dataOb = new JSONObject(response.toString());

                            JSONArray msgArray = dataOb.getJSONArray("Message");
                            JSONObject msgOb = msgArray.getJSONObject(0);
                            if (msgOb.getBoolean("result")) {

                                Toast.makeText(RoomConstructionListActivity.this, "Your data copied successfully",
                                        Toast.LENGTH_LONG).show();
                                if (title.equalsIgnoreCase("banisters")) {
                                    BanistersDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("frame")) {
                                    FrameDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("door")) {
                                    DoorDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("ceiling")) {
                                    CeilingDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("light_fitting")) {
                                    LightFittingsDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("walls")) {
                                    WallsDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("fittings")) {
                                    FittingsDuplicateRoom = false;
                                } else if (title.equalsIgnoreCase("switch_sockets")) {
                                    SwitchesAndSocketsDuplicateRoom = false;
                                }
                                callNextPage(apicall, finalIndex, finalFlag, title, header);

                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "" + msgOb.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("insertstandard_room_desc Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                String strroomid = "", strtitle = "title", strimageupload1 = "imageupload1", strimage1 = "image1",
                        strimageupload2 = "imageupload2", strimage2 = "image2",
                        strimageupload3 = "imageupload3", strimage3 = "image3",
                        strimageupload4 = "imageupload4", strimage4 = "image4";
                if (apicall.equalsIgnoreCase("insertstandard_room_furniture")) {
                    strroomid = "standardroomid";
                    strtitle = "furniture_title";
                    strimageupload1 = "furn_imageupload1";
                    strimage1 = "furn_image1";
                    strimageupload2 = "furn_imageupload2";
                    strimage2 = "furn_image2";
                    strimageupload3 = "furn_imageupload3";
                    strimage3 = "furn_image3";
                    strimageupload4 = "furn_imageupload4";
                    strimage4 = "furn_image4";
                } else if (apicall.equalsIgnoreCase("insertkitchen_room_furniture")) {
                    strroomid = "kitchenroomid";
                    strtitle = "furniture_title";
                    strimageupload1 = "furn_imageupload1";
                    strimage1 = "furn_image1";
                    strimageupload2 = "furn_imageupload2";
                    strimage2 = "furn_image2";
                    strimageupload3 = "furn_imageupload3";
                    strimage3 = "furn_image3";
                    strimageupload4 = "furn_imageupload4";
                    strimage4 = "furn_image4";
                } else if (apicall.equalsIgnoreCase("insertbathroom_furniture")) {
                    strroomid = "bathroomid";
                    strtitle = "furniture_title";
                    strimageupload1 = "furn_imageupload1";
                    strimage1 = "furn_image1";
                    strimageupload2 = "furn_imageupload2";
                    strimage2 = "furn_image2";
                    strimageupload3 = "furn_imageupload3";
                    strimage3 = "furn_image3";
                    strimageupload4 = "furn_imageupload4";
                    strimage4 = "furn_image4";
                } else if (apicall.equalsIgnoreCase("insertstairs_landing_furniture")) {
                    strroomid = "stairslandingid";
                    strtitle = "furniture_title";
                    strimageupload1 = "furn_imageupload1";
                    strimage1 = "furn_image1";
                    strimageupload2 = "furn_imageupload2";
                    strimage2 = "furn_image2";
                    strimageupload3 = "furn_imageupload3";
                    strimage3 = "furn_image3";
                    strimageupload4 = "furn_imageupload4";
                    strimage4 = "furn_image4";
                } else if (apicall.equalsIgnoreCase("insertstandard_room_desc")) {
                    strroomid = "standardroomid";
                } else if (apicall.equalsIgnoreCase("insertkitchen_room_desc")) {
                    strroomid = "kitchenroomid";
                } else if (apicall.equalsIgnoreCase("insertbathroom_desc")) {
                    strroomid = "bathroomid";
                } else if (apicall.equalsIgnoreCase("insertstairs_landing_desc")) {
                    strroomid = "stairslandingid";
                } else if (apicall.equalsIgnoreCase("insertoutside_space_desc")) {
                    strroomid = "outsidespaceid";
                } else if (apicall.equalsIgnoreCase("insertkitchen_room_appliances")) {
                    strroomid = "kitchenroomid";
                    strtitle = "appliances_title";
                    strimageupload1 = "app_imageupload1";
                    strimage1 = "app_image1";
                    strimageupload2 = "app_imageupload2";
                    strimage2 = "app_image2";
                    strimageupload3 = "app_imageupload3";
                    strimage3 = "app_image3";
                    strimageupload4 = "app_imageupload4";
                    strimage4 = "app_image4";
                } else if (apicall.equalsIgnoreCase("insertbathroom_sanitary")) {
                    strroomid = "bathroomid";
                    strtitle = "sanitary_title";
                    strimageupload1 = "app_imageupload1";
                    strimage1 = "app_image1";
                    strimageupload2 = "app_imageupload2";
                    strimage2 = "app_image2";
                    strimageupload3 = "app_imageupload3";
                    strimage3 = "app_image3";
                    strimageupload4 = "app_imageupload4";
                    strimage4 = "app_image4";
                }
                params.put("propertytypeid", "" + propertyTypeId);
                params.put("general_desc", "" + generalDesc);
                params.put("additional_comm", "" + additionalComment);
                params.put(strroomid, "" + Room_ID);
                params.put(strtitle, "" + title);
                params.put(strimage1, "");
                params.put(strimage2, "");
                params.put(strimage3, "");
                params.put(strimage4, "");
                params.put(strimageupload1, "");
                params.put(strimageupload2, "");
                params.put(strimageupload3, "");
                params.put(strimageupload4, "");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }*/

    public void setButtonData(final String title, final String header, final String MATCHING_ROOM_ID, final boolean buttonDuplicateStatus) {
        String apicall = "";
        if (call.equalsIgnoreCase("STANDARD")) {
            apicall = "insertstandard_room_desc";
        } else if (call.equalsIgnoreCase("KITCHEN")) {
            apicall = "insertkitchen_room_desc";
        } else if (call.equalsIgnoreCase("BATHROOM")) {
            apicall = "insertbathroom_desc";
        } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
            apicall = "insertstairs_landing_desc";
        } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
            apicall = "insertoutside_space_desc";
        }
        int index = 0, flag = 0, previousindex = 0, previousflag = 0;
        String MATCHID = "", DESC_ID = "", PREVIOUS_DESC_ID = "";
        for (int j = 0; j < ROOM_DESC.length(); j++) {
            try {
                JSONObject jsob = ROOM_DESC.getJSONObject(j);

                if (call.equalsIgnoreCase("STANDARD")) {
                    MATCHID = jsob.getString("sid");
                } else if (call.equalsIgnoreCase("KITCHEN")) {
                    MATCHID = jsob.getString("kid");
                } else if (call.equalsIgnoreCase("BATHROOM")) {
                    MATCHID = jsob.getString("bid");
                } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                    MATCHID = jsob.getString("slid");
                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                    MATCHID = jsob.getString("osid");
                }
                if (jsob.getString("title").equalsIgnoreCase(title) && MATCHING_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                    flag++;
                    index = j;
                    DESC_ID = jsob.getString("id");
                }
                if (jsob.getString("title").equalsIgnoreCase(title) && PREVIOUS_ROOM_ID.equalsIgnoreCase(MATCHID)) {
                    previousflag++;
                    previousindex = j;
                    PREVIOUS_DESC_ID = jsob.getString("id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (DUPLICATE_ROOM.equalsIgnoreCase("YES") && buttonDuplicateStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RoomConstructionListActivity.this);
            builder.setCancelable(false);

            if (call.equalsIgnoreCase("STANDARD")) {
                builder.setMessage("Do you want to duplicate previous Room data?");
            } else if (call.equalsIgnoreCase("KITCHEN")) {
                builder.setMessage("Do you want to duplicate previous Kitchen data?");
            } else if (call.equalsIgnoreCase("BATHROOM")) {
                builder.setMessage("Do you want to duplicate previous Bathroom data?");
            } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
            } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                builder.setMessage("Do you want to duplicate previous Outside space data?");
            }

//                    final int finalPreviousflag = previousflag;
//                    final int finalPreviousindex = previousindex;
            final int finalFlag = flag;
            final int finalIndex = index;
            final String finalApicall = apicall;
            final String finalDESC_ID = DESC_ID;
            final String finalPREVIOUS_DESC_ID = PREVIOUS_DESC_ID;
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
                            PREVIOUS_ROOM_ID, Room_id, finalPREVIOUS_DESC_ID, title, header);
                    dialog.dismiss();
                }
            });


            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (title.equalsIgnoreCase("banisters")) {
                        BanistersDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("frame")) {
                        FrameDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("door")) {
                        DoorDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("ceiling")) {
                        CeilingDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("light_fitting")) {
                        LightFittingsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("walls")) {
                        WallsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("fittings")) {
                        FittingsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("switch_sockets")) {
                        SwitchesAndSocketsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("window")) {
                        WindowsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("window_dressing")) {
                        WindowDressingsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("cupboard")) {
                        CupboardDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("skirting")) {
                        SkirtingsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("heating")) {
                        HeatingDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("kitchen")) {
                        KitchenDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("worksurface")) {
                        WorkSurfaceDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("sink")) {
                        SinkDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("walltiling")) {
                        WallTilingDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("flooring")) {
                        FlooringDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("front_of_property")) {
                        FrontOfPropertyDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("side_of_property")) {
                        SideOfPropertyDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("rear_of_property")) {
                        RearOfPropertyDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("out_buildings")) {
                        OutBuildingsDuplicateRoom = false;
                    } else if (title.equalsIgnoreCase("garage")) {
                        GarageDuplicateRoom = false;
                    } else if (title.isEmpty() && header.equalsIgnoreCase("Furniture")) {
                        FurnitureAndOtherItemDuplicateRoom = false;
                    } else if (title.isEmpty() && header.equalsIgnoreCase("Appliances")) {
                        AddAppliancesDuplicateRoom = false;
                    } else if (title.isEmpty() && header.equalsIgnoreCase("Sanitary")) {
                        AddSanitaryWareDuplicateRoom = false;
                    }
                    callNextPage(finalApicall, finalIndex, finalFlag, title, header, buttonDuplicateStatus);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            callNextPage(apicall, index, flag, title, header, buttonDuplicateStatus);
        }
    }

    public void duplicatePreviousPropertyData(final String propertyTypeId, final String previousStandardRoomId, final String copyStandardRoomId,
                                              final String descID, final String title, final String header) {
        String tag_json_obj = "json_obj_req";
        String url = "";
        if (API_CALL.equalsIgnoreCase("insertstandard_room")) {
            url = ApplicationData.serviceURL + "insert_multistandardroomdesc";
        } else if (API_CALL.equalsIgnoreCase("insertkitchen_room")) {
            url = ApplicationData.serviceURL + "insert_multikitchenroomdesc";
        } else if (API_CALL.equalsIgnoreCase("insertbathroom")) {
            url = ApplicationData.serviceURL + "insert_multibathroomdesc";
        } else if (API_CALL.equalsIgnoreCase("insertstairs_landing")) {
            url = ApplicationData.serviceURL + "insert_multistairsandladingdesc";
        } else if (API_CALL.equalsIgnoreCase("insertoutside_space")) {
            url = ApplicationData.serviceURL + "insert_multioutsidespacedesc";
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insert_room", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArray = new JSONArray(response.toString());
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            if (dataObject.getBoolean("result")) {

                                Toast.makeText(RoomConstructionListActivity.this, "Your data copied successfully",
                                        Toast.LENGTH_LONG).show();
                                if (title.equalsIgnoreCase("banisters")) {
                                    BanistersDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("frame")) {
                                    FrameDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("door")) {
                                    DoorDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("ceiling")) {
                                    CeilingDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("light_fitting")) {
                                    LightFittingsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("walls")) {
                                    WallsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("fittings")) {
                                    FittingsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("switch_sockets")) {
                                    SwitchesAndSocketsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("window")) {
                                    WindowsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("window_dressing")) {
                                    WindowDressingsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("cupboard")) {
                                    CupboardDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("skirting")) {
                                    SkirtingsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("heating")) {
                                    HeatingDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("kitchen")) {
                                    KitchenDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("worksurface")) {
                                    WorkSurfaceDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("sink")) {
                                    SinkDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("walltiling")) {
                                    WallTilingDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("flooring")) {
                                    FlooringDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("front_of_property")) {
                                    FrontOfPropertyDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("side_of_property")) {
                                    SideOfPropertyDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("rear_of_property")) {
                                    RearOfPropertyDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("out_buildings")) {
                                    OutBuildingsDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.equalsIgnoreCase("garage")) {
                                    GarageDuplicateRoom = false;
                                    setButtonData(title, header, PREVIOUS_ROOM_ID, false);
                                } else if (title.isEmpty() && header.equalsIgnoreCase("Furniture")) {
                                    FurnitureAndOtherItemDuplicateRoom = false;
                                    if (call.equalsIgnoreCase("STANDARD")) {
                                        getStandardRoomList(true);
                                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                                        getKitchenRoomList(true);
                                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                                        getBathRoomList(true);
                                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                                        getStairsAndListing(true);
                                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                                        getOutsideSpaceListing(true);
                                    }

                                } else if (title.isEmpty() && header.equalsIgnoreCase("Appliances")) {
                                    AddAppliancesDuplicateRoom = false;
                                    if (call.equalsIgnoreCase("STANDARD")) {
                                        getStandardRoomList(true);
                                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                                        getKitchenRoomList(true);
                                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                                        getBathRoomList(true);
                                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                                        getStairsAndListing(true);
                                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                                        getOutsideSpaceListing(true);
                                    }
                                } else if (title.isEmpty() && header.equalsIgnoreCase("Sanitary")) {
                                    AddSanitaryWareDuplicateRoom = false;
                                    if (call.equalsIgnoreCase("STANDARD")) {
                                        getStandardRoomList(true);
                                    } else if (call.equalsIgnoreCase("KITCHEN")) {
                                        getKitchenRoomList(true);
                                    } else if (call.equalsIgnoreCase("BATHROOM")) {
                                        getBathRoomList(true);
                                    } else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
                                        getStairsAndListing(true);
                                    } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
                                        getOutsideSpaceListing(true);
                                    }
                                }

                               /* Intent i = new Intent(RoomConstructionListActivity.this, RoomConstructionListActivity.class);
                                i.putExtra("DUPLICATE", "YES");
                                i.putExtra("CALL", call);
                                i.putExtra("HEADER", header);
                                i.putExtra("ROOM_DESC", ROOM_DESC);
                                i.putExtra("ROOM_FURNITURE", ROOM_FURNITURE);
                                i.putExtra("ROOM_APPLIANCES", ROOM_APPLIANCES);
                                i.putExtra("ROOM_SANITARY", ROOM_SAINTARY);
                                i.putExtra("ROOM_ID", "" + copyStandardRoomId);
                                startActivity(i);*/

                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "" + dataObject.getString("msg"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("insert_room Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(RoomConstructionListActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomConstructionListActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("propertytypeid", "" + propertyTypeId);
                if (API_CALL.equalsIgnoreCase("insertstandard_room")) {
                    params.put("usestandardroomid", "" + previousStandardRoomId);
                    params.put("insertstandardroomid", "" + copyStandardRoomId);
                } else if (API_CALL.equalsIgnoreCase("insertkitchen_room")) {
                    params.put("usekitchenroomid", "" + previousStandardRoomId);
                    params.put("insertkitchenroomid", "" + copyStandardRoomId);
                } else if (API_CALL.equalsIgnoreCase("insertbathroom")) {
                    params.put("usebathroomid", "" + previousStandardRoomId);
                    params.put("insertbathroomid", "" + copyStandardRoomId);
                } else if (API_CALL.equalsIgnoreCase("insertstairs_landing")) {
                    params.put("usestairsandladingid", "" + previousStandardRoomId);
                    params.put("insertstairsandlading", "" + copyStandardRoomId);
                } else if (API_CALL.equalsIgnoreCase("insertoutside_space")) {
                    params.put("useoutsidespaceid", "" + previousStandardRoomId);
                    params.put("insertoutsidespace", "" + copyStandardRoomId);
                }
                params.put("usedescid", "" + descID);
                if (header.equalsIgnoreCase("Furniture")) {
                    params.put("typevalue", "furniture");
                } else if (header.equalsIgnoreCase("Appliances")) {
                    params.put("typevalue", "appliance");
                } else if (header.equalsIgnoreCase("Sanitary")) {
                    params.put("typevalue", "sanitary");
                } else {
                    params.put("typevalue", "description");
                }
                Log.e("duplicateAPI PARAM", "" + params.toString());//else if (call.equalsIgnoreCase("STAIRS_LANDING")) {
//                    params.put("insertstandardroomid", "" + copyStandardRoomId);
//                } else if (call.equalsIgnoreCase("OUTSIDE_SPACES")) {
//                    params.put("insertstandardroomid", "" + copyStandardRoomId);
//                }
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}

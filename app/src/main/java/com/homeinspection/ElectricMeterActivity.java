package com.homeinspection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElectricMeterActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    static String selectedImage1 = "", selectedImage2 = "", selectedImage3 = "", selectedImage4 = "",
            getImage1 = "", getImage2 = "", getImage3 = "", getImage4 = "";
    static String imagecall = "", CALL = "";
    static String ELECTRIC_EDIT_ID = "";
    static String chkCheck = "";
    FrameLayout flBack;
    Button btnDone;
    EditText edtSerialNo, edtLocation, edtReading, edtReading1, edtReading2, edtReading3;
    ImageView image1, image2, image3, image4;
    RadioButton rdbcheckIn, rdbcheckOut;
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
        setContentView(R.layout.activity_electric_meter);

        flBack = (FrameLayout) findViewById(R.id.flBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        edtSerialNo = (EditText) findViewById(R.id.edtSerialNo);
        edtLocation = (EditText) findViewById(R.id.edtLocation);
        edtReading = (EditText) findViewById(R.id.edtReading);
        edtReading1 = (EditText) findViewById(R.id.edtReading1);
        edtReading2 = (EditText) findViewById(R.id.edtReading2);
        edtReading3 = (EditText) findViewById(R.id.edtReading3);
        rdbcheckIn = (RadioButton) findViewById(R.id.checkIn);
        rdbcheckOut = (RadioButton) findViewById(R.id.checkOut);
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnDone.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtSerialNo.getText().toString().isEmpty()) {
                            Toast.makeText(ElectricMeterActivity.this, "Please enter serial no", Toast.LENGTH_LONG).show();
                        } else if (edtLocation.getText().toString().isEmpty()) {
                            Toast.makeText(ElectricMeterActivity.this, "Please enter location", Toast.LENGTH_LONG).show();
                        } else if (edtReading.getText().toString().isEmpty()) {
                            Toast.makeText(ElectricMeterActivity.this, "Please enter reading", Toast.LENGTH_LONG).show();
                        } else {
                            if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase(chkCheck)) {
                                insertElectricMeter();
                            } else {
                                Toast.makeText(ElectricMeterActivity.this, "Sorry! You have started " +
                                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + " process", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IMAGE1";

                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        ElectricMeterActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IMAGE2";
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        ElectricMeterActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IMAGE3";
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        ElectricMeterActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IMAGE4";
                final CharSequence[] options = {"Take Photo",
                        "Choose from Gallery", "Cancel"};

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                        ElectricMeterActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    startCamera();
                                } else if (options[item]
                                        .equals("Choose from Gallery")) {
                                    startGallery();
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                builder.show();
            }
        });
        rdbcheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkCheck = "checkin";
                getElectricMeterCheckIn();
            }
        });
        rdbcheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkCheck = "checkout";
                getElectricMeterCheckOut();
            }
        });

        if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
            chkCheck = "checkin";
            getElectricMeterCheckIn();
            rdbcheckIn.setChecked(true);
        } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
            chkCheck = "checkout";
            getElectricMeterCheckOut();
            rdbcheckOut.setChecked(true);
        }
    }

    private void startCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFile(1);
        if (mFileUri != null) {
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent1, REQUEST_CAMERA);
        } else {
            Log.e("image camera", "file not available");
        }
    }

    private void startGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
    }

    public Uri getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "HomeInspection");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("survey form second", "could not create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File mediaFile;
        if (type == 1) {
            String imageStoragePath = mediaStorageDir + "/Images/";
            createDirectory(imageStoragePath);
            mediaFile = new File(imageStoragePath + "IMG" + timeStamp + ".jpg");

        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    public String getPath(Uri uri, boolean isImage) {
        if (uri == null) {
            return null;
        }
        String[] projection;
        String coloumnName, selection;
        if (isImage) {
            selection = MediaStore.Images.Media._ID + "=?";
            coloumnName = MediaStore.Images.Media.DATA;
        } else {
            selection = MediaStore.Video.Media._ID + "=?";
            coloumnName = MediaStore.Video.Media.DATA;
        }
        projection = new String[]{coloumnName};
        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            if (isImage) {
                cursor = getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                                new String[]{id}, null);
            } else {
                cursor = getContentResolver()
                        .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection, new String[]{id},
                                null);
            }
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor.getColumnIndex(coloumnName);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                String selectedImage = "";
                if (mFileUri != null) {
                    Log.d("upload image", "file: " + mFileUri);
                    selectedImage = getRealPathFromURI(mFileUri);
                } else {
                    if (data != null) {
                        try {
                            selectedImage = getPath(data.getData(), true);
                        } catch (Exception e) {
                            selectedImage = getRealPathFromURI(data.getData());
                        }
                    }
                }
                if (imagecall.equalsIgnoreCase("IMAGE1")) {
                    selectedImage1 = selectedImage;
                    image1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IMAGE2")) {
                    selectedImage2 = selectedImage;
                    image2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IMAGE3")) {
                    selectedImage3 = selectedImage;
                    image3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IMAGE4")) {
                    selectedImage4 = selectedImage;
                    image4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                }
                // ivTakePhoto.setImageBitmap(getBitmap(new File(selectedImage)));
            } else if (requestCode == REQUEST_GALLERY) {
                String selectedImage = "";
                Uri selectedImageUri = data.getData();
                Log.e("Image path: ", selectedImageUri.toString());
                selectedImage = getPath(ElectricMeterActivity.this, selectedImageUri);
                Log.e("Image path:", selectedImage);
                System.out.println("Image Path : " + selectedImage);

                Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                // Bitmap conv_bm = getRoundedRectBitmap(resized, 200);

                if (data != null && data.getData() != null) {
//                    try {
//                        selectedImage = getPath(data.getData(), true);
//                    } catch (Exception e) {
//                        selectedImage = getRealPathFromURI(data.getData());
//                    }
                    if (imagecall.equalsIgnoreCase("IMAGE1")) {
                        selectedImage1 = selectedImage;
                        image1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IMAGE2")) {
                        selectedImage2 = selectedImage;
                        image2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IMAGE3")) {
                        selectedImage3 = selectedImage;
                        image3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IMAGE4")) {
                        selectedImage4 = selectedImage;
                        image4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    }
                    //  ivTakePhoto.setImageBitmap(getBitmap(new File(selectedImage)));
                }

            }
        }

    }

    private Bitmap getThumbnailBitmap(final String path, final int thumbnailSize) {
        Bitmap bitmap;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            bitmap = null;
        }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }

    public Bitmap getBitmap(File file) {

        Bitmap bitmap = null;
        if (file != null) {
            String filePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(filePath, options);

        }
        return bitmap;
    }

    public void getElectricMeterCheckIn() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_electricmeter";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_electricmeter", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArr = new JSONArray(response.toString());
                            JSONObject dataOb = dataArr.getJSONObject(0);

                            if (dataOb.getBoolean("result")) {
                                JSONArray meterArray = dataOb.getJSONArray("electricmeter");
                                JSONObject meterOb = meterArray.getJSONObject(0);

                                edtSerialNo.setText(meterOb.getString("serialno"));
                                edtLocation.setText(meterOb.getString("location"));
                                edtReading.setText(meterOb.getString("reading1"));
                                edtReading1.setText(meterOb.getString("reading2"));
                                edtReading2.setText(meterOb.getString("reading3"));
                                edtReading3.setText(meterOb.getString("reading4"));
                                if (!meterOb.getString("meter_image1").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image1"))
                                            .into(image1);
                                } else {
                                    if (!getImage1.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage1)
                                                .into(image1);
                                    } else
                                        image1.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("meter_image2").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image2"))
                                            .into(image2);
                                } else {
                                    if (!getImage2.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage2)
                                                .into(image2);
                                    } else
                                        image2.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("meter_image3").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image3"))
                                            .into(image3);
                                } else {
                                    if (!getImage3.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage3)
                                                .into(image3);
                                    } else
                                        image3.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("mater_image4").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("mater_image4"))
                                            .into(image4);
                                } else {
                                    if (!getImage4.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage4)
                                                .into(image4);
                                    } else
                                        image4.setImageResource(R.drawable.ic_camera);
                                }
                                CALL = "EDIT";
                                getImage1 = meterOb.getString("meter_image1");
                                getImage2 = meterOb.getString("meter_image2");
                                getImage3 = meterOb.getString("meter_image3");
                                getImage4 = meterOb.getString("mater_image4");
                                ELECTRIC_EDIT_ID = meterOb.getString("id");
                            } else {
                                Toast.makeText(ElectricMeterActivity.this, "Check in history not available", Toast.LENGTH_LONG).show();
//                                edtSerialNo.setText("");
//                                edtLocation.setText("");
//                                edtReading.setText("");
//                                edtReading1.setText("");
//                                edtReading2.setText("");
//                                edtReading3.setText("");
                                ELECTRIC_EDIT_ID = "";
//                                getImage1 = "";
//                                getImage2 = "";
//                                getImage3 = "";
//                                getImage4 = "";
//                                CALL = "NEW";
//                                image1.setImageResource(R.drawable.ic_camera);
//                                image2.setImageResource(R.drawable.ic_camera);
//                                image3.setImageResource(R.drawable.ic_camera);
//                                image4.setImageResource(R.drawable.ic_camera);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ElectricMeterActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ElectricMeterActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_electricmeter Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ElectricMeterActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ElectricMeterActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
//                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
//                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", ""));
                Map<String, String> params = new HashMap<String, String>();
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void getElectricMeterCheckOut() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "list_electricmeter";

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("list_electricmeter", response.toString());
                        try {
                            mProgressDialog.dismiss();
                            JSONArray dataArr = new JSONArray(response.toString());
                            JSONObject dataOb = dataArr.getJSONObject(0);

                            if (dataOb.getBoolean("result")) {
                                JSONArray meterArray = dataOb.getJSONArray("electricmeter");
                                JSONObject meterOb = meterArray.getJSONObject(0);
                                edtSerialNo.setText(meterOb.getString("serialno"));
                                edtLocation.setText(meterOb.getString("location"));
                                edtReading.setText(meterOb.getString("reading1"));
                                edtReading1.setText(meterOb.getString("reading2"));
                                edtReading2.setText(meterOb.getString("reading3"));
                                edtReading3.setText(meterOb.getString("reading4"));
                                if (!meterOb.getString("meter_image1").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image1"))
                                            .into(image1);
                                } else {
                                    if (!getImage1.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage1)
                                                .into(image1);
                                    } else
                                        image1.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("meter_image2").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image2"))
                                            .into(image2);
                                } else {
                                    if (!getImage2.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage2)
                                                .into(image2);
                                    } else
                                        image2.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("meter_image3").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("meter_image3"))
                                            .into(image3);
                                } else {
                                    if (!getImage3.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage3)
                                                .into(image3);
                                    } else
                                        image3.setImageResource(R.drawable.ic_camera);
                                }
                                if (!meterOb.getString("mater_image4").isEmpty()) {
                                    Picasso.with(ElectricMeterActivity.this)
                                            .load(meterOb.getString("mater_image4"))
                                            .into(image4);
                                } else {
                                    if (!getImage4.isEmpty()) {
                                        Picasso.with(ElectricMeterActivity.this)
                                                .load(getImage4)
                                                .into(image4);
                                    } else
                                        image4.setImageResource(R.drawable.ic_camera);
                                }
                                CALL = "EDIT";
                                getImage1 = meterOb.getString("meter_image1");
                                getImage2 = meterOb.getString("meter_image2");
                                getImage3 = meterOb.getString("meter_image3");
                                getImage4 = meterOb.getString("mater_image4");
                                ELECTRIC_EDIT_ID = meterOb.getString("id");
                            } else {
                                Toast.makeText(ElectricMeterActivity.this, "Check out history not available", Toast.LENGTH_LONG).show();
//                                edtSerialNo.setText("");
//                                edtLocation.setText("");
//                                edtReading.setText("");
//                                edtReading1.setText("");
//                                edtReading2.setText("");
//                                edtReading3.setText("");
                                ELECTRIC_EDIT_ID = "";
//                                getImage1 = "";
//                                getImage2 = "";
//                                getImage3 = "";
//                                getImage4 = "";
//                                CALL = "NEW";
//                                image1.setImageResource(R.drawable.ic_camera);
//                                image2.setImageResource(R.drawable.ic_camera);
//                                image3.setImageResource(R.drawable.ic_camera);
//                                image4.setImageResource(R.drawable.ic_camera);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ElectricMeterActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ElectricMeterActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("list_electricmeter Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ElectricMeterActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ElectricMeterActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
//                Log.e("Property details", "Property_id:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_ID", "") +
//                        "PROPERTY_TYPE_ID:" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "") + ",PROPERTY_TYPE:" +
//                        getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", ""));


                Map<String, String> params = new HashMap<String, String>();
                params.put("propertyid", "" + getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void insertElectricMeter() {
        final String serialno = edtSerialNo.getText().toString();
        final String location = edtLocation.getText().toString();
        final String reading = edtReading.getText().toString();
        final String reading1 = edtReading1.getText().toString();
        final String reading2 = edtReading2.getText().toString();
        final String reading3 = edtReading3.getText().toString();
        final boolean rdbcheckin = rdbcheckIn.isChecked();
        boolean rdbcheckout = rdbcheckOut.isChecked();
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "insertelectric");

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (ELECTRIC_EDIT_ID.isEmpty()) {
                        entity.addPart("electricid", new StringBody("0"));
                    } else {
                        entity.addPart("electricid", new StringBody(ELECTRIC_EDIT_ID));
                    }
                    if (!selectedImage1.isEmpty()) {
                        Bitmap bmp = getThumbnailBitmap(selectedImage1, 200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                        InputStream in = new ByteArrayInputStream(bos.toByteArray());
                        ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage1.substring(selectedImage1.lastIndexOf('/') + 1, selectedImage1.lastIndexOf('.')));

                        entity.addPart("meter_imageupload1", foto);
                        entity.addPart("meter_image1", new StringBody(""));
                    } else {
                        entity.addPart("meter_imageupload1", new StringBody(""));
                        entity.addPart("meter_image1", new StringBody(getImage1));
                    }
                    if (!selectedImage2.isEmpty()) {
                        Bitmap bmp = getThumbnailBitmap(selectedImage2, 200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                        InputStream in = new ByteArrayInputStream(bos.toByteArray());
                        ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage2.substring(selectedImage2.lastIndexOf('/') + 1, selectedImage2.lastIndexOf('.')));

                        entity.addPart("meter_imageupload2", foto);
                        entity.addPart("meter_image2", new StringBody(""));
                    } else {
                        entity.addPart("meter_imageupload2", new StringBody(""));
                        entity.addPart("meter_image2", new StringBody(getImage2));
                    }
                    if (!selectedImage3.isEmpty()) {
                        Bitmap bmp = getThumbnailBitmap(selectedImage3, 200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                        InputStream in = new ByteArrayInputStream(bos.toByteArray());
                        ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage3.substring(selectedImage3.lastIndexOf('/') + 1, selectedImage3.lastIndexOf('.')));

                        entity.addPart("meter_imageupload3", foto);
                        entity.addPart("meter_image3", new StringBody(""));
                    } else {
                        entity.addPart("meter_imageupload3", new StringBody(""));
                        entity.addPart("meter_image3", new StringBody(getImage3));
                    }
                    if (!selectedImage4.isEmpty()) {
                        Bitmap bmp = getThumbnailBitmap(selectedImage4, 200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                        InputStream in = new ByteArrayInputStream(bos.toByteArray());
                        ContentBody foto = new InputStreamBody(in, "image/jpeg", selectedImage4.substring(selectedImage4.lastIndexOf('/') + 1, selectedImage4.lastIndexOf('.')));

                        entity.addPart("meter_imageupload4", foto);
                        entity.addPart("meter_image4", new StringBody(""));
                    } else {
                        entity.addPart("meter_imageupload4", new StringBody(""));
                        entity.addPart("meter_image4", new StringBody(getImage4));
                    }
                    entity.addPart("propertytypeid", new StringBody(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", "")));
                    entity.addPart("type", new StringBody("ELECTRIC_METER"));
                    entity.addPart("serialno", new StringBody(serialno));
                    entity.addPart("location", new StringBody(location));
                    entity.addPart("reading1", new StringBody(reading));
                    entity.addPart("reading2", new StringBody(reading1));
                    entity.addPart("reading3", new StringBody(reading2));
                    entity.addPart("reading4", new StringBody(reading3));
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();
                    Log.e("add electric meter data", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    Log.e("add electric meter exce", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Log.e("add electric meter", result.toString());
                try {
                    mProgressDialog.dismiss();
                    JSONObject object = new JSONObject(result.toString());
                    JSONArray msgArray = object.getJSONArray("Message");
                    JSONObject msgOb = msgArray.getJSONObject(0);
                    if (msgOb.getBoolean("result")) {

                        Toast.makeText(ElectricMeterActivity.this, "Record saved successfully",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ElectricMeterActivity.this, "" + msgOb.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                        Toast.makeText(ElectricMeterActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ElectricMeterActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(ElectricMeterActivity.this);
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();
    }

}

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class RoomOrderCheckInOutActivity extends AppCompatActivity {
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    FrameLayout flBack;
    Button btnCheckInDone, btnCheckOutDone;
    TextView txvHeader;
    String call = "", header = "", PREVIOUS_ROOM_ID = "", ROOM_ID = "", ROOM_DESC = "[]", ROOM_FURNITURE = "[]", ROOM_APPLIANCES = "[]", ROOM_SAINTARY = "[]";
    ImageView imvCheckInImage1, imvCheckInImage2, imvCheckInImage3, imvCheckInImage4,
            imvCheckOutImage1, imvCheckOutImage2, imvCheckOutImage3, imvCheckOutImage4;
    String selectedCheckInImage1 = "", selectedCheckInImage2 = "", selectedCheckInImage3 = "", selectedCheckInImage4 = "",
            selectedCheckOutImage1 = "", selectedCheckOutImage2 = "", selectedCheckOutImage3 = "", selectedCheckOutImage4 = "";
    String imagecall, getImage1 = "", getImage2 = "", getImage3 = "", getImage4 = "", apicall = "";
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
        imvCheckInImage1 = (ImageView) findViewById(R.id.imvCheckInImage1);
        imvCheckInImage2 = (ImageView) findViewById(R.id.imvCheckInImage2);
        imvCheckInImage3 = (ImageView) findViewById(R.id.imvCheckInImage3);
        imvCheckInImage4 = (ImageView) findViewById(R.id.imvCheckInImage4);

        imvCheckOutImage1 = (ImageView) findViewById(R.id.imvCheckOutImage1);
        imvCheckOutImage2 = (ImageView) findViewById(R.id.imvCheckOutImage2);
        imvCheckOutImage3 = (ImageView) findViewById(R.id.imvCheckOutImage3);
        imvCheckOutImage4 = (ImageView) findViewById(R.id.imvCheckOutImage4);

        imvCheckInImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IN_IMAGE1";
                imageCall();

            }
        });
        imvCheckInImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IN_IMAGE2";
                imageCall();

            }
        });
        imvCheckInImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IN_IMAGE3";
                imageCall();

            }
        });
        imvCheckInImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "IN_IMAGE4";
                imageCall();

            }
        });
        imvCheckOutImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "OUT_IMAGE1";
                imageCall();

            }
        });
        imvCheckOutImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "OUT_IMAGE2";
                imageCall();

            }
        });
        imvCheckOutImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "OUT_IMAGE3";
                imageCall();

            }
        });
        imvCheckOutImage4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagecall = "OUT_IMAGE4";
                imageCall();

            }
        });
        btnCheckInDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
                    addNewRoom(header, selectedCheckInImage1, selectedCheckInImage2, selectedCheckInImage3, selectedCheckInImage4,
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKIN", ""));
                } else {
                    Toast.makeText(RoomOrderCheckInOutActivity.this, "Sorry! You have started " +
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + " process", Toast.LENGTH_LONG).show();
                }

            }
        });
        btnCheckOutDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
                    addNewRoom(header, selectedCheckOutImage1, selectedCheckOutImage2, selectedCheckOutImage3, selectedCheckOutImage4,
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID_CHECKOUT", ""));
                } else {
                    Toast.makeText(RoomOrderCheckInOutActivity.this, "Sorry! You have started " +
                            getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "") + " process", Toast.LENGTH_LONG).show();
                }

            }
        });

        Intent i = getIntent();
        txvHeader.setText(i.getStringExtra("HEADER"));
        header = i.getStringExtra("HEADER");
        call = i.getStringExtra("CALL");
        PREVIOUS_ROOM_ID = i.getStringExtra("PREVIOUS_ROOM_ID");
        ROOM_ID = i.getStringExtra("ROOM_ID");
        ROOM_DESC = i.getStringExtra("ROOM_DESC");
        ROOM_FURNITURE = i.getStringExtra("ROOM_FURNITURE");
        ROOM_APPLIANCES = i.getStringExtra("ROOM_APPLIANCES");
        ROOM_SAINTARY = i.getStringExtra("ROOM_SAINTARY");
        getImage1 = i.getStringExtra("IMAGE1");
        getImage2 = i.getStringExtra("IMAGE2");
        getImage3 = i.getStringExtra("IMAGE3");
        getImage4 = i.getStringExtra("IMAGE4");
        apicall = i.getStringExtra("API_CALL");

        if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkin")) {
            if (!getImage1.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage1).resize(500, 500)
                        .into(imvCheckInImage1);
            }
            if (!getImage2.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage2).resize(500, 500)
                        .into(imvCheckInImage2);
            }
            if (!getImage3.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage3).resize(500, 500)
                        .into(imvCheckInImage3);
            }
            if (!getImage4.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage4).resize(500, 500)
                        .into(imvCheckInImage4);
            }

        } else if (getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE", "").equalsIgnoreCase("checkout")) {
            if (!getImage1.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage1).resize(500, 500)
                        .into(imvCheckOutImage1);
            }
            if (!getImage2.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage2).resize(500, 500)
                        .into(imvCheckOutImage2);
            }
            if (!getImage3.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage3).resize(500, 500)
                        .into(imvCheckOutImage3);
            }
            if (!getImage4.isEmpty()) {
                Picasso.with(RoomOrderCheckInOutActivity.this)
                        .load(getImage4).resize(500, 500)
                        .into(imvCheckOutImage4);
            }
        }
    }

    public void imageCall() {
        final CharSequence[] options = {"Take Photo",
                "Choose from Gallery", "Cancel"};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                RoomOrderCheckInOutActivity.this);
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
                if (imagecall.equalsIgnoreCase("IN_IMAGE1")) {
                    selectedCheckInImage1 = selectedImage;
                    imvCheckInImage1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IN_IMAGE2")) {
                    selectedCheckInImage2 = selectedImage;
                    imvCheckInImage2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IN_IMAGE3")) {
                    selectedCheckInImage3 = selectedImage;
                    imvCheckInImage3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("IN_IMAGE4")) {
                    selectedCheckInImage4 = selectedImage;
                    imvCheckInImage4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("OUT_IMAGE1")) {
                    selectedCheckOutImage1 = selectedImage;
                    imvCheckOutImage1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("OUT_IMAGE2")) {
                    selectedCheckOutImage2 = selectedImage;
                    imvCheckOutImage2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("OUT_IMAGE3")) {
                    selectedCheckOutImage3 = selectedImage;
                    imvCheckOutImage3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                } else if (imagecall.equalsIgnoreCase("OUT_IMAGE4")) {
                    selectedCheckOutImage4 = selectedImage;
                    imvCheckOutImage4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                }
                // ivTakePhoto.setImageBitmap(getBitmap(new File(selectedImage)));
            } else if (requestCode == REQUEST_GALLERY) {
                String selectedImage = "";
                Uri selectedImageUri = data.getData();
                Log.e("Image path: ", selectedImageUri.toString());
                selectedImage = getPath(RoomOrderCheckInOutActivity.this, selectedImageUri);
                Log.e("Image path:", selectedImage);
                System.out.println("Image Path : " + selectedImage);

//                Bitmap bitmap = BitmapFactory.decodeFile(selectedImage);
//                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                // Bitmap conv_bm = getRoundedRectBitmap(resized, 200);

                if (data != null && data.getData() != null) {
                    if (imagecall.equalsIgnoreCase("IN_IMAGE1")) {
                        selectedCheckInImage1 = selectedImage;
                        imvCheckInImage1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IN_IMAGE2")) {
                        selectedCheckInImage2 = selectedImage;
                        imvCheckInImage2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IN_IMAGE3")) {
                        selectedCheckInImage3 = selectedImage;
                        imvCheckInImage3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("IN_IMAGE4")) {
                        selectedCheckInImage4 = selectedImage;
                        imvCheckInImage4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("OUT_IMAGE1")) {
                        selectedCheckOutImage1 = selectedImage;
                        imvCheckOutImage1.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("OUT_IMAGE2")) {
                        selectedCheckOutImage2 = selectedImage;
                        imvCheckOutImage2.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("OUT_IMAGE3")) {
                        selectedCheckOutImage3 = selectedImage;
                        imvCheckOutImage3.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
                    } else if (imagecall.equalsIgnoreCase("OUT_IMAGE4")) {
                        selectedCheckOutImage4 = selectedImage;
                        imvCheckOutImage4.setImageBitmap(getThumbnailBitmap(selectedImage, 200));
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

    public ContentBody getContentBodyForUpload(String path) {
        Bitmap bmp = getThumbnailBitmap(path, 200);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());
        ContentBody foto = new InputStreamBody(in, "image/jpeg", path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.')));
        return foto;
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

    public void addNewRoom(final String roomType, final String image1, final String image2, final String image3, final String image4, final String propertyTypeId) {
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + apicall);

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    if (apicall.equalsIgnoreCase("insertstandard_room")) {
                        entity.addPart("standard_roomid", new StringBody(ROOM_ID));
                        entity.addPart("roomtype", new StringBody("" + roomType));
                    } else if (apicall.equalsIgnoreCase("insertkitchen_room")) {
                        entity.addPart("kitchen_roomid", new StringBody(ROOM_ID));
                        entity.addPart("kitchentype", new StringBody("" + roomType));
                    } else if (apicall.equalsIgnoreCase("insertbathroom")) {
                        entity.addPart("bathroomid", new StringBody(ROOM_ID));
                        entity.addPart("bathroomtype", new StringBody("" + roomType));
                    } else if (apicall.equalsIgnoreCase("insertstairs_landing")) {
                        entity.addPart("stairs_landingid", new StringBody(ROOM_ID));
                        entity.addPart("stairslandingtype", new StringBody("" + roomType));
                    } else if (apicall.equalsIgnoreCase("insertoutside_space")) {
                        entity.addPart("outside_spaceid", new StringBody(ROOM_ID));
                        entity.addPart("outsidespacetype", new StringBody("" + roomType));
                    }
                    entity.addPart("propertytypeid", new StringBody("" + propertyTypeId));

                    if (!image1.isEmpty()) {
                        entity.addPart("imageupload1", getContentBodyForUpload(image1));
                        entity.addPart("image1", new StringBody(""));
                    } else {
                        entity.addPart("imageupload1", new StringBody(""));
                        entity.addPart("image1", new StringBody(getImage1));
                    }
                    if (!image2.isEmpty()) {
                        entity.addPart("imageupload2", getContentBodyForUpload(image2));
                        entity.addPart("image2", new StringBody(""));
                    } else {
                        entity.addPart("imageupload2", new StringBody(""));
                        entity.addPart("image2", new StringBody(getImage2));
                    }
                    if (!image3.isEmpty()) {
                        entity.addPart("imageupload3", getContentBodyForUpload(image3));
                        entity.addPart("image3", new StringBody(""));
                    } else {
                        entity.addPart("imageupload3", new StringBody(""));
                        entity.addPart("image3", new StringBody(getImage3));
                    }
                    if (!image4.isEmpty()) {
                        entity.addPart("imageupload4", getContentBodyForUpload(image4));
                        entity.addPart("image4", new StringBody(""));
                    } else {
                        entity.addPart("imageupload4", new StringBody(""));
                        entity.addPart("image4", new StringBody(getImage4));
                    }

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
                    Log.e(apicall + " data", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    Log.e(apicall + " problem", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Log.e(apicall + "", result.toString());
                try {
                    mProgressDialog.dismiss();
                    JSONObject dataOb = new JSONObject(result.toString());

                    JSONArray msgArray = dataOb.getJSONArray("Message");
                    final JSONObject msgOb = msgArray.getJSONObject(0);
                    if (msgOb.getBoolean("result")) {
                        final String room_id = msgOb.getString("id");
                        Toast.makeText(RoomOrderCheckInOutActivity.this, "Your Room saved successfully",
                                Toast.LENGTH_LONG).show();
                        if (ROOM_ID.equalsIgnoreCase("0")&&!PREVIOUS_ROOM_ID.equalsIgnoreCase("0")) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(RoomOrderCheckInOutActivity.this);
//                            builder.setCancelable(false);
//
//                            if (apicall.equalsIgnoreCase("insertstandard_room")) {
//                                builder.setMessage("Do you want to duplicate previous Room data?");
//                            } else if (apicall.equalsIgnoreCase("insertkitchen_room")) {
//                                builder.setMessage("Do you want to duplicate previous Kitchen data?");
//                            } else if (apicall.equalsIgnoreCase("insertbathroom")) {
//                                builder.setMessage("Do you want to duplicate previous Bathroom data?");
//                            } else if (apicall.equalsIgnoreCase("insertstairs_landing")) {
//                                builder.setMessage("Do you want to duplicate previous Stairs & Landing data?");
//                            } else if (apicall.equalsIgnoreCase("insertoutside_space")) {
//                                builder.setMessage("Do you want to duplicate previous Outside space data?");
//                            }
//
//                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    duplicatePreviousPropertyData(getSharedPreferences("LOGIN_DETAIL", 0).getString("PROPERTY_TYPE_ID", ""),
//                                            PREVIOUS_ROOM_ID, room_id);
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(RoomOrderCheckInOutActivity.this, RoomConstructionListActivity.class);
                                    i.putExtra("DUPLICATE", "YES");
                                    i.putExtra("PREVIOUS_ROOM_ID",""+PREVIOUS_ROOM_ID);
                                    i.putExtra("CALL", call);
                            i.putExtra("API_CALL",""+apicall);
                                    i.putExtra("HEADER", header);
                                    i.putExtra("ROOM_DESC", ROOM_DESC);
                                    i.putExtra("ROOM_FURNITURE", ROOM_FURNITURE);
                                    i.putExtra("ROOM_APPLIANCES", ROOM_APPLIANCES);
                                    i.putExtra("ROOM_SANITARY", ROOM_SAINTARY);
                                    i.putExtra("ROOM_ID", "" + room_id);
                                    startActivity(i);
//                                    dialog.dismiss();
//                                }
//                            });
//                            AlertDialog alert = builder.create();
//                            alert.show();
                        }else{
                            Intent i = new Intent(RoomOrderCheckInOutActivity.this, RoomConstructionListActivity.class);
                            i.putExtra("DUPLICATE", "NO");
                            i.putExtra("CALL", call);
                            i.putExtra("API_CALL",""+apicall);
                            i.putExtra("HEADER", header);
                            i.putExtra("ROOM_DESC", ROOM_DESC);
                            i.putExtra("ROOM_FURNITURE", ROOM_FURNITURE);
                            i.putExtra("ROOM_APPLIANCES", ROOM_APPLIANCES);
                            i.putExtra("ROOM_SANITARY", ROOM_SAINTARY);
                            i.putExtra("ROOM_ID", "" + room_id);
                            startActivity(i);
                        }

                    } else {
                        Toast.makeText(RoomOrderCheckInOutActivity.this, "" + msgOb.getString("message"),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                        Toast.makeText(RoomOrderCheckInOutActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RoomOrderCheckInOutActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(RoomOrderCheckInOutActivity.this);
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();


    }

    public void duplicatePreviousPropertyData(final String propertyTypeId, final String previousStandardRoomId, final String copyStandardRoomId) {
        String tag_json_obj = "json_obj_req";
        String url = "";
        if (apicall.equalsIgnoreCase("insertstandard_room")) {
            url = ApplicationData.serviceURL + "insert_multistandardroomdesc";
        } else if (apicall.equalsIgnoreCase("insertkitchen_room")) {
            url = ApplicationData.serviceURL + "insert_multikitchenroomdesc";
        } else if (apicall.equalsIgnoreCase("insertbathroom")) {
            url = ApplicationData.serviceURL + "insert_multibathroomdescc";
        } else if (apicall.equalsIgnoreCase("insertstairs_landing")) {
            url = ApplicationData.serviceURL + "insert_multistairsandladingdesc";
        } else if (apicall.equalsIgnoreCase("insertoutside_space")) {
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

                                Toast.makeText(RoomOrderCheckInOutActivity.this, "Your data copied successfully",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(RoomOrderCheckInOutActivity.this, RoomConstructionListActivity.class);
                                i.putExtra("DUPLICATE", "YES");
                                i.putExtra("CALL", call);
                                i.putExtra("HEADER", header);
                                i.putExtra("ROOM_DESC", ROOM_DESC);
                                i.putExtra("ROOM_FURNITURE", ROOM_FURNITURE);
                                i.putExtra("ROOM_APPLIANCES", ROOM_APPLIANCES);
                                i.putExtra("ROOM_SANITARY", ROOM_SAINTARY);
                                i.putExtra("ROOM_ID", "" + copyStandardRoomId);
                                startActivity(i);

                            } else {
                                Toast.makeText(RoomOrderCheckInOutActivity.this, "" + dataObject.getString("msg"),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(RoomOrderCheckInOutActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RoomOrderCheckInOutActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(RoomOrderCheckInOutActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RoomOrderCheckInOutActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("propertytypeid", "" + propertyTypeId);
                params.put("usestandardroomid", "" + previousStandardRoomId);
                params.put("insertstandardroomid", "" + copyStandardRoomId);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}

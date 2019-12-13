package com.datingapp.android.Accounts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Profile.Profile_F;
import com.datingapp.android.R;
import com.datingapp.android.common.Common;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {


    /** GALLERYアクティビティのリクエストID */
    private static final int GALLERY_ACTIVITY_ID = 2001;

    /** Galleryの許可のリクエストID */
    private final static int GALLERY_PERMISSIONS_RESULT = 103;

    private LinearLayout cameraBtn, galleryBtn;
    private RelativeLayout alertLayout;
    private ImageView closeBtn;
    private ImageView selectedImgView;
    private String selectedImgName = "";
    private SharedPreferences sharedPreferences;
    private String imgPath = "";
    private String imgPath1 = "";
    private String imgPath2 = "";
    private String imgPath3 = "";
    private String imgPath4 = "";
    private String imgPath5 = "";
    private String imgPath6 = "";
    byte[] image_byteArray;
    public IOSDialog iosDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Common.currentActivity = this;

        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        imgPath1 = sharedPreferences.getString(Variables.u_pic1, "null");
        imgPath2 = sharedPreferences.getString(Variables.u_pic2, "null");
        imgPath3 = sharedPreferences.getString(Variables.u_pic3, "null");
        imgPath4 = sharedPreferences.getString(Variables.u_pic4, "null");
        imgPath5 = sharedPreferences.getString(Variables.u_pic5, "null");
        imgPath6 = sharedPreferences.getString(Variables.u_pic6, "null");

        RelativeLayout backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final LinearLayout photoGroup = findViewById(R.id.photoGroupLayout);
        photoGroup.post(new Runnable() {
            @Override
            public void run() {
                int viewWidth = photoGroup.getWidth();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) photoGroup.getLayoutParams();
                params.height = viewWidth;
                photoGroup.setLayoutParams(params);
            }
        });

        cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getcamrapermission();
            }
        });

        galleryBtn = findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getStoragepermission();
            }
        });

        alertLayout = findViewById(R.id.alertLayout);
        alertLayout.setVisibility(View.GONE);

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertLayout.setVisibility(View.GONE);
            }
        });

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        ImageView mainImgView = findViewById(R.id.mainPhoto);
        mainImgView.setOnClickListener(this);
        if (!imgPath1.equals("null")) {
            Picasso.with(this).load(imgPath1)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(mainImgView);
        }

        ImageView subImgView1 = findViewById(R.id.subPhoto1);
        subImgView1.setOnClickListener(this);
        if (!imgPath2.equals("null")) {
            Picasso.with(this).load(imgPath2)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView1);
        }

        ImageView subImgView2 = findViewById(R.id.subPhoto2);
        subImgView2.setOnClickListener(this);
        if (!imgPath3.equals("null")) {
            Picasso.with(this).load(imgPath3)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView2);
        }

        ImageView subImgView3 = findViewById(R.id.subPhoto3);
        subImgView3.setOnClickListener(this);
        if (!imgPath4.equals("null")) {
            Picasso.with(this).load(imgPath4)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView3);
        }

        ImageView subImgView4 = findViewById(R.id.subPhoto4);
        subImgView4.setOnClickListener(this);
        if (!imgPath5.equals("null")) {
            Picasso.with(this).load(imgPath5)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView4);
        }

        ImageView subImgView5 = findViewById(R.id.subPhoto5);
        subImgView5.setOnClickListener(this);
        if (!imgPath6.equals("null")) {
            Picasso.with(this).load(imgPath6)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView5);
        }

        RelativeLayout nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Common.currentActivity = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getcamrapermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    786);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getStoragepermission(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, 2);
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        787 );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 786) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }

            } else {
            }
        }

        if (requestCode == 787) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, 2);
            }
        }
    }

    //on image select activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
//                //path from full size image
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.ImageColumns.ORIENTATION
                        },
                        MediaStore.Images.Media.DATE_ADDED,
                        null,
                        "date_added DESC");

                Bitmap fullsize = null;
                if (cursor != null && cursor.moveToFirst()) {
                    Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    String photoPath = uri.toString();
                    cursor.close();
                    if (photoPath != null) {
                        System.out.println("path: "+photoPath); //path from image full size
                        fullsize = decodeSampledBitmap(photoPath);//here is the bitmap of image full size
                    }
                }
                setImageByteData(fullsize);
                alertLayout.setVisibility(View.GONE);
//                Uri selectedImage = data.getData();
//                beginCrop(selectedImage);
            }

            else if (requestCode == 2) {

                Uri selectedImage = data.getData();

//                handleCrop(resultCode, data);
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                setImageByteData(imagebitmap);
                alertLayout.setVisibility(View.GONE);
            }

        }

    }

    private void setImageByteData(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        selectedImgView.setImageBitmap(bitmap);
        alertLayout.setVisibility(View.GONE);
        image_byteArray = baos.toByteArray();
        iosDialog.show();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        String id=reference.push().getKey();
        // first we upload image after upload then get the picture url and save the group data in database
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference filelocation = storageReference.child(sharedPreferences.getString(Variables.uid, "null"))
                .child(id+".jpg");
        filelocation.putBytes(image_byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (!imgPath.equals("null")) {
//            Call_Api_For_deletelink(imgPath);
                    deleteFirestorageFromUrl(imgPath);
                }
                String url=taskSnapshot.getDownloadUrl().toString();
//                Call_Api_For_uploadLink(url);
                iosDialog.cancel();

                SharedPreferences.Editor editor=sharedPreferences.edit();
                if (selectedImgName.equals("image1")) {
                    imgPath1 = url;
                    editor.putString(Variables.u_pic1, url);
                }else if (selectedImgName.equals("image2")) {
                    imgPath2 = url;
                    editor.putString(Variables.u_pic2, url);
                }else if (selectedImgName.equals("image3")) {
                    imgPath3 = url;
                    editor.putString(Variables.u_pic3, url);
                }else if (selectedImgName.equals("image4")) {
                    imgPath4 = url;
                    editor.putString(Variables.u_pic4, url);
                }else if (selectedImgName.equals("image5")) {
                    imgPath5 = url;
                    editor.putString(Variables.u_pic5, url);
                }else if (selectedImgName.equals("image6")) {
                    imgPath6 = url;
                    editor.putString(Variables.u_pic6, url);
                }
                editor.commit();

            }});
    }

    private void deleteFirestorageFromUrl(String path) {
        Task<Void> storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void Call_Api_For_uploadLink(final String link) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("image_link",link);
            parameters.put("colum_name",selectedImgName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.uploadImages, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }



    // this method will call when we click for delete the profile images
    private void Call_Api_For_deletelink(String link) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("image_link",link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.deleteImages, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    // botoom there function are related to crop the image
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(this.getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(500,500).start(this,123);

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri userimageuri=Crop.getOutput(result);

            InputStream imageStream = null;
            try {
                imageStream = this.getContentResolver().openInputStream(userimageuri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

            String path=userimageuri.getPath();
            Matrix matrix = new Matrix();
            android.media.ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                try {
                    exif = new android.media.ExifInterface(path);
                    int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
            setImageByteData(rotatedBitmap);

        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmap(String pathName) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return decodeSampledBitmap(pathName, width, height);
    }

    private Bitmap decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainPhoto:
                selectedImgView = (ImageView) view;
                selectedImgName = "image1";
                imgPath = imgPath1;
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.subPhoto1:
                selectedImgView = (ImageView) view;
                selectedImgName = "image2";
                imgPath = imgPath2;
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.subPhoto2:
                selectedImgView = (ImageView) view;
                selectedImgName = "image3";
                imgPath = imgPath3;
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.subPhoto3:
                selectedImgView = (ImageView) view;
                selectedImgName = "image4";
                imgPath = imgPath4;
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.subPhoto4:
                selectedImgView = (ImageView) view;
                selectedImgName = "image5";
                imgPath = imgPath5;
                alertLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.subPhoto5:
                selectedImgName = "image6";
                imgPath = imgPath6;
                selectedImgView = (ImageView) view;
                alertLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}

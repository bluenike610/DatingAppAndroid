package com.datingapp.android.Profile.EditProfile;


import android.Manifest;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.Accounts.LocationActivity;
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.Main_Menu.MainMenuActivity;
import com.datingapp.android.Profile.Profile_Details.Profile_Details_F;
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
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;
import com.wonshinhyo.dragrecyclerview.SimpleDragListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.datingapp.android.CodeClasses.Variables.Select_image_from_gallry_code;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile_F extends Fragment implements View.OnClickListener {

    View view;
    Context context;

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

    public EditProfile_F() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_profile, container, false);
        context=getContext();

        sharedPreferences = Common.currentActivity.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        imgPath1 = sharedPreferences.getString(Variables.u_pic1, "null");
        imgPath2 = sharedPreferences.getString(Variables.u_pic2, "null");
        imgPath3 = sharedPreferences.getString(Variables.u_pic3, "null");
        imgPath4 = sharedPreferences.getString(Variables.u_pic4, "null");
        imgPath5 = sharedPreferences.getString(Variables.u_pic5, "null");
        imgPath6 = sharedPreferences.getString(Variables.u_pic6, "null");

        RelativeLayout backBtn = view.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        final LinearLayout photoGroup = view.findViewById(R.id.photoGroupLayout);
        photoGroup.post(new Runnable() {
            @Override
            public void run() {
                int viewWidth = photoGroup.getWidth();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) photoGroup.getLayoutParams();
                params.height = viewWidth;
                photoGroup.setLayoutParams(params);
            }
        });

        cameraBtn = view.findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getcamrapermission();
            }
        });

        galleryBtn = view.findViewById(R.id.galleryBtn);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                getStoragepermission();
            }
        });

        alertLayout = view.findViewById(R.id.alertLayout);
        alertLayout.setVisibility(View.GONE);

        closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertLayout.setVisibility(View.GONE);
            }
        });

        iosDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        ImageView mainImgView = view.findViewById(R.id.mainPhoto);
        mainImgView.setOnClickListener(this);
        if (!imgPath1.equals("null")) {
            Picasso.with(context).load(imgPath1)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(mainImgView);
        }

        ImageView subImgView1 = view.findViewById(R.id.subPhoto1);
        subImgView1.setOnClickListener(this);
        if (!imgPath2.equals("null")) {
            Picasso.with(context).load(imgPath2)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView1);
        }

        ImageView subImgView2 = view.findViewById(R.id.subPhoto2);
        subImgView2.setOnClickListener(this);
        if (!imgPath3.equals("null")) {
            Picasso.with(context).load(imgPath3)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView2);
        }

        ImageView subImgView3 = view.findViewById(R.id.subPhoto3);
        subImgView3.setOnClickListener(this);
        if (!imgPath4.equals("null")) {
            Picasso.with(context).load(imgPath4)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView3);
        }

        ImageView subImgView4 = view.findViewById(R.id.subPhoto4);
        subImgView4.setOnClickListener(this);
        if (!imgPath5.equals("null")) {
            Picasso.with(context).load(imgPath5)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView4);
        }

        ImageView subImgView5 = view.findViewById(R.id.subPhoto5);
        subImgView5.setOnClickListener(this);
        if (!imgPath6.equals("null")) {
            Picasso.with(context).load(imgPath6)
                    .resize(200, 200)
                    .placeholder(R.drawable.profile_image_placeholder)
                    .centerCrop()
                    .into(subImgView5);
        }

        String intro = sharedPreferences.getString(Variables.about, "null");

        final TextView limitNumTxt = view.findViewById(R.id.limitNumTxt);
        limitNumTxt.setText("500");

        final EditText introTxt = view.findViewById(R.id.introTxt);
        if (!intro.equals("null")) {
            introTxt.setText(intro);
        }
        introTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int currentNum = editable.toString().length();
                limitNumTxt.setText((500-currentNum) + "");
            }
        });

        final EditText jobTxt = view.findViewById(R.id.job_txt);
        jobTxt.setText(sharedPreferences.getString(Variables.job, "null"));

        ImageView saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.about, introTxt.getText().toString());
                editor.putString(Variables.job, jobTxt.getText().toString());
                editor.commit();
                Call_Api_For_edit();
            }
        });

        TextView previewBtn = view.findViewById(R.id.preview_btn);
        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.about, introTxt.getText().toString());
                editor.putString(Variables.job, jobTxt.getText().toString());
                editor.commit();
                Profile_Details_F profile_details_f = new Profile_Details_F();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, profile_details_f).commit();
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getcamrapermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(Common.currentActivity,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(Common.currentActivity.getPackageManager()) != null) {
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
        if (ContextCompat.checkSelfPermission(context,
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

                if (intent.resolveActivity(Common.currentActivity.getPackageManager()) != null) {
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
                Cursor cursor = Common.currentActivity.getContentResolver().query(
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
                    imageStream = Common.currentActivity.getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                setImageByteData(imagebitmap);
                alertLayout.setVisibility(View.GONE);
            }

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
        Display display = Common.currentActivity.getWindowManager().getDefaultDisplay();
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


    // on done btn press this method will call
    // below two mehtod is user for save the change in our profile which we have done
    private void Call_Api_For_edit() {

        iosDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", sharedPreferences.getString(Variables.uid, "null"));
            parameters.put("first_name",sharedPreferences.getString(Variables.f_name, "null"));
            parameters.put("last_name", "");
            parameters.put("birthday", sharedPreferences.getString(Variables.birth_day, "null"));
            parameters.put("gender", sharedPreferences.getString(Variables.gender, "null"));
            parameters.put("about_me", sharedPreferences.getString(Variables.about, "null"));
            parameters.put("job_title", sharedPreferences.getString(Variables.job, "null"));
            parameters.put("image1",sharedPreferences.getString(Variables.u_pic1, "null"));
            parameters.put("image2",sharedPreferences.getString(Variables.u_pic2, "null"));
            parameters.put("image3",sharedPreferences.getString(Variables.u_pic3, "null"));
            parameters.put("image4",sharedPreferences.getString(Variables.u_pic4, "null"));
            parameters.put("image5",sharedPreferences.getString(Variables.u_pic5, "null"));
            parameters.put("image6",sharedPreferences.getString(Variables.u_pic6, "null"));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.Edit_profile, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_edit_data(respo);
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

    public void Parse_edit_data(String loginData){
        iosDialog.cancel();
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){

                getActivity().onBackPressed();
            }
        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

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

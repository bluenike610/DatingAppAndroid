package com.datingapp.android.Accounts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datingapp.android.CodeClasses.Functions;
import com.datingapp.android.CodeClasses.Variables;
import com.datingapp.android.R;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Get_User_Info_A extends AppCompatActivity {

    ImageView profile_image;
    TextView first_name,last_name;
    SharedPreferences sharedPreferences;

    DatabaseReference rootref;

    Button nextbtn;
    IOSDialog iosDialog;

    ImageButton edit_profile_image;
    EditText dateofbrith_edit;
    RadioButton male_btn,female_btn;
    byte [] image_byte_array;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);



        sharedPreferences=getSharedPreferences(Variables.pref_name, Context.MODE_PRIVATE);

        rootref= FirebaseDatabase.getInstance().getReference();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();


        profile_image=findViewById(R.id.profile_image);

        edit_profile_image=findViewById(R.id.edit_profile_image);
        edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        first_name=findViewById(R.id.first_name);
        last_name=findViewById(R.id.last_name);


        dateofbrith_edit=findViewById(R.id.dateofbirth_edit);

        male_btn=findViewById(R.id.male_btn);
        female_btn=findViewById(R.id.female_btn);


        dateofbrith_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Functions.Opendate_picker(Get_User_Info_A.this,dateofbrith_edit);

            }
        });


        nextbtn=findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f_name= first_name.getText().toString();
                String l_name=last_name.getText().toString();
                String date_of_birth=dateofbrith_edit.getText().toString();

                if(image_byte_array==null){
                    Toast.makeText(Get_User_Info_A.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(f_name)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();

                } else if(TextUtils.isEmpty(l_name)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your Last Name", Toast.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(date_of_birth)){

                    Toast.makeText(Get_User_Info_A.this, "Please enter your Date of Birth", Toast.LENGTH_SHORT).show();
                }
                else {

                    Save_info();
                }

            }
        });




        Intent intent=getIntent();
        if(intent.hasExtra("id")) {
            user_id = intent.getExtras().getString("id");
            user_id = user_id.replace("+", "");
        }
        if(intent.hasExtra("fname")){
            first_name.setText(intent.getExtras().getString("fname"));
        }

        if(intent.hasExtra("lname")){
            last_name.setText(intent.getExtras().getString("lname"));
        }


    }



    // open the gallary to select and upload the picture
    private void selectImage() {
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }


    // on select the bottom method will reture the uri of that image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

        if (requestCode == 2) {

            Uri selectedImage = data.getData();
            beginCrop(selectedImage);
        }
        else if (requestCode == 123) {
            handleCrop(resultCode, data);
        }

        }

    }



    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(500,500).start(this,123);
    }


    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri userimageuri=Crop.getOutput(result);

            InputStream imageStream = null;
            try {
                imageStream =getContentResolver().openInputStream(userimageuri);
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
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            image_byte_array = out.toByteArray();


            profile_image.setImageBitmap(null);
            profile_image.setImageURI(null);
            profile_image.setImageBitmap(rotatedBitmap);



        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    // this method is used to store the selected image into database
    public void Save_info(){
        iosDialog.show();
        // first we upload image after upload then get the picture url and save the group data in database
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference filelocation = storageReference.child("User_image")
                .child(user_id + ".jpg");
        filelocation.putBytes(image_byte_array).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {


                Call_Api_For_Signup(user_id,
                        first_name.getText().toString(),
                        last_name.getText().toString(),
                        dateofbrith_edit.getText().toString()
                        ,taskSnapshot.getDownloadUrl().toString());

            }});


    }



    // this method will store the info of user to  database
    private void Call_Api_For_Signup(String user_id,
                                     String f_name,String l_name,
                                     String birthday,String picture) {

        f_name=f_name.replaceAll("\\W+","");
        l_name=l_name.replaceAll("\\W+","");

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", user_id);
            parameters.put("first_name",f_name);
            parameters.put("last_name", l_name);
            parameters.put("birthday", birthday);

            if(male_btn.isChecked()){
                parameters.put("gender","Male");

            }else{
                parameters.put("gender","Female");
            }
            parameters.put("image1",picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.SignUp, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        Parse_signup_data(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(Get_User_Info_A.this, "Somthing Wrong with Api", Toast.LENGTH_LONG).show();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    // if the signup successfull then this method will call and it store the user info in local
    public void Parse_signup_data(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray jsonArray=jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.uid,userdata.optString("fb_id"));
                editor.putString(Variables.f_name,userdata.optString("first_name"));
                editor.putString(Variables.l_name,userdata.optString("last_name"));
                editor.putString(Variables.birth_day,userdata.optString("age"));
                editor.putString(Variables.gender,userdata.optString("gender"));
                editor.putString(Variables.u_pic1,userdata.optString("image1"));
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                // after all things done we will move the user to enable location screen
                enable_location();
            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }


    private void enable_location() {

        // will move the user for enable location screen
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.Get_Info_F, enable_location_f).addToBackStack(null).commit();
    }


    public void Goback(View view) {
        finish();
    }


}

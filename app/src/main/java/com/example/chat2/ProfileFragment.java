package com.example.chat2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_GALLERY_CODE =102;
    private static final int IMAGE_PICK_CAMERA_CODE = 103;

    //arrays of permission to be requested
    String[] cameraPermissions;
    String[] storagePermissions;

    //firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference usersDatabase;
    private StorageReference storageReference;
    private String uid;

    //path where users' profile images will be stored
    String storagePath = "Users_Profile_Imgs";

    private ChatUser user;
    private ImageView imgAvatar;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSave;
    private ProgressBar progBarImage;

    //uri of picked image
    private Uri image_uri;

    public ProfileFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        View v= inflater.inflate(R.layout.profile_fragment, container, false);
        user = this.getArguments().getParcelable("user");
        firebaseUser = this.getArguments().getParcelable("firebaseUser");
        uid = firebaseUser.getUid();

        usersDatabase = FirebaseDatabase.getInstance().getReference("Users");

        //init arrays of permissions
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init views
        imgAvatar = v.findViewById(R.id.imageView_profile);
        editTextUsername = v.findViewById(R.id.ET_profile_username);
        editTextEmail = v.findViewById(R.id.ET_profile_email);
        editTextPassword = v.findViewById(R.id.ET_profile_confirmPassword);
        buttonSave = v.findViewById(R.id.Btn_profile_save);
        progBarImage = v.findViewById(R.id.PB_profile_imageUpload);

        //set initial values to views
        editTextUsername.setText(user.getUsername());
        editTextEmail.setText(firebaseUser.getEmail());

        if(!user.getImage().equals(""))
             Picasso.get().load(user.getImage()).into(imgAvatar);
        else
             Picasso.get().load("R.mipmap/ic_launcher_round").into(imgAvatar);



        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void saveUserInformation(){
         final String newUsername =  editTextUsername.getText().toString().trim();
         String newEmail = editTextEmail.getText().toString().trim();
         String password = editTextPassword.getText().toString().trim();

         if(newUsername != user.getUsername() && newEmail != firebaseUser.getEmail()){
             //both, username and email were changed, updating database

             //first update the current object user
             user.setUsername(newUsername);

             //updating the realtime database
             usersDatabase.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     if(task.isSuccessful()){
                         Toast.makeText(getActivity(), R.string.successUpdateProfile, Toast.LENGTH_LONG).show();
                     }
                     else{
                         Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
             });

             //update Authentication Firebasea


         }else
             if(newUsername != user.getUsername()){
                 // only the username was changed

                 user.setUsername(newUsername);

                 //update the current object user
                 user.setUsername(newUsername);

                 //update the database
                 usersDatabase.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             MainChatActivity.setUsername(newUsername);
                             Toast.makeText(getActivity(), R.string.successUpdateProfile, Toast.LENGTH_LONG).show();
                         }
                         else {
                             Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }
                 });

             }
             else if(newEmail != firebaseUser.getEmail()){
                 //only the email was changed

                 firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(getActivity(), R.string.successUpdateProfile, Toast.LENGTH_LONG).show();
                         }
                         else{
                             Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }
                 });
             }
    }


    private boolean checkStoragePermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled

        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void showImageDialog(){
        //show dialog containing options camera and gallery to pick an image

        String[] options={"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Pick image from");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if(which == 0){
                    //Camera clicked
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                if(which == 1){
                    //Gallery clicked
                    if(!checkStoragePermission()){
                         requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }

                }
            }
        });
        //create and show dialog
        builder.create().show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                //picking from camera, check if camera and storage permissions are allowed or not
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        pickFromCamera();
                    }
                    else{
                        //permissions denied
                        Toast.makeText(getActivity(), R.string.enableCameraStoragePerm, Toast.LENGTH_LONG).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                //picking from gallery, check if storage permissions are allowed or not
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        //permissions enabled
                        pickFromGallery();
                    }
                    else{
                        //permissions denied
                        Toast.makeText(getActivity(), R.string.enableStoragePerm, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after picking image from Camera or Gallery
        if(requestCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                image_uri = data.getData();
                uploadProfilePic(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                image_uri = data.getData();
                uploadProfilePic(image_uri);
            }
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePic(Uri uri) {
        progBarImage.setVisibility(android.view.View.VISIBLE);
        String fileName = "img_" + firebaseUser.getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(storagePath).child(fileName);
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get its URL and store it in users' database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and url is received
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            //add/update url in users' database
                            user.setImage(downloadUri.toString());
                            usersDatabase.child(firebaseUser.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //url added successfully in database
                                    progBarImage.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), R.string.successUploadPic, Toast.LENGTH_LONG).show();

                                }
                            })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     progBarImage.setVisibility(View.GONE);
                                     Toast.makeText(getActivity(), R.string.errorUploadPic, Toast.LENGTH_LONG).show();
                                 }
                             });
                        }else{
                            Toast.makeText(getActivity(), R.string.errorUploadPic, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //handle errors
                        Toast.makeText(getActivity(), R.string.errorUploadPic, Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery(){
        //picking from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }
}

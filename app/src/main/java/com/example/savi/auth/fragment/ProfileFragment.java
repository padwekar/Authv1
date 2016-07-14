package com.example.savi.auth.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.ProfilePicSelectAdapter;
import com.example.savi.auth.model.User;
import com.example.savi.auth.utils.CircleTransform;
import com.example.savi.auth.utils.Constants;
import com.example.savi.auth.utils.ImageCompressionAsyncTask;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;
import android.net.Uri;
import java.util.logging.LogRecord;

public class ProfileFragment extends Fragment {
    EditText mEdittextDisplayName ;
    EditText mEdittextStatus ;
    Button mButtonSubmit ;
    Switch mSwitch ;
    String uid ;
    User user ;
    ProgressBar mProgressbar ;
    Firebase mFireBaseUserRef ;
    Handler handler ;
    ImageView mImgaeView ;
    int image_position ;
    TypedArray mTypedArray ;
    FirebaseStorage mFirebaseStorage ;
    StorageReference mStorageReference ;
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        mFireBaseUserRef = new Firebase("https://todocloudsavi.firebaseio.com/user");
        mProgressbar = (ProgressBar)view.findViewById(R.id.progressbar);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://todocloudsavi.appspot.com/profile/");
        mStorageReference.child(mFireBaseUserRef.getAuth().getProviderData().get("email").toString());
        mStorageReference.child(mFireBaseUserRef.getAuth().getProviderData().get("email").toString()).child("profilepic");
        Toast.makeText(getContext(),"In ProfileFragment",Toast.LENGTH_SHORT).show();
        mEdittextDisplayName = (EditText)view.findViewById(R.id.edittext_displayname);
        mEdittextDisplayName.setText(mFireBaseUserRef.getAuth().getProviderData().get("email").toString());
        mEdittextStatus = (EditText)view.findViewById(R.id.edittext_status);
        mSwitch = (Switch)view.findViewById(R.id.switch_email_visibility);
        mEdittextStatus.setText("Hi everyone");
        mButtonSubmit = (Button)view.findViewById(R.id.button_submit);
        mImgaeView = (ImageView)view.findViewById(R.id.imageview_profile);
        mTypedArray = getContext().getResources().obtainTypedArray(R.array.avatars);
        mImgaeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_select_profile_pic);
                RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recycler_view_profile_pic);
                ProfilePicSelectAdapter picSelectAdapter = new ProfilePicSelectAdapter(getContext());
                picSelectAdapter.setOnProfilePicSelectedListener(new ProfilePicSelectAdapter.OnProfilePicSelectedListener() {
                    @Override
                    public void onProfilePicSelected(int position) {
                        dialog.dismiss();
                        if(position==mTypedArray.length()-1){
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, Constants.IMAGE_PICK);
                        }else {
                         //   mImgaeView.setImageResource(mTypedArray.getResourceId(position,0));
                            Picasso.with(getContext()).load(mTypedArray.getResourceId(position,0)).transform(new CircleTransform(Color.WHITE,5)).fit().into(mImgaeView);

                            image_position = position ;
                        }

                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                recyclerView.setAdapter(picSelectAdapter);
                dialog.show();
            }
        });
        uid = getActivity().getIntent().getStringExtra("uid");

        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(getActivity()!=null){
                    switch (msg.arg1){
                        case 0 :  mProgressbar.setVisibility(View.GONE);
                                  Toast.makeText(getActivity(),"User Details Updated lolwa",Toast.LENGTH_SHORT).show();
                        case 1 :  mProgressbar.setVisibility(View.GONE);
                                 Toast.makeText(getActivity(),"Success",Toast.LENGTH_SHORT).show(); break;
                    }
                }

            }
        };
        setData();
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mProgressbar.setVisibility(View.VISIBLE);
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      String displayName =  mEdittextDisplayName.getText().toString();
                      String status = mEdittextStatus.getText().toString() ;
                      String email = mFireBaseUserRef.getAuth().getProviderData().get("email").toString();
                      user  = new User();
                      user.setDisplayName(displayName);
                      user.setStatus(status);
                      user.setVisible(false);
                      user.setEmail(email);
                      user.setPicPosition(image_position);
                      user.setUid(uid);
                      Message message = new Message();
                      message.arg1 = 1 ;
                      String data = new Gson().toJson(user);
                      mFireBaseUserRef.child(uid).setValue(data);
                      handler.sendMessage(message);
                  }
              }).start();

            }
        });


        return view;
    }

    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mFireBaseUserRef.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                                String data = dataSnapshot.getValue().toString() ;
                                user = new User();
                                user = new Gson().fromJson(data,User.class);
                                Message message = new Message();
                                message.arg1 = 0 ;
                                String displayName = user.getDisplayName() ;
                                String status = user.getStatus() ;
                                image_position = user.getPicPosition() ;
                                mEdittextDisplayName.setText(displayName);
                                mEdittextStatus.setText(status);
                                message.arg2 = user.getPicPosition();
                                Picasso.with(getContext()).load(mTypedArray.getResourceId(user.getPicPosition(),0)).transform(new CircleTransform(Color.WHITE,5)).into(mImgaeView);
                                handler.sendMessage(message);
                            }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }).start();
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == Constants.IMAGE_PICK) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String image = cursor.getString(columnIndex);
            cursor.close();

            File imageFile = new File(image);
            mImgaeView.setBackgroundResource(0);
            Picasso.with(getContext()).load(imageFile).transform(new CircleTransform(Color.WHITE, 5)).fit().into(mImgaeView);


            ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity());
            imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
                @Override
                public void onCompressedImage(Bitmap bitmap) {

                    if (requestCode == Constants.IMAGE_PICK) {
                        mImgaeView.setImageBitmap(bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = mStorageReference.child(mFireBaseUserRef.getAuth().getProviderData().get("email").toString()).child("profilepic").putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });
                    }

                }
            });
            imageCompressionAsyncTask.execute(image);

        }

    }

}

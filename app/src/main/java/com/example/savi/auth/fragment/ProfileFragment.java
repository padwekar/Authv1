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
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.adapter.ProfilePicSelectAdapter;
import com.example.savi.auth.model.MessageItem;
import com.example.savi.auth.model.User;
import com.example.savi.auth.model.UserTest;
import com.example.savi.auth.utils.CircleTransform;
import com.example.savi.auth.utils.Constants;
import com.example.savi.auth.utils.ImageCompressionAsyncTask;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {

    private EditText mEdittextDisplayName ;
    private EditText mEdittextStatus ;

    private ImageView mImgaeViewProfile ;
    private Switch mSwitch ;

    private TypedArray mTypedArray ;
    private int image_position ;
    private String uid , imageSdPath ;
    private String imageUri ;

    private ProgressBar mProgressbar ;
    private Handler handler ;
    private User user ;

    private Firebase mFireBaseUserRef ;
    private Firebase mRef ;
    private FirebaseStorage mFirebaseStorage ;
    private StorageReference mStorageReference ;


    private List<UserTest> userTestList ;
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mProgressbar = (ProgressBar)view.findViewById(R.id.progressbar);
        mTypedArray = getContext().getResources().obtainTypedArray(R.array.avatars);

        mFireBaseUserRef = new Firebase("https://todocloudsavi.firebaseio.com/user");

        mRef = new Firebase("https://todocloudsavi.firebaseio.com/");
        mRef.child("detaileduser_v1");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://todocloudsavi.appspot.com/");

        Toast.makeText(getContext(),"In ProfileFragment",Toast.LENGTH_SHORT).show();

        mEdittextDisplayName = (EditText)view.findViewById(R.id.edittext_displayname);
        mEdittextDisplayName.setText(mFireBaseUserRef.getAuth().getProviderData().get("email").toString());

        mEdittextStatus = (EditText)view.findViewById(R.id.edittext_status);
        mEdittextStatus.setText("Hi everyone");

        mSwitch = (Switch)view.findViewById(R.id.switch_email_visibility);

        mImgaeViewProfile = (ImageView)view.findViewById(R.id.imageview_profile);
        mImgaeViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_select_profile_pic);
                RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recycler_view_profile_pic);
                ProfilePicSelectAdapter picSelectAdapter = new ProfilePicSelectAdapter(getContext());
                picSelectAdapter.setOnProfilePicSelectedListener(new ProfilePicSelectAdapter.OnProfilePicSelectedListener() {
                    @Override
                    public void onProfilePicSelected(int position) {
                        dialog.dismiss();
                        if (position == mTypedArray.length() - 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, Constants.IMAGE_PICK);
                        } else {
                            Picasso.with(getContext()).load(mTypedArray.getResourceId(position, 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(mImgaeViewProfile);
                            imageSdPath = null;
                            image_position = position;
                        }
                    }
                });
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(picSelectAdapter);
                dialog.show();
            }
        });

        uid = getActivity().getIntent().getStringExtra("uid");

        userTestList = new ArrayList<>() ;
        userTestList.clear();

        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(getActivity()!=null){
                    switch (msg.arg1){
                        case Constants.SUCCESS_USER_VALUE_SET :
                                  mProgressbar.setVisibility(View.GONE);
                                  Toast.makeText(getActivity(),"User Details Updated lolwa",Toast.LENGTH_SHORT).show(); break;

                        case Constants.SUCCESS_IMAGE_UPLOAD :
                                 syncData();

                        case Constants.FAIL_IMAGE_UPLOAD :
                            Toast.makeText(getActivity(),"Fail_upload",Toast.LENGTH_SHORT).show(); break;

                    }
                }
            }
        };

        setData();


        Button mButtonSubmit  = (Button)view.findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressbar.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageSdPath != null) {
                            syncImage();
                        } else {
                            notifyHandler(handler, Constants.SUCCESS_IMAGE_UPLOAD);
                        }
                    }
                }).start();

            }
        });


       /* mRef.child("detaileduser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
            UserTest userTest = postSnapShot.getValue(UserTest.class) ;
                    //   userTestList.add(postSnapShot.getValue(UserTest.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        return view;
    }

    private void syncData() {

        String displayName =  mEdittextDisplayName.getText().toString();
        String status = mEdittextStatus.getText().toString() ;
        String email = mFireBaseUserRef.getAuth().getProviderData().get("email").toString();

        User user  = new User();
        user.setDisplayName(displayName);
        user.setStatus(status);
        user.setVisible(false);
        user.setEmail(email);
        user.setPicPosition(image_position);
        user.setUid(uid);
        user.setProfileDownloadUri(imageUri);

        mRef.child("detaileduser_v1").child(uid).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getContext(),"Database Updated",Toast.LENGTH_SHORT).show();
                mRef.child("detaileduser_v1").child(uid).setPriority(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            }

        });
        mProgressbar.setVisibility(View.GONE);

/*
        final MessageItem messageme = new MessageItem();
        messageme.setMessage("hi bechoo");
        messageme.setSelf(false);
        messageme.setStatus(5);
        messageme.setSenderUid("gsgsgsddgg");
        messageme.setTimeStamp(System.currentTimeMillis() + "");

        mRef.child("detaileduser").child("gopalgogocom").child("messageItemList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                mRef.child("detaileduser").child("gopalgogocom").child("messageItemList").child(count+"").setValue(messageme);
                mRef.child("detaileduser").child("gopalgogocom").child("messageItemList").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

      //  mRef.child("detaileduser").child("gopalgogocom").setValue(test);
        //  mRef.child("detaileduser").child("gopalgogocom").child("messageList").push().setValue(new UserTest());
*/

    }

    private void syncImage() {
        ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity());
        imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
            @Override
            public void onCompressedImage(Bitmap bitmap) {

                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mStorageReference.child(mFireBaseUserRef.getAuth().getProviderData().get("email").toString()).child("profilepic").putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            notifyHandler(handler, Constants.FAIL_IMAGE_UPLOAD);

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            imageUri = taskSnapshot.getDownloadUrl().toString();
                            image_position = - 1 ;
                        notifyHandler(handler, Constants.SUCCESS_IMAGE_UPLOAD);
                        }
                    });
                }

            }
        });
        imageCompressionAsyncTask.execute(imageSdPath);
    }

    private void setData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                mRef.child("detaileduser_v1").child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                            User user = dataSnapshot.getValue(User.class);

                                String displayName = user.getDisplayName() ;
                                String status = user.getStatus() ;
                                image_position = user.getPicPosition() ;
                                mEdittextDisplayName.setText(displayName);
                                mEdittextStatus.setText(status);
                                imageUri = user.getProfileDownloadUri();
                                if(image_position>=0 && getContext()!=null)
                                    Picasso.with(getContext()).load(mTypedArray.getResourceId(image_position, 0)).transform(new CircleTransform(Color.WHITE,5)).into(mImgaeViewProfile);
                                else if(user.getProfileDownloadUri()!=null && getContext()!=null)
                                    Picasso.with(getContext()).load(Uri.parse(user.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).into(mImgaeViewProfile);

                                notifyHandler(handler, Constants.SUCCESS_USER_VALUE_SET, user.getPicPosition());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }).start();
    }



    private void downloadAndSetProfile(User user) {
        StorageReference profilePicRef = mStorageReference.
        child(mFireBaseUserRef.getAuth().getProviderData().get("email").toString()).child("profilepic");
        profilePicRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void notifyHandler(Handler handler, int arg1){
        notifyHandler(handler, arg1, -1);
    }

    private void notifyHandler(Handler handler, int arg1, int arg2) {
        Message message = new Message();
        message.arg1 = arg1 ;
        message.arg2 = arg2 ;
        handler.sendMessage(message);
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
            imageSdPath = image ;
            cursor.close();

            File imageFile = new File(image);
            mImgaeViewProfile.setBackgroundResource(0);
            Picasso.with(getContext()).load(imageFile).transform(new CircleTransform(Color.WHITE, 5)).fit().into(mImgaeViewProfile);

        }

    }

}

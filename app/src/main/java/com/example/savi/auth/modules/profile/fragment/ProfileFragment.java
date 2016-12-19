package com.example.savi.auth.modules.profile.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.modules.dashboard.activity.DashboardActivity;
import com.example.savi.auth.modules.profile.adapter.ProfilePicSelectAdapter;
import com.example.savi.auth.modules.profile.operation.manager.ProfileManager;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.example.savi.auth.utils.CircleTransform;
import com.example.savi.auth.utils.FileUtils;
import com.example.savi.auth.utils.ImageCompressionAsyncTask;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.edittext_displayname)
    EditText mEditTextDisplayName;

    @BindView(R.id.edittext_status)
    EditText mEditTextStatus;

    @BindView(R.id.imageview_profile)
    ImageView mImageViewProfile;

    @BindView(R.id.progressbar)
    ProgressBar mProgressbar;

    private String imageSdPath;
    private String imageUri;
    private TypedArray mTypedArray;
    private int image_position;

    private User mUser;
    private boolean isValidUsername ;
    //FireBase Attributes
    private Firebase mRef;
    private StorageReference mStorageReference;
    private EditText mEditTextUserId;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mProgressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        mTypedArray = getContext().getResources().obtainTypedArray(R.array.avatars);

        ButterKnife.bind(this,view);

        mRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL);
        mUser = AuthPreferences.getInstance().getUser();

        FirebaseStorage mFireBaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFireBaseStorage.getReferenceFromUrl(URLConstants.TODOCLOUD_FIREBASE_STORAGE_URL);

        mEditTextUserId = (EditText)view.findViewById(R.id.edittext_userid);
        mEditTextUserId.setText(mUser.getUserName()==null?"" : mUser.getUserName());
        if(mUser.getProfileStatus()==User.NEW) mEditTextUserId.setEnabled(true);
        mEditTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length()>3){
                    checkIfUsernameAvailable(charSequence.toString());
                }else {
                    mEditTextUserId.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_plus_black_24dp),null);
                    isValidUsername = false ;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEditTextDisplayName.setText(mUser.getDisplayName() == null ? "" : mUser.getDisplayName());
        mEditTextStatus.setText(mUser.getStatus() == null ? "Hi everyone" : mUser.getStatus());

        mImageViewProfile = (ImageView) view.findViewById(R.id.imageview_profile);
        mImageViewProfile.setOnClickListener(new View.OnClickListener() {
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
                            Picasso.with(getContext()).load(mTypedArray.getResourceId(position, 0)).transform(new CircleTransform(Color.WHITE, 5)).fit().into(mImageViewProfile);
                            imageSdPath = null;
                            imageUri = null ;
                            image_position = position;
                        }
                    }
                });

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(picSelectAdapter);
                dialog.show();
            }
        });

        mEditTextDisplayName.setText(mUser.getDisplayName());
        mEditTextStatus.setText(mUser.getStatus());

        Picasso.with(getContext()).load(mTypedArray.getResourceId(mUser.getPicPosition(), 0)).transform(new CircleTransform(Color.WHITE, 5)).into(mImageViewProfile);
        if (mUser.getProfileDownloadUri() != null && getContext() != null)
        Picasso.with(getContext()).load(Uri.parse(mUser.getProfileDownloadUri())).transform(new CircleTransform(Color.WHITE, 5)).into(mImageViewProfile);

        Button mButtonSubmit = (Button) view.findViewById(R.id.button_submit);
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    mProgressbar.setVisibility(View.VISIBLE);
                    if (imageSdPath != null) {
                        getImageUri();
                    } else {
                        updateProfile();
                    }
                }

            }
        });

        return view;
    }

    private void checkIfUsernameAvailable(String requestedUserName) {
        ProfileManager manager = new ProfileManager();
        manager.checkIfUserNameAvailable(requestedUserName, new ProfileManager.OnUserNameVerification() {
            @Override
            public void onUserNameVerificationSuccess(boolean isAvailable) {
                if(isAvailable)
                    mEditTextUserId.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_check_black_24dp),null);
                else
                    mEditTextUserId.setCompoundDrawablesWithIntrinsicBounds(null,null,ContextCompat.getDrawable(getContext(),R.drawable.ic_plus_black_24dp),null);

                isValidUsername = isAvailable ;
            }

            @Override
            public void onUserNameVerificationError(FirebaseError error) {
                Toast.makeText(getContext(),"error username varify",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid() {
        boolean isValid = true ;
        if(mUser.getProfileStatus()==User.NEW && !isValidUsername){
            Toast.makeText(getContext(),"Enter a valid user name",Toast.LENGTH_SHORT).show();
            isValid =false;
        }else if(TextUtils.isEmpty(mEditTextDisplayName.getText().toString())){
            Toast.makeText(getContext(),"Enter a display name",Toast.LENGTH_SHORT).show();
            isValid =false ;
        }
        return isValid;
    }

    private void updateProfile() {

        final User user = new User();
        if(mUser.getProfileStatus()==User.NEW){
            mRef.child(URLConstants.USER_ID).child(mEditTextUserId.getText().toString()).setValue(mUser.getEmail());
            user.setUserName(mEditTextUserId.getText().toString());
        }

        user.setProfileStatus(User.ACTIVE);
        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.setDisplayName(mEditTextDisplayName.getText().toString());
        user.setStatus(mEditTextStatus.getText().toString());

        user.setEmail(mUser.getEmail());

        user.setPicPosition(image_position);
        user.setProfileDownloadUri(imageUri);
        user.setToken(FirebaseInstanceId.getInstance().getToken());

        AuthPreferences.getInstance().setUser(user);
        mRef.child(URLConstants.USER_DETAIL).child(user.getUid()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mRef.child(URLConstants.USER_DETAIL).child(user.getUid()).setPriority
                        (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                Toast.makeText(getContext(), "Profile Updated SuccessFully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                intent.putExtra("uid",user.getUid());
                startActivity(intent);
            }

        });


        mProgressbar.setVisibility(View.GONE);

    }

    private void getImageUri() {
        ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask(getActivity());
        imageCompressionAsyncTask.setOnImageCompressed(new ImageCompressionAsyncTask.OnImageCompressed() {
            @Override
            public void onCompressedImage(Bitmap bitmap) {

                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = mStorageReference.child(mUser.getEmail()).child("profilepic").putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(getContext(), "Image Upload Fail", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            imageUri = taskSnapshot.getDownloadUrl().toString();
                            image_position = 0;
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            updateProfile();

                        }
                    });
                }else{
                    mProgressbar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Bitmap null", Toast.LENGTH_SHORT).show();

                }

            }
        });
        imageCompressionAsyncTask.execute(imageSdPath);
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == Constants.IMAGE_PICK) {
                Uri selectedImage = data.getData();
                imageSdPath = FileUtils.getFilePath(getContext(),selectedImage);


                Picasso.with(getContext()).load(selectedImage).transform(new CircleTransform(Color.WHITE, 5)).into(mImageViewProfile);
                //   Picasso.with(getActivity()).load(selectedImage).into(mImageViewProfile);

            } else if (requestCode == Constants.IMAGE_CROP) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    assert photo != null;
                    photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                }
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        mRef.unauth();
    }
}

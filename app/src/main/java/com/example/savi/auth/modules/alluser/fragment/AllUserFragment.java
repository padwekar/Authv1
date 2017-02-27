package com.example.savi.auth.modules.alluser.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseViewPagerFragment;
import com.example.savi.auth.constant.Constants;
import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.modules.alluser.adapter.AllUserAdapter;
import com.example.savi.auth.modules.alluser.operation.manager.UserManager;
import com.example.savi.auth.modules.dashboard.activity.HomeActivity;
import com.example.savi.auth.modules.message.fragment.ChatFragment;
import com.example.savi.auth.operation.manager.SocialManager;
import com.example.savi.auth.pojo.NotificationRequest;
import com.example.savi.auth.pojo.User;
import com.example.savi.auth.utils.AuthPreferences;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

import static com.example.savi.auth.constant.URLConstants.TODOCLOUD_FIREBASE_ROOT_URL;

public class AllUserFragment extends BaseViewPagerFragment {

    public static AllUserFragment newInstance() {
        return new AllUserFragment();
    }

    private AllUserAdapter mAllUserAdapter;
    private Firebase mFireBaseRef;
    private User sender;

    private ProgressBar mProgressBar;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alluser, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        Firebase.setAndroidContext(getContext());
        mFireBaseRef = new Firebase(TODOCLOUD_FIREBASE_ROOT_URL);

        mAllUserAdapter = new AllUserAdapter(getContext());

        uid = AuthPreferences.getInstance().getUserUid();

        //Request Send Listener
        mAllUserAdapter.setOnFriendShipStatusClickListener(new AllUserAdapter.OnFriendShipStatusClickListener() {
            @Override
            public void onFriedShipStatusClick(final User user) {
                mFireBaseRef.child(URLConstants.USER_DETAIL).child(user.getUid()).removeValue();
                //TODO : Uncomment it later
                //sendFriendRequest(user);
            }
        });

        mAllUserAdapter.setOnInstantMessageClickListener(new AllUserAdapter.OnInstantMessageClickListener() {
            @Override
            public void onInstantMessagesClick(User user) {
                ((HomeActivity) getActivity()).setRadioButton(R.id.radiobutton_msg);
                ((HomeActivity) getActivity()).setFragment(ChatFragment.newInstance(user));
            }
        });
        getCircleMap();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_alluser);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAllUserAdapter);
        getAllUser();

        return view;
    }

    public CharSequence getTitle() {
        return getActivity().getResources().getString(R.string.lbl_all_user);
    }

    private void getAllUser() {
        new UserManager().getAllUsers(new UserManager.OnGetAllUserManager() {
            @Override
            public void onUserAdded(User user) {
                if (mProgressBar.getVisibility() != View.GONE) mProgressBar.setVisibility(View.GONE);
                if (!uid.equals(user.getUid())) mAllUserAdapter.addUser(user);

            }

            @Override
            public void onUserUpdated(User user) {
                if (!uid.equals(user.getUid())) mAllUserAdapter.addUser(user);
            }

            @Override
            public void onUserRemoved(User user) {

            }

            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
    }

    private void getCircleMap() {
        SocialManager manager = new SocialManager();
        manager.getUserCircleMap(uid, new SocialManager.OnGetUserCircleMap() {
            @Override
            public void onGetUserCircleMapSuccess(HashMap<String, Integer> circleMap) {
                if (isAdded()) mAllUserAdapter.setCircleMap(circleMap);
            }

            @Override
            public void onGetUserCircleMapFailure(FirebaseError error) {
                if (isAdded())
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFriendRequest(User receiverInfo) {

        mFireBaseRef.child(Constants.CIRCLE).child(uid).child(receiverInfo.getUid()).setValue(User.REQUEST_SENT, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                String message = "Successful";
                if (firebaseError != null)
                    message = firebaseError.getMessage();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();


            }
        });
        mFireBaseRef.child(Constants.CIRCLE).child(receiverInfo.getUid()).child(uid).setValue(User.FRIEND_REQUEST);

        NotificationRequest request = new NotificationRequest();
        request.data.put("body", AuthPreferences.getInstance().getUserName() + " wants to be your friend ! ");
        request.data.put("for", receiverInfo.getUid());
        request.to = receiverInfo.getToken();
        SocialManager manager = new SocialManager();
        manager.sendFriendRequest(request, new SocialManager.OnSendFriendRequest() {
            @Override
            public void onSendFriendRequestSuccess(Object o) {
                Toast.makeText(getContext(), "Notification Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendFriendRequestFailure(Exception e) {
                Toast.makeText(getContext(), "Notification Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

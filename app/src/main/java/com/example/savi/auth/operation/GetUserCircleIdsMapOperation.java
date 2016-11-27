package com.example.savi.auth.operation;

import com.example.savi.auth.constant.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

public class GetUserCircleIdsMapOperation {

    public interface GetUserCircleMapOperationListener {
        void onGetUserCircleMapOperationListenerSuccess(HashMap<String, Integer> circleMap);

        void onGetUserCircleMapOperationListenerFailure(FirebaseError error);
    }

    public GetUserCircleMapOperationListener listener;

    private Firebase fireBaseRef = new Firebase(Constants.TODOCLOUD_ROOT_FIREBASE_URL + Constants.CIRCLE);

    public GetUserCircleIdsMapOperation(String uid, int type, GetUserCircleMapOperationListener listener) {
        this.listener = listener;
        if (type == Constants.VALUE_EVENT_LISTENER) addValueEventListener(uid);
        else if (type == Constants.SINGLE_VALUE_EVENT_LISTENER) addSingleValueEventListener(uid);
    }

    private void addValueEventListener(String uid) {
        fireBaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listener == null) return;
                getCircleMap(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (listener == null) return;
                updateError(firebaseError);
            }
        });
    }


    private void addSingleValueEventListener(String uid) {
        fireBaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (listener == null) return;
                getCircleMap(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (listener == null) return;
                updateError(firebaseError);
            }
        });

    }


    private void updateError(FirebaseError firebaseError) {
        listener.onGetUserCircleMapOperationListenerFailure(firebaseError);
    }

    private void getCircleMap(DataSnapshot dataSnapshot) {
        HashMap<String, Integer> circleMap;
        if (dataSnapshot == null || dataSnapshot.getChildren() == null) {
            circleMap = new HashMap<>();
        } else {
            circleMap = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Integer>>() {
            });
        }
        if (circleMap == null || circleMap.size() == 0) {
            circleMap = new HashMap<>();
        }

        listener.onGetUserCircleMapOperationListenerSuccess(circleMap);

    }


}

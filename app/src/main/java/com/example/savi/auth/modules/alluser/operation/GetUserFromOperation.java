package com.example.savi.auth.modules.alluser.operation;

import com.example.savi.auth.constant.URLConstants;
import com.example.savi.auth.operation.BaseOperation;
import com.firebase.client.Firebase;

public class GetUserFromOperation extends BaseOperation {
    GetUserFromOperation(String uid, GetAllUserOperation.OnGetAllUserListener listener) {
        Firebase firebaseRef = new Firebase(URLConstants.TODOCLOUD_FIREBASE_ROOT_URL + URLConstants.USER_DETAIL);
    }

}

package com.example.savi.auth.operation;

import com.example.savi.auth.constant.OperationConstants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

/**
 * Created by Savi on 27-11-2016.
 */
public class BaseOperation {

    protected boolean isEmpty(DataSnapshot dataSnapshot){
        if(dataSnapshot==null || dataSnapshot.getChildren()==null){new FirebaseError(-400, OperationConstants.NO_DATA_FOUND);return true;}
        return false;
    }
}

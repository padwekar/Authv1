package com.example.savi.auth.modules.alluser.operation.manager;

import com.example.savi.auth.modules.alluser.operation.GetAllUserOperation;
import com.example.savi.auth.pojo.User;
import com.firebase.client.FirebaseError;

public class UserManager {

    public interface OnGetAllUserManager{
        void onUserAdded(User user, String key);
        void onUserUpdated(User user,String key);
        void onUserRemoved(User user);
        void onCancelled(FirebaseError error);
    }

    public void getAllUsers(final OnGetAllUserManager listener){
        new GetAllUserOperation(new GetAllUserOperation.OnGetAllUserListener() {
            @Override
            public void onUserAdded(User user, String key) {
                if(listener!=null)listener.onUserAdded(user,key);
            }

            @Override
            public void onUserUpdated(User user, String key) {
                if(listener!=null)listener.onUserUpdated(user,key);
            }

            @Override
            public void onUserRemoved(User user) {
                if(listener!=null)listener.onUserRemoved(user);
            }

            @Override
            public void onCancelled(FirebaseError error) {
                if(listener!=null)listener.onCancelled(error);
            }
        });
    }
}

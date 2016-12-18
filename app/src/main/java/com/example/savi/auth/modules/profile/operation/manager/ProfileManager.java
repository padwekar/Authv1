package com.example.savi.auth.modules.profile.operation.manager;

import com.example.savi.auth.modules.profile.operation.VerifyUserNameOperation;
import com.firebase.client.FirebaseError;

public class ProfileManager {

    public interface OnUserNameVerification{
        void onUserNameVerificationSuccess(boolean isAvailable);
        void onUserNameVerificationError(FirebaseError error);
    }

    public void checkIfUserNameAvailable(String username,final OnUserNameVerification onUserNameVerification){
         new VerifyUserNameOperation(username, new VerifyUserNameOperation.OnVerifyUserNameOperation() {
            @Override
            public void OnVerifyUserNameOperationSuccess(boolean isAvailable) {
                if(onUserNameVerification!=null)
                onUserNameVerification.onUserNameVerificationSuccess(isAvailable);
            }

            @Override
            public void OnVerifyUserNameOperationError(FirebaseError error) {
                if(onUserNameVerification!=null)
               onUserNameVerification.onUserNameVerificationError(error);
            }
        });
    }
}

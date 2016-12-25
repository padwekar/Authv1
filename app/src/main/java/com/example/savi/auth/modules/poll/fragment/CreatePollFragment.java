package com.example.savi.auth.modules.poll.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.savi.auth.R;
import com.example.savi.auth.base.BaseFragment;
import com.example.savi.auth.pojo.Group;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreatePollFragment extends BaseFragment {

    private Group mGroup ;

    @BindView(R.id.edittext_group_name)
    EditText mEditTextGroupName ;

    @BindView(R.id.spinner_max_members)
    Spinner mSpinnerMaxMember ;

    @BindView(R.id.switch_password)
    Switch mSwitchPassword ;

    @BindView(R.id.edittext_password)
    EditText mEditTextPassword;

    @BindView(R.id.radiogroup_group_status)
    RadioGroup mRadioGroupStatus ;

/*
    @BindView(R.id.textinputlayout_group_name)
    TextInputEditText mTextInputEditTextGroupName;
*/

    @BindView(R.id.textinputlayout_password)
    TextInputLayout mTextInputLayoutPassword ;

    public OnSubmitClickListener mOnClickListener;

    public interface OnSubmitClickListener{
        void onSubmitClick(Group group);
        void onCancelClick();
    }

    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener){
        this.mOnClickListener = onSubmitClickListener ;
    }

    public static CreatePollFragment newInstance(Group group) {
        CreatePollFragment fragment = new CreatePollFragment();
        fragment.mGroup = group ;
        return fragment;
    }


    public static CreatePollFragment newInstance() {
        return newInstance(new Group());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group,container,false);

        ButterKnife.bind(this,view);

        Button buttonDone = ButterKnife.findById(view,R.id.button_done);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid() && mOnClickListener !=null){
                    mGroup.setId(mGroup.getId()<=0?(1000 + new Random().nextInt(9000)):mGroup.getId());
                    mGroup.setMaxMembers(mSpinnerMaxMember.getSelectedItemPosition()+1);
                    mGroup.setName(mEditTextGroupName.getText().toString());
                    mGroup.setStatus(getStatus());
                    if(mSwitchPassword.isChecked()){
                        mGroup.setType(Group.TYPE_PROTECTED);
                        mGroup.setPassword(mEditTextPassword.getText().toString());
                    }
                    if(mOnClickListener != null){
                        mOnClickListener.onSubmitClick(mGroup);
                    }
                }
            }
        });
        Button buttonCancel = ButterKnife.findById(view,R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(mOnClickListener !=null){
                        mOnClickListener.onCancelClick();
                    }
            }
        });
        return view;
    }

    private int getStatus() {
        switch (mRadioGroupStatus.getCheckedRadioButtonId()){
            case R.id.radiobutton_open : return Group.OPEN_GROUP ;
            case R.id.radiobutton_closed : return Group.CLOSED_GROUP ;
            case R.id.radiobutton_active : return Group.ACTIVE_GROUP ;
            default:return Group.OPEN_GROUP;
        }
    }

    private boolean isValid() {
        boolean isValid = true ;
        if(TextUtils.isEmpty(mEditTextGroupName.getText().toString())){
            isValid = false ;
            Toast.makeText(getContext(), "Enter Group Name", Toast.LENGTH_SHORT).show();
            /*mTextInputEditTextGroupName.setEnabled(true);
            mTextInputEditTextGroupName.setError(getString(R.string.error_msg_required));*/
        }else if(mSwitchPassword.isChecked() && TextUtils.isEmpty(mEditTextPassword.getText().toString())){
            isValid = false ;
            mTextInputLayoutPassword.setEnabled(true);
            mTextInputLayoutPassword.setError(getString(R.string.error_msg_required));
        }
        return isValid ;
    }
}

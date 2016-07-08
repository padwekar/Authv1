package com.example.savi.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.savi.auth.R;

public class AllUserFragment extends Fragment {

    public static AllUserFragment newInstance() {
        AllUserFragment fragment = new AllUserFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alluser,container,false);
        Toast.makeText(getContext(),"In AllUserFragment",Toast.LENGTH_SHORT).show();
        return view ;
    }


}

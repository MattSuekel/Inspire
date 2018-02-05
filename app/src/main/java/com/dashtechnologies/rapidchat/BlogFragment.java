package com.dashtechnologies.rapidchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dashtechnologies.rapidchat.activities.MainTwitterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogFragment extends Fragment {


    public BlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_blog, container, false);

        View view = inflater.inflate(R.layout.fragment_blog,
                container, false);
        view.findViewById(R.id.button2).setOnClickListener(mListener);
        view.findViewById(R.id.btweet).setOnClickListener(mListener);

        return view;

    }

    private final View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button2:
                    startActivity(new Intent(getActivity(), BlogActivity.class));
                    break;
                case R.id.btweet:
                    startActivity(new Intent(getActivity(), MainTwitterActivity.class));
                    break;
            }
        }
    };

}

package com.ucas.jomoo.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucas.jomoo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpTextFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_help_text,null);


        return v;
    }

    public  static HelpTextFragment newInstance(){
        HelpTextFragment helptext = new HelpTextFragment();
        return helptext;
    }

}

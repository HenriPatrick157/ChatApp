package com.kenzohenri.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kenzohenri.chatapp.Home;
import com.kenzohenri.chatapp.R;

public class TermsFragment extends Fragment {
    TextView terms_title, short_notice,terms;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_terms, container,false);

        terms_title = view.findViewById(R.id.terms_title);
        short_notice = view.findViewById(R.id.short_notice);
        terms = view.findViewById(R.id.terms);

        //make it scroll
        terms.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((Home) getActivity()).hideFloatingActionButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((Home) getActivity()).showFloatingActionButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home) getActivity()).hideFloatingActionButton();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Home) getActivity()).showFloatingActionButton();
    }
}

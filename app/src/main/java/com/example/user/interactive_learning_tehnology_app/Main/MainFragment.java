package com.example.user.interactive_learning_tehnology_app.Main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.user.interactive_learning_tehnology_app.R;

public class MainFragment extends Fragment {
    private ImageButton detectButton;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        final ChoiceFeedBackFragment choiceFeedBackFragment = new ChoiceFeedBackFragment();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        detectButton = (ImageButton) view.findViewById(R.id.detectButton);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.center, choiceFeedBackFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
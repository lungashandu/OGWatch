package com.sourcream.orientgardenneighbourhoodwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

public class ViewImageFragment extends DialogFragment {
    private ImageView imageView;

    public ViewImageFragment() {
    }

    public static ViewImageFragment newInstance(String imageUrl) {
        ViewImageFragment fragment = new ViewImageFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_image, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.stolen_item_enlarged_imageView);
        String imageUrl = getArguments().getString("imageUrl", null);
        getDialog().setTitle("Stolen Item");
        Picasso.get().load(imageUrl).into(imageView);
    }
}

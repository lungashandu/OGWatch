package com.sourcream.orientgardenneighbourhoodwatch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewImageFragment extends DialogFragment {

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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.stolen_item_enlarged_imageView);
        assert getArguments() != null;
        String imageUrl = getArguments().getString("imageUrl", null);
        Objects.requireNonNull(getDialog()).setTitle("Stolen Item");
        Picasso.get().load(imageUrl).into(imageView);
    }
}

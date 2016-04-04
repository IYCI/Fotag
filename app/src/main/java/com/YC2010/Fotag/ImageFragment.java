package com.YC2010.Fotag;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.UUID;

/**
 * Created by jason on 4/1/2016.
 */
public class ImageFragment extends Fragment {
    private static final String UUID_KEY = "UUID_KEY";
    private ImageItem mImageItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        UUID mUUID = (UUID) getArguments().getSerializable(UUID_KEY);
        mImageItem = Model.getInstance().getImageByUUID(mUUID);

        ImageView mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        mImageView.setImageDrawable(mImageItem.getmDrawable());
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return rootView;
    }
}

package com.zjl.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

/**
 * Created by lenovo on 2017/8/13.
 */

public class PhotoViewFragment extends DialogFragment {
    public static final String EXTRA_DELETE_PHOTO = "DeletePhoto";
    private ImageView mPhotoView;

    private static final String ARG_PHOTO = "photo";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_view, null);
        mPhotoView = (ImageView) v.findViewById(R.id.iv_dialog_photo_view);
        String path = getArguments().getString(ARG_PHOTO, "");
        if (TextUtils.isEmpty(path)){

        }
        File photoFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mPhotoView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(photoFile.getName())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static PhotoViewFragment newInstance(String photoPath){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, photoPath);

        PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return ;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE_PHOTO, "deletePhoto");
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

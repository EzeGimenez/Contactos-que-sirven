package com.visoft.jobfinder.turnprofragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.visoft.jobfinder.Objects.ImagePicker;
import com.visoft.jobfinder.R;

import static android.app.Activity.RESULT_CANCELED;


public class ProfilePicFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private Uri filePath;
    private Bitmap bitmap;

    //Componentes gráficas
    private ImageButton btnChoosePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pic, container, false);
    }

    private Uri outputFileUri;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.selecciona_foto);

        btnChoosePic = view.findViewById(R.id.btnChoosePic);

        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                startActivityForResult(chooseImageIntent, PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {

                if (data.getExtras() == null) {
                    bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);
                } else {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                ImageView ivPic = getView().findViewById(R.id.ivPic);
                ivPic.setVisibility(View.VISIBLE);
                btnChoosePic.setVisibility(View.GONE);
                ivPic.setImageBitmap(bitmap);

            }

        }

    }

    public Bitmap getFilePath() {
        return bitmap;
    }

}
package com.visoft.network.turnprofragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.GlideApp;
import com.visoft.network.Util.ImagePicker;

import static android.app.Activity.RESULT_CANCELED;


public class ProfilePicFragment extends Fragment {
    private static final int PICK_IMAGE = 1;
    private Bitmap bitmap;

    //Componentes gráficas
    private ImageButton btnChoosePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pic, container, false);
    }

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


        Bundle args = getArguments();
        if (args != null) {
            ImageView ivPic = getView().findViewById(R.id.ivPic);
            ivPic.setVisibility(View.VISIBLE);
            btnChoosePic.setAlpha(0.7f);

            byte[] byteArray = args.getByteArray("bitmapByteArray");
            if (byteArray != null && !args.getBoolean("hasPic", false)) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                ivPic.setImageBitmap(bitmap);
            } else if (args.getBoolean("hasPic", false)) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                StorageReference userRef = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(
                                Constants.FIREBASE_USERS_CONTAINER_NAME + "/" +
                                        mAuth.getCurrentUser().getUid() +
                                        args.getInt("imgVersion") + ".jpg");
                GlideApp.with(getContext())
                        .load(userRef)
                        .into(ivPic);
            }
        }
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
                btnChoosePic.setAlpha(0.0f);
                ivPic.setImageBitmap(bitmap);
            }
        }
    }

    public Bitmap getFilePath() {
        return bitmap;
    }

}
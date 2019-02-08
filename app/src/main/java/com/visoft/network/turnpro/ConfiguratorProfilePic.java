package com.visoft.network.turnpro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.visoft.network.R;
import com.visoft.network.funcionalidades.ImagePicker;
import com.visoft.network.util.Constants;
import com.visoft.network.util.GlideApp;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_CANCELED;


public class ConfiguratorProfilePic extends ConfiguratorTurnPro {
    private static final int PICK_IMAGE = 1;
    boolean saved;
    private Bitmap bitmap;
    private ImageView ivPic;
    //Componentes gráficas
    private ImageButton btnChoosePic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        ivPic = getView().findViewById(R.id.ivPic);
        ivPic.setVisibility(View.VISIBLE);
        btnChoosePic.setAlpha(0.7f);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_IMAGE) {
                user.setHasPic(true);
                if (data.getExtras() == null) {
                    bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);
                } else {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                saved = false;

                ImageView ivPic = getView().findViewById(R.id.ivPic);
                ivPic.setVisibility(View.VISIBLE);
                btnChoosePic.setAlpha(0.0f);
                ivPic.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void finalizar() {
        if (bitmap != null && !saved) {

            StorageReference storage = FirebaseStorage.getInstance().getReference();

            StorageReference userRef;
            if (user.getHasPic()) {
                storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg").delete();
                user.setImgVersion(user.getImgVersion() + 1);
            }

            userRef = storage.child(Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" + user.getUid() + user.getImgVersion() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            userRef.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            user.setHasPic(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            bitmap = null;
                            user.setHasPic(false);
                        }
                    });

            saved = true;
            user.setHasPic(true);

        } else if (bitmap == null && !saved) {
            user.setHasPic(false);
        }
    }

    @Override
    protected void iniciar() {
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
        } else if (user.getHasPic()) {
            StorageReference userRef = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(
                            Constants.FIREBASE_USERS_PRO_CONTAINER_NAME + "/" +
                                    user.getUid() +
                                    user.getImgVersion() + ".jpg");
            saved = true;
            GlideApp.with(getContext())
                    .load(userRef)
                    .into(ivPic);
        }
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public String getDescriptor() {
        return "selecciona_foto";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}
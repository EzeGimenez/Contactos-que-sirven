package com.visoft.network.turnpro;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;


public class ConfiguratorSocialApps extends ConfiguratorTurnPro implements View.OnClickListener {
    private ImageView btnInstagramAdded, btnFacebookAdded, btnMailAdded, btnWhatsappAdded;
    private ImageView btnInstagram, btnFacebook, btnMail, btnWhatsapp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social_apps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.selecciona_social_networks);

        btnInstagramAdded = view.findViewById(R.id.btnInstagramA);
        btnFacebookAdded = view.findViewById(R.id.btnFacebookA);
        btnMailAdded = view.findViewById(R.id.btnMailA);
        btnWhatsappAdded = view.findViewById(R.id.btnWhatsappA);

        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnMail = view.findViewById(R.id.btnMail);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);

        iniciarListeners();
    }

    private void iniciarListeners() {
        btnInstagram.setOnClickListener(this);
        btnWhatsapp.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnMail.setOnClickListener(this);

        btnInstagramAdded.setOnClickListener(this);
        btnWhatsappAdded.setOnClickListener(this);
        btnFacebookAdded.setOnClickListener(this);
        btnMailAdded.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CustomDialog dialog = new CustomDialog(getContext());

        String title = "";
        String msg = "";
        DialogInterface.OnClickListener listenerPos = null, listenerNeg = null;
        final EditText editText = (EditText) getLayoutInflater().inflate(R.layout.edit_text, null, false);

        switch (v.getId()) {
            case R.id.btnInstagram:

                title = "Instagram";
                msg = getString(R.string.introduzcaUsuarioInstagram);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);

                dialog.setView(editText);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        if (input.length() > 0) {
                            user.setInstagramID(input);
                            btnInstagramAdded.setVisibility(View.VISIBLE);
                            btnInstagram.setVisibility(View.GONE);
                        }
                    }
                };

                break;

            case R.id.btnFacebook:
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                title = "Facebook";
                msg = getString(R.string.introduzcaUsuarioFacebook);
                dialog.setView(editText);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        if (input.length() > 0) {
                            user.setFacebookID(input);
                            btnFacebookAdded.setVisibility(View.VISIBLE);
                            btnFacebook.setVisibility(View.GONE);
                        }
                    }
                };

                break;

            case R.id.btnWhatsapp:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                title = "Whatsapp";
                editText.setText(user.getTelefono1());
                msg = getString(R.string.introduzcaNumeroWhatsapp);
                dialog.setView(editText);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        if (input.length() > 0) {
                            user.setWhatsappNum(input
                                    .replaceAll("\\+", "")
                                    .replaceAll(" ", ""));
                            btnWhatsappAdded.setVisibility(View.VISIBLE);
                            btnWhatsapp.setVisibility(View.GONE);
                        }
                    }
                };

                break;

            case R.id.btnMail:

                title = "Email";
                msg = getString(R.string.deseaMostrarSuEmail);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setShowEmail(true);
                        btnMail.setVisibility(View.GONE);
                        btnMailAdded.setVisibility(View.VISIBLE);
                    }
                };
                listenerNeg = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setShowEmail(false);
                    }
                };

                break;

            case R.id.btnInstagramA:
                title = "Instagram";
                msg = getString(R.string.removerInstagram);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setInstagramID("");
                        btnInstagram.setVisibility(View.VISIBLE);
                        btnInstagramAdded.setVisibility(View.GONE);
                    }
                };

                break;

            case R.id.btnFacebookA:

                title = "Facebook";
                msg = getString(R.string.removerFacebook);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setFacebookID("");
                        btnFacebook.setVisibility(View.VISIBLE);
                        btnFacebookAdded.setVisibility(View.GONE);
                    }
                };

                break;

            case R.id.btnWhatsappA:

                title = "Whatsapp";
                msg = getString(R.string.removerWhatsapp);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setWhatsappNum("");
                        btnWhatsapp.setVisibility(View.VISIBLE);
                        btnWhatsappAdded.setVisibility(View.GONE);
                    }
                };

                break;

            case R.id.btnMailA:

                title = "Email";
                msg = getString(R.string.removerMail);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setShowEmail(false);
                        btnMail.setVisibility(View.VISIBLE);
                        btnMailAdded.setVisibility(View.GONE);
                    }
                };

                break;

        }

        dialog
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(getString(R.string.aceptar), listenerPos)
                .setNegativeButton(getString(R.string.cancelar), listenerNeg);
        dialog.show();
    }

    @Override
    protected void finalizar() {

    }

    @Override
    protected void iniciar() {
        if (user.getFacebookID() != null && user.getFacebookID().length() > 0) {
            btnFacebookAdded.setVisibility(View.VISIBLE);
            btnFacebook.setVisibility(View.GONE);
        } else {
            btnFacebookAdded.setVisibility(View.GONE);
            btnFacebook.setVisibility(View.VISIBLE);
        }

        if (user.getInstagramID() != null && user.getInstagramID().length() > 0) {
            btnInstagramAdded.setVisibility(View.VISIBLE);
            btnInstagram.setVisibility(View.GONE);
        } else {
            btnInstagramAdded.setVisibility(View.GONE);
            btnInstagram.setVisibility(View.VISIBLE);
        }

        if (user.getWhatsappNum() != null && user.getWhatsappNum().length() > 0) {
            btnWhatsappAdded.setVisibility(View.VISIBLE);
            btnWhatsapp.setVisibility(View.GONE);
        } else {
            btnWhatsappAdded.setVisibility(View.GONE);
            btnWhatsapp.setVisibility(View.VISIBLE);
        }

        if (user.getShowEmail()) {
            btnMailAdded.setVisibility(View.VISIBLE);
            btnMail.setVisibility(View.GONE);
        } else {
            btnMailAdded.setVisibility(View.GONE);
            btnMail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public String getDescriptor() {
        return "selecciona_social_networks";
    }

    @Override
    public boolean handleBackPress() {
        return false;
    }
}

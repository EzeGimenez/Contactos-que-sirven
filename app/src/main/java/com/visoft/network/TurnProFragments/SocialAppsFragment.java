package com.visoft.network.TurnProFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.visoft.network.Objects.UserPro;
import com.visoft.network.R;
import com.visoft.network.custom_views.CustomDialog;


public class SocialAppsFragment extends Fragment implements View.OnClickListener {
    private UserPro proUser;

    private ImageView btnInstagramAdded, btnFacebookAdded, btnMailAdded, btnWhatsappAdded;
    private ImageView btnInstagram, btnFacebook, btnMail, btnWhatsapp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        proUser = (UserPro) getArguments().getSerializable("user");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social_apps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvInfo = getActivity().findViewById(R.id.tvInfo);
        tvInfo.setText(R.string.selecciona_social_networks);

        //Inicializacion de variables gráficas
        //Componentes gráficas
        btnInstagramAdded = view.findViewById(R.id.btnInstagramA);
        btnFacebookAdded = view.findViewById(R.id.btnFacebookA);
        btnMailAdded = view.findViewById(R.id.btnMailA);
        btnWhatsappAdded = view.findViewById(R.id.btnWhatsappA);

        btnInstagram = view.findViewById(R.id.btnInstagram);
        btnFacebook = view.findViewById(R.id.btnFacebook);
        btnMail = view.findViewById(R.id.btnMail);
        btnWhatsapp = view.findViewById(R.id.btnWhatsapp);

        iniciarUI();
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
                            proUser.setInstagramID(input);
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
                            proUser.setFacebookID(input);
                            btnFacebookAdded.setVisibility(View.VISIBLE);
                            btnFacebook.setVisibility(View.GONE);
                        }
                    }
                };

                break;

            case R.id.btnWhatsapp:
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                title = "Whatsapp";
                editText.setText(proUser.getTelefono1());
                msg = getString(R.string.introduzcaNumeroWhatsapp);
                dialog.setView(editText);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        if (input.length() > 0) {
                            proUser.setWhatsappNum(input
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
                        proUser.setShowEmail(true);
                        btnMail.setVisibility(View.GONE);
                        btnMailAdded.setVisibility(View.VISIBLE);
                    }
                };
                listenerNeg = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        proUser.setShowEmail(false);
                    }
                };

                break;

            case R.id.btnInstagramA:
                title = "Instagram";
                msg = getString(R.string.removerInstagram);
                listenerPos = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        proUser.setInstagramID("");
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
                        proUser.setFacebookID("");
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
                        proUser.setWhatsappNum("");
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
                        proUser.setShowEmail(false);
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

    private void iniciarUI() {
        if (proUser.getFacebookID() != null && proUser.getFacebookID().length() > 0) {
            btnFacebookAdded.setVisibility(View.VISIBLE);
            btnFacebook.setVisibility(View.GONE);
        } else {
            btnFacebookAdded.setVisibility(View.GONE);
            btnFacebook.setVisibility(View.VISIBLE);
        }

        if (proUser.getInstagramID() != null && proUser.getInstagramID().length() > 0) {
            btnInstagramAdded.setVisibility(View.VISIBLE);
            btnInstagram.setVisibility(View.GONE);
        } else {
            btnInstagramAdded.setVisibility(View.GONE);
            btnInstagram.setVisibility(View.VISIBLE);
        }

        if (proUser.getWhatsappNum() != null && proUser.getWhatsappNum().length() > 0) {
            btnWhatsappAdded.setVisibility(View.VISIBLE);
            btnWhatsapp.setVisibility(View.GONE);
        } else {
            btnWhatsappAdded.setVisibility(View.GONE);
            btnWhatsapp.setVisibility(View.VISIBLE);
        }

        if (proUser.getShowEmail()) {
            btnMailAdded.setVisibility(View.VISIBLE);
            btnMail.setVisibility(View.GONE);
        } else {
            btnMailAdded.setVisibility(View.GONE);
            btnMail.setVisibility(View.VISIBLE);
        }
    }
}

package com.visoft.network.funcionalidades;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.visoft.network.Objects.ChatOverview;
import com.visoft.network.Objects.User;
import com.visoft.network.Objects.UserNormal;
import com.visoft.network.R;
import com.visoft.network.Util.Constants;
import com.visoft.network.Util.Database;
import com.visoft.network.exceptions.InvalidEmailException;
import com.visoft.network.exceptions.InvalidPasswordException;
import com.visoft.network.exceptions.InvalidUsernameException;

import java.util.ArrayList;

public class AccountManagerFirebaseNormal extends AccountManager {

    private static AccountManager instance;
    private static ListenerRequestResult listener;
    private static AppCompatActivity act;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private UserNormal user;
    private FirebaseUser fbUser;

    private GoogleSignInClient googleClient;

    private AccountManagerFirebaseNormal() {
        GoogleSignInOptions op = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("421055195921-q7vcj31bcr0lmhe9s4p9176r44ndma8m.apps.googleusercontent.com")
                .requestEmail()
                .requestId()
                .build();

        usersRef = Database.getDatabase()
                .getReference()
                .child(Constants.FIREBASE_USERS_NORMAL_CONTAINER_NAME);

        googleClient = GoogleSignIn.getClient(act, op);
        mAuth = FirebaseAuth.getInstance();
    }

    public static AccountManager getInstance(ListenerRequestResult l, AppCompatActivity a) {
        listener = l;
        act = a;

        if (instance == null) {
            instance = new AccountManagerFirebaseNormal();
        }
        return instance;
    }

    //USER
    private UserNormal createUser() {
        UserNormal user = new UserNormal();
        String instanceId = FirebaseInstanceId.getInstance().getToken();

        FirebaseInstanceId.getInstance().getToken();

        user.setUsername(fbUser.getDisplayName())
                .setRating(-1)
                .setNumberReviews(0)
                .setUid(fbUser.getUid())
                .setEmail(fbUser.getEmail())
                .setInstanceID(instanceId);

        this.user = user;
        return user;
    }

    private void registerUserInDatabase(final UserNormal u, final int requestCode) {
        String json = GsonerUser.getGson().toJson(u, User.class);
        usersRef.child(fbUser.getUid()).setValue(json).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", u);
                    notifyAccountActivity(true, requestCode, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", act.getString(R.string.error_sign_up));
                    notifyAccountActivity(false, requestCode, bundle);
                }
            }
        });
    }

    private void getUserFromDatabase(final int requestCode) {
        if (user == null) {
            usersRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String json = dataSnapshot.getValue(String.class);
                    user = (UserNormal) GsonerUser.getGson().fromJson(json, User.class);

                    if (user != null) {
                        user.setInstanceID(FirebaseInstanceId.getInstance().getToken());
                        dataSnapshot.getRef().setValue(GsonerUser.getGson().toJson(user, User.class));

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        notifyAccountActivity(true, requestCode, bundle);
                    } else {
                        registerUserInDatabase(createUser(), -1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    notifyAccountActivity(false, requestCode, null);
                }
            });
        } else {

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            notifyAccountActivity(true, requestCode, bundle);
        }
    }

    @Override
    public User getCurrentUser(int requestCode) {
        if (user != null) {
            getUserFromDatabase(requestCode);
            return user;
        } else {
            fbUser = mAuth.getCurrentUser();

            if (fbUser == null) {
                notifyAccountActivity(false, requestCode, null);
            } else {
                getUserFromDatabase(requestCode);
            }
        }
        return null;
    }

    @Override
    public void invalidate() {
        user = null;
    }

    //LOGINS
    @Override
    public void logInWithFacebook(int requestCode) {

    }

    //GOOGLE
    @Override
    public void logInWithGoogle(int requestCode) {
        Intent signInIntent = googleClient.getSignInIntent();
        act.startActivityForResult(signInIntent, requestCode);
    }

    private void authWithGoogle(GoogleSignInAccount acc, final int requestCode) {
        AuthCredential cred = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fbUser = mAuth.getCurrentUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                registerUserInDatabase(createUser(), requestCode);
                            } else {
                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("pro" + mAuth.getCurrentUser().getUid())) {
                                            getUserFromDatabase(requestCode);
                                        } else {
                                            registerUserInDatabase(createUser(), requestCode);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("error", act.getString(R.string.error_google));
                            notifyAccountActivity(false, requestCode, bundle);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount acc;
                acc = task.getResult(ApiException.class);
                authWithGoogle(acc, requestCode);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("error", act.getString(R.string.error_google));
            notifyAccountActivity(false, requestCode, bundle);
        }
    }

    @Override
    public void deleteAccount(final int requestCode) {
        final DatabaseReference rootRef = Database.getDatabase().getReference();

        //Removing from users;
        rootRef.child(Constants.FIREBASE_USERS_NORMAL_CONTAINER_NAME).child(user.getUid()).removeValue();

        //Removing contacts
        rootRef.child(Constants.FIREBASE_CONTACTS_CONTAINER_NAME).child(user.getUid()).removeValue();

        //Remove chats
        final ArrayList<String> uidsChat = new ArrayList<>();
        final ArrayList<String> chatIds = new ArrayList<>();

        rootRef
                .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            uidsChat.add(ds.getKey());
                            ChatOverview chatOverview = ds.getValue(ChatOverview.class);
                            chatIds.add(chatOverview.getChatID());
                        }

                        rootRef.child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                                .child(user.getUid())
                                .removeValue();

                        for (String uid : uidsChat) {
                            rootRef
                                    .child(Constants.FIREBASE_CHATS_CONTAINER_NAME)
                                    .child(uid)
                                    .child(user.getUid()).removeValue();
                        }

                        for (String chatId : chatIds) {
                            rootRef
                                    .child(Constants.FIREBASE_MESSAGES_CONTAINER_NAME)
                                    .child(chatId)
                                    .removeValue();
                        }

                        notifyAccountActivity(true, requestCode, null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void setListener(ListenerRequestResult l) {
        listener = l;
    }

    private void notifyAccountActivity(boolean result, int requestCode, Bundle data) {
        if (listener != null)
            listener.onRequestResult(result, requestCode, data);
    }

    //EMAIL
    @Override
    public void logInWithEmail(String email, String password, final int requestCode) throws
            InvalidUsernameException,
            InvalidPasswordException,
            InvalidEmailException {

        checkCredentials(email, password, "fakeUsername");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fbUser = mAuth.getCurrentUser();
                            getUserFromDatabase(requestCode);
                        } else {
                            Bundle bundle = getBundleFromException(task.getException());
                            notifyAccountActivity(false, requestCode, bundle);
                        }
                    }
                });
    }

    @Override
    public void logOut(int requestCode) {
        user.setInstanceID("");

        usersRef.child(user.getUid())
                .setValue(GsonerUser.getGson().toJson(user, User.class));

        user = null;
        fbUser = null;

        mAuth.signOut();
    }

    //SIGN UPS
    @Override
    public void signUp(final String username, String email, String pw, final int requestCode) throws
            InvalidUsernameException,
            InvalidPasswordException,
            InvalidEmailException {

        checkCredentials(email, pw, username);

        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    fbUser = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username).build();

                    fbUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            registerUserInDatabase(createUser(), requestCode);
                        }
                    });

                } else {
                    Bundle bundle = getBundleFromException(task.getException());

                    notifyAccountActivity(false, requestCode, bundle);
                }
            }
        });
    }

    private Bundle getBundleFromException(Exception e) {
        Bundle bundle = new Bundle();
        String msg;

        try {
            throw e;
        } catch (FirebaseAuthInvalidCredentialsException i) {
            msg = act.getString(R.string.credenciales_erroneas);
        } catch (FirebaseAuthUserCollisionException i) {
            msg = act.getString(R.string.usuario_existente_normal);
        } catch (FirebaseNetworkException i) {
            msg = act.getString(R.string.error_coneccion);
        } catch (Exception i) {
            msg = e.getMessage();
        }
        bundle.putString("error", msg);
        return bundle;
    }

    /**
     * Validates the email and the password
     *
     * @param email    email
     * @param password password
     */
    private void checkCredentials(String email, String password, String username) throws InvalidEmailException, InvalidPasswordException, InvalidUsernameException {
        if (email == null || email.length() < 3 || !email.contains("@")) {
            throw new InvalidEmailException(act.getString(R.string.email_erroneo));
        }

        if (password == null || password.length() < 6) {
            throw new InvalidPasswordException(act.getString(R.string.worng_password));
        }

        if (username == null || username.length() < 4) {
            throw new InvalidUsernameException(act.getString(R.string.wrong_username));
        }

    }
}
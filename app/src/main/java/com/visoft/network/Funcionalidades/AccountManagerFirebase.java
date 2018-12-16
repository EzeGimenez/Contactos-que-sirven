package com.visoft.network.Funcionalidades;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.visoft.network.Objects.User;
import com.visoft.network.Util.Constants;
import com.visoft.network.exceptions.InvalidEmailException;
import com.visoft.network.exceptions.InvalidPasswordException;
import com.visoft.network.exceptions.InvalidUsernameException;

public class AccountManagerFirebase implements AccountManager {
    private static final int GOOGLE_SIGN_IN_INTENT = 1;

    private static AccountManager instance;
    private static Activity act;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private User user;
    private FirebaseUser fbUser;

    private GoogleSignInClient googleClient;

    private AccountManagerFirebase() {
        GoogleSignInOptions op = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("421055195921-q7vcj31bcr0lmhe9s4p9176r44ndma8m.apps.googleusercontent.com")
                .requestEmail()
                .requestId()
                .build();

        userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.FIREBASE_USERS_CONTAINER_NAME);

        googleClient = GoogleSignIn.getClient(act, op);
        mAuth = FirebaseAuth.getInstance();
    }

    public static AccountManager getInstance(Activity a) {
        if (instance == null) {
            act = a;
            instance = new AccountManagerFirebase();
        }
        return instance;
    }

    @Override
    public void logInWithGoogle() {
        Intent signInIntent = googleClient.getSignInIntent();
        act.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_INTENT);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == GOOGLE_SIGN_IN_INTENT) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {

                //Recibido un task completado conteniendo la cuenta de google
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                authWithGoogle(acc);

            } catch (ApiException ignored) {
            }
            return true;
        }
        return false;
    }

    private void authWithGoogle(GoogleSignInAccount acc) {
        AuthCredential cred = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                FirebaseUser userfb = mAuth.getCurrentUser();
                                registerUserInDatabase(createUser(userfb));
                            }

                        }
                    }
                });
    }

    private User createUser(FirebaseUser fbUser) {
        User user = new User();
        String instanceId = FirebaseInstanceId.getInstance().getToken();

        user.setUsername(fbUser.getDisplayName())
                .setRating(-1)
                .setNumberReviews(0)
                .setUid(fbUser.getUid())
                .setEmail(fbUser.getEmail())
                .setInstanceID(instanceId);

        this.user = user;
        return user;
    }

    private void registerUserInDatabase(User user) {
        userRef.child(fbUser.getUid())
                .setValue(user);
    }

    @Override
    public void logInWithFacebook() {

    }

    @Override
    public void logInWithEmail(String email, String password) throws
            InvalidUsernameException,
            InvalidPasswordException,
            InvalidEmailException {

        checkCredentials(email, password, "fakeUsername");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserFromDatabase(mAuth.getCurrentUser());
                        }
                    }
                });
    }

    private void getUserFromDatabase(FirebaseUser fbUser) {
        userRef.child(fbUser.getUid()).child("instanceID").setValue(FirebaseInstanceId.getInstance().getToken());

        userRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = (User) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void logOut() {
        user = null;
        mAuth.signOut();
    }

    @Override
    public User getCurrentAccount() {
        return user;
    }

    @Override
    public void signUp(final String username, String email, String pw) throws
            InvalidUsernameException,
            InvalidPasswordException,
            InvalidEmailException {

        checkCredentials(email, pw, username);

        mAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(act, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    registerUserInDatabase(createUser(user));
                                }
                            });
                        }
                    }
                });
    }

    /**
     * Validates the email and the password
     *
     * @param email    email
     * @param password password
     */
    private void checkCredentials(String email, String password, String username) throws InvalidEmailException, InvalidPasswordException, InvalidUsernameException {
        if (email == null || email.length() < 3 || !email.contains("@")) {
            throw new InvalidEmailException("email erroneo");
        }

        if (password == null || password.length() < 6) {
            throw new InvalidPasswordException("password erroneo");
        }

        if (username == null || username.length() < 4) {
            throw new InvalidUsernameException("asdsda");
        }

    }
}
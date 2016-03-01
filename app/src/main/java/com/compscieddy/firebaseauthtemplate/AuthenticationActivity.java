package com.compscieddy.firebaseauthtemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends BaseActivity {

  private static final Lawg lawg = Lawg.newInstance(AuthenticationActivity.class.getSimpleName());

  @Bind(R.id.email) EditText mEmail;
  @Bind(R.id.password) EditText mPassword;
  @Bind(R.id.authentication_button) Button mAuthenticationButton;
  @Bind(R.id.progress_bar) ProgressBar mProgressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authentication);
    ButterKnife.bind(this);

    mEmail.setText("test@test.com");
    mPassword.setText("password");
  }

  @OnClick(R.id.authentication_button)
  protected void authenticate() {
    mProgressBar.setVisibility(View.VISIBLE);

    final String email = mEmail.getText().toString();
    final String password = mPassword.getText().toString();

    if (!Etils.isEmailValid(email)) {
      Etils.showToast(AuthenticationActivity.this, "Invalid Email Address");
      // TODO: better special handling for invalid email address
    }

    new Firebase(Constants.FIREBASE_URL).createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
      @Override
      public void onSuccess(Map<String, Object> stringObjectMap) {
        mProgressBar.setVisibility(View.INVISIBLE);
        Etils.showToast(AuthenticationActivity.this, "Hi " + email + "! :)");

        String encodedEmail = Etils.encodeEmail(email);

        User newUser = new User(encodedEmail);
        Firebase newUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
        newUserRef.setValue(newUser);

        loginUser(email, password);
      }

      @Override
      public void onError(FirebaseError firebaseError) {
        if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
          Etils.showToast(AuthenticationActivity.this, "Email " + email + " already exists - trying to log you in instead");
          loginUser(email, password);
        } else {
          lawg.e("firebaseError: " + firebaseError + " firebaseError.getCode(): " + firebaseError.getCode());
        }
      }
    });

  }

  private void loginUser(final String email, String password) {
    new Firebase(Constants.FIREBASE_URL).authWithPassword(email, password, new Firebase.AuthResultHandler() {
      @Override
      public void onAuthenticated(AuthData authData) {
        lawg.d("Successfully authed");
        mProgressBar.setVisibility(View.INVISIBLE);
        final String encodedEmail = Etils.encodeEmail(email);

        new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            lawg.d("Logged into email: " + email + " dataSnapshot: " + dataSnapshot);
            // If it doesn't exist, just recreate it - this can happen when the database is cleared
            if (dataSnapshot.getValue() == null) {
              populateUserFirebaseData(lawg, encodedEmail);
            }

            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
          }

          @Override
          public void onCancelled(FirebaseError firebaseError) {
            lawg.e("onCancelled() while logging in firebaseError: " + firebaseError);

            mProgressBar.setVisibility(View.INVISIBLE);
            if (firebaseError.getCode() == FirebaseError.INVALID_PASSWORD) {
              Etils.showToast(AuthenticationActivity.this, "Wrong password!");
            }
          }
        });
      }

      @Override
      public void onAuthenticationError(FirebaseError firebaseError) {
        lawg.e("Authentication Error firebaseError: " + firebaseError);
      }
    });
  }

  private void populateUserFirebaseData(Lawg lawg, String encodedEmail) {
    lawg.d("Doesn't exist so repopulating this data");
    User newUser = new User(encodedEmail);
    Firebase newUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
    newUserRef.setValue(newUser);
  }

}

package com.compscieddy.firebaseauthtemplate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.compscieddy.eddie_utils.Etils;
import com.compscieddy.eddie_utils.Lawg;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by elee on 2/29/16.
 */
public class BaseActivity extends AppCompatActivity {

  private static final Lawg lawg = Lawg.newInstance(BaseActivity.class.getSimpleName());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Firebase ref = new Firebase(Constants.FIREBASE_URL);
    AuthData authData = ref.getAuth();
    if (authData == null) {
      lawg.d("User not logged in - booting them to the AuthenticationActivity");
      if (!(this instanceof AuthenticationActivity)) {
        Intent intent = new Intent(BaseActivity.this, AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    } else {
      String email = (String) authData.getProviderData().get("email");
      String encodedEmail = Etils.encodeEmail(email);
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
      sharedPreferences.edit().putString(Constants.PREF_KEY_EMAIL, encodedEmail).apply();
      if (this instanceof AuthenticationActivity) { // don't want to be redirecting if we're already on the ClockActivity
        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
      }
    }
  }

  protected String getEncodedEmail() {
    AuthData authData = new Firebase(Constants.FIREBASE_URL).getAuth();
    String email = (String) authData.getProviderData().get("email");
    String encodedEmail = Etils.encodeEmail(email);
    return encodedEmail;
  }

  protected User populateUserFirebaseData(Lawg lawg) {
    lawg.d("Doesn't exist so repopulating this data");
    String encodedEmail = getEncodedEmail();
    User newUser = new User(encodedEmail);
    Firebase newUserRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);
    newUserRef.setValue(newUser);
    return newUser;
  }

  protected void logout() {
    new Firebase(Constants.FIREBASE_URL).unauth();

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
    SharedPreferences.Editor spe = sp.edit();
//    spe.putString(Constants.KEY_PROVIDER, null);
    spe.apply();

    if (!(this instanceof AuthenticationActivity)) {
      Intent intent = new Intent(BaseActivity.this, AuthenticationActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
    }
  }

}

package com.compscieddy.firebaseauthtemplate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

/**
 * Created by elee on 2/29/16.
 */
public class BaseActivity extends AppCompatActivity {

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

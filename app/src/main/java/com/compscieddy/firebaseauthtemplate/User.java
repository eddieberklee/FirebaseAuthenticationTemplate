package com.compscieddy.firebaseauthtemplate;

import android.content.Context;

import com.compscieddy.eddie_utils.Lawg;
import com.firebase.client.Firebase;

/**
 * Created by elee on 2/8/16.
 */
public class User extends FirebaseObject {

  private static final Lawg lawg = Lawg.newInstance(User.class.getSimpleName());

  @Override
  protected String getObjectUrl() {
    return Constants.FIREBASE_URL_USERS;
  }

  @Override
  public void deleteFirebase(final Context context) {
    new Firebase(Constants.FIREBASE_URL_USERS).child(key).removeValue();
  }

  public User() {}

  public User(String encodedEmail) {
    super(encodedEmail); // encodedEmail is the key for users
  }

  public String getKey() {
    return key;
  }

  /** Methods */

}

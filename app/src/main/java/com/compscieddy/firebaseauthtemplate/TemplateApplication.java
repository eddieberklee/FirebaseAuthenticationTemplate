package com.compscieddy.firebaseauthtemplate;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by elee on 2/22/16.
 */
public class TemplateApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    /* Initialize Firebase */
    Firebase.setAndroidContext(this);
    /* Enable disk persistence  */
    Firebase.getDefaultConfig().setPersistenceEnabled(true);
  }
}

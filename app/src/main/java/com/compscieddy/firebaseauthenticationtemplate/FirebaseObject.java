package com.compscieddy.firebaseauthenticationtemplate;

import android.content.Context;
import android.text.TextUtils;

import com.compscieddy.eddie_utils.Lawg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by elee on 2/8/16.
 *   Subclasses must populate OBJECT_URL_STRING, override getKey()
 */
abstract class FirebaseObject {

  private static final Lawg lawg = Lawg.newInstance(FirebaseObject.class.getSimpleName());

  protected String key;
  protected HashMap<String, Object> timestampCreated;
  protected HashMap<String, Object> timestampModified;

  protected static final String TIMESTAMP_CREATED = "timestampCreated";
  protected static final String TIMESTAMP_MODIFIED = "timestampModified";

  protected abstract String getObjectUrl();
  public abstract void deleteFirebase(Context context);

  public FirebaseObject() {}

  public FirebaseObject(String key) {
    this.key = key;

    // TODO: Investigate can I move out these lines to the default constructor?
    HashMap<String, Object> map = new HashMap<>();
    map.put(TIMESTAMP_CREATED, ServerValue.TIMESTAMP);
    timestampCreated = map;
  }

  public String getKey() {
    return key;
  }
  public HashMap<String, Object> getTimestampCreated() {
    return timestampCreated;
  }
  public HashMap<String, Object> getTimestampModified() {
    return timestampModified;
  }
  @JsonIgnore
  public long getTimestampCreatedLong() {
    return (long) timestampCreated.get(TIMESTAMP_CREATED);
  }
  @JsonIgnore
  public long getTimestampModifiedLong() {
    return (long) timestampModified.get(TIMESTAMP_MODIFIED);
  }

  protected void updateFirebase(String FIELD, Object value) {
    // LOL cause fucking Firebase uses these same setters and I didn't read it in their documentation
    if (key == null) { // Object didn't go through FirebaseObject() so it must be the JSON Deserializer
      return;
    }
    HashMap<String, Object> map = new HashMap<>();
    map.put(FIELD, value);

    lawg.d("key:" + key + " | " + FIELD + " is being updated to " + value);

    HashMap<String, Object> timestampMap = new HashMap<>();
    timestampMap.put(TIMESTAMP_MODIFIED, ServerValue.TIMESTAMP);
    map.put(TIMESTAMP_MODIFIED, timestampMap);

    Firebase firebaseRef = new Firebase(getObjectUrl()).child(key);
    firebaseRef.updateChildren(map, new Firebase.CompletionListener() {
      @Override
      public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        if (firebaseError != null) {
          lawg.e("onComplete() trying to update but wtf firebaseError:" + firebaseError);
        }
      }
    });
  }

  /** HashMaps shouldn't be completely updated - only the keys that need to be updated should be updated
    * so data isn't clobbered.
    */
  protected void updateFirebase(String FIELD, HashMap<String, Boolean> hashMap, String hashKey) {
    boolean found = false;
    for (Object k : hashMap.keySet()) {
      if (TextUtils.equals((String) k, hashKey)) {
        found = true;
      }
    }
    if (found) { // item being added
      HashMap<String, Object> addItem = new HashMap<>();
      addItem.put(hashKey, hashMap.get(hashKey));
      new Firebase(getObjectUrl()).child(this.key).child(FIELD).updateChildren(
        addItem
      );
    } else { // item being removed
      new Firebase(getObjectUrl()).child(this.key).child(FIELD).child(hashKey).removeValue();
    }
  }

  protected void save() {
    Map<String, Object> map = new ObjectMapper().convertValue(this, Map.class);
    new Firebase(getObjectUrl()).updateChildren(map);
  }

}

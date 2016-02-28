package com.compscieddy.firebaseauthenticationtemplate;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by elee on 1/6/16.
 */
public class EddieTextView extends TextView {

  public EddieTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EddieTextView);
    int typefaceId = ta.getInt(R.styleable.EddieTextView_fontface, FontCache.MONTSERRAT_REGULAR);
    setTypeface(FontCache.get(context, typefaceId));
    ta.recycle();

  }


}

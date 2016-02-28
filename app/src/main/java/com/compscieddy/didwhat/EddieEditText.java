package com.compscieddy.didwhat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by elee on 1/7/16.
 */
public class EddieEditText extends EditText {

  public EddieEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    if (isInEditMode()) return;

    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EddieEditText);
    int typefaceId = ta.getInt(R.styleable.EddieEditText_fontface, FontCache.MONTSERRAT_REGULAR);
    setTypeface(FontCache.get(context, typefaceId));
    ta.recycle();
  }

}

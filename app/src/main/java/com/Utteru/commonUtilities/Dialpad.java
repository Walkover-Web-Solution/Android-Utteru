package com.Utteru.commonUtilities;

/**
 * Created by Rakshit on 19/02/2015.
 */


import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.internal.widget.TintEditText;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.Utteru.R;


public class Dialpad {
    int view, layout;
    EditText edittext;
    Editable editable;
    private KeyboardView mKeyboardView;
    private Activity mHostActivity;

    public Dialpad(final Activity host, int viewid, int layoutid,View v) {
        mHostActivity = host;
        view = viewid;
        layout = layoutid;
        mKeyboardView = (KeyboardView) v.findViewById(viewid);
        edittext = (EditText) v.findViewById(R.id.ed_calling_screen);
        final View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
        mKeyboardView.setPreviewEnabled(false); //  show the preview balloons
        OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

            public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
            public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
            public final static int CodeCall = -2; // Keyboard.KEYCODE_CANCEL

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
                    if (focusCurrent == null || focusCurrent.getClass() != EditText.class)
                        return;
                } else {
                    if (focusCurrent == null || focusCurrent.getClass() != TintEditText.class)
                        return;
                }
                editable = edittext.getText();
                int start = edittext.getSelectionStart();

                if (primaryCode == CodeCancel) {
                    hideDialpadOnly();
                } else if (primaryCode == CodeDelete) {
                    if (editable != null && start > 0)
                        editable.delete(start - 1, start);
                } else if (primaryCode == CodeCall) {

                } else { // insert character
                    editable.insert(start, Character.toString((char) primaryCode));
                    AudioManager am = (AudioManager) mHostActivity.getSystemService(Context.AUDIO_SERVICE);
                    am.playSoundEffect(AudioManager.FX_KEY_CLICK);
                }
            }

            @Override
            public void onPress(int primaryCode) {
            }

            @Override
            public void onRelease(int primaryCode) {
            }

            @Override
            public void onText(CharSequence text) {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeUp() {
            }
        };

        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void showDialpad(View v) {
        Keyboard keyboard = new Keyboard(mHostActivity, layout);
        mKeyboardView.setKeyboard(keyboard);
        Animation bottomUp = AnimationUtils.loadAnimation(mHostActivity,
                R.anim.bottom_up);
        mKeyboardView.setAnimation(bottomUp);
        mKeyboardView.animate();
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void hideDialpadWithAnimation() {
        Animation bottomDown = AnimationUtils.loadAnimation(mHostActivity,
                R.anim.bottom_down);

        mKeyboardView.setAnimation(bottomDown);
        mKeyboardView.animate();
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void hideDialpadOnly() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void hideKeyboard(View v) {
        if (isKeyboardVisible()) {
            Animation bottomDown = AnimationUtils.loadAnimation(mHostActivity,
                    R.anim.bottom_down);

            mKeyboardView.setAnimation(bottomDown);
            mKeyboardView.animate();
            mKeyboardView.setVisibility(View.GONE);
            mKeyboardView.setEnabled(false);
        }
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public Boolean isKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }
}

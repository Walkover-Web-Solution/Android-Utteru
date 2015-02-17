package com.Utteru.commonUtilities;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.internal.widget.TintEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Utteru.R;
import com.Utteru.utteru_sip.DialerFragment;


/**
 * When an activity hosts a keyboardView, this class allows several EditText's to register for it.
 *
 * @author Maarten Pennings
 * @date 2012 December 23
 */
public class CustomKeyboard {

    /**
     * A link to the KeyboardView that is used to render this CustomKeyboard.
     */
    private KeyboardView mKeyboardView;
    int view, layout;

    Boolean isNumpad = false;
    TextView countryName;
    TextView arrow;
    Button selectCountry;

    /**
     * A link to the activity that hosts the {@link #mKeyboardView}.
     */
    private Keyboard keyboard;
    private Activity mHostActivity;
    private boolean caps = false;

    /**
     * The key (code) handler.
     */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
        public final static int ModeChange = -2; //keyboard.KEYCODE_MODE_CHANGE
        public final static int capitals = -1; //keyboard.KEYCODE_SHIFT

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable

            Log.e("onkey", "onkey");
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();


            Log.e("view object", "" + view + " second check " + (focusCurrent.getClass().getSimpleName()));

            if (focusCurrent.getClass() != EditText.class) {
                // Do something for froyo and above versions

                if (focusCurrent == null || focusCurrent.getClass() != TintEditText.class) return;
            } else {
                // do something for phones running an SDK before froyo
                if (focusCurrent == null || focusCurrent.getClass() != EditText.class) return;
            }


            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            int n1 = 0; // -1 count
            Log.e("onkey", "onkey");
            // Apply the key to the edittext
            if (primaryCode == CodeCancel) {

                hideCustomKeyboard();
            } else if (primaryCode == ModeChange) {

                caps = false;

                if (isNumpad)
                    initNumKeypad(R.xml.numberic_keypad);
                else
                    initAlphaKeypad(R.xml.alpha_keypad);
            } else if (primaryCode == CodeDelete) {
                if (editable != null && start > 0) editable.delete(start - 1, start);

            } else if (primaryCode == capitals) {

                caps = !caps;
                keyboard.setShifted(caps);

                mKeyboardView.invalidateAllKeys();

            } else { // insert character

                if (keyboard.isShifted())
                    editable.insert(start, Character.toString((char) primaryCode).toUpperCase());
                else
                    editable.insert(start, Character.toString((char) primaryCode).toLowerCase());
            }

        }

        @Override
        public void onPress(int arg0) {
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

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link android.inputmethodservice.Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(int)}.
     *
     * @param host     The hosting activity.
     * @param viewid   The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    public CustomKeyboard(Activity host, int viewid, int layoutid) {
        mHostActivity = host;
        view = viewid;
        layout = layoutid;

        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);
        arrow = (TextView) mHostActivity.findViewById(R.id.down_arrow);
        countryName = (TextView) mHostActivity.findViewById(R.id.country_name);
        selectCountry = (Button) mHostActivity.findViewById(R.id.select_country);

        initNumKeypad(layout);

        //  keyboard = new Keyboard(mHostActivity, layoutid);
        //  mKeyboardView.setKeyboard(keyboard);
        // mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));

          mKeyboardView.setPreviewEnabled(false); //  show the preview balloons

        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initAlphaKeypad(int layoutid) {

        countryName.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        selectCountry.setVisibility(View.GONE);
        //mKeyboardView = (KeyboardView) mHostActivity.findViewById(view);
        keyboard = new Keyboard(mHostActivity, layoutid);
        mKeyboardView.setKeyboard(keyboard);
        isNumpad = true;

    }

    private void initNumKeypad(int layoutid) {

        countryName.setVisibility(View.VISIBLE);
        selectCountry.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);
        //mKeyboardView = (KeyboardView) mHostActivity.findViewById(view);
        keyboard = new Keyboard(mHostActivity, layoutid);
        mKeyboardView.setKeyboard(keyboard);
        isNumpad = false;
    }

    /**
     * Returns whether the CustomKeyboard is visible.
     */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Make the CustomKeyboard invisible.
     */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext = (EditText) mHostActivity.findViewById(resid);
        if (edittext == null) {
            Log.e("edit text is null", "edit text is null");
        } else {
            Log.e("edit text is not null", "edit text is not  null");
        }
//        // Make the custom keyboard appear
//        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
//            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                    showCustomKeyboard(v);
//                else hideCustomKeyboard();
//            }
//        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
//        edittext.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                EditText edittext = (EditText) v;
//                int inType = edittext.getInputType();       // Backup the input type
//                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
//                edittext.setFocusableInTouchMode(true);
//                edittext.onTouchEvent(event);               // Call native handler
//                edittext.setInputType(inType);              // Restore input type
//                return true; // Consume touch event
//            }
//        });

        edittext.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                //  edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        view.onTouchEvent(motionEvent);
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        return true;
                    }
                });
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

}
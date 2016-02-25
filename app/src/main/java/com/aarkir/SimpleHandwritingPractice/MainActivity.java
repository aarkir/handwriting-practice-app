package com.aarkir.SimpleHandwritingPractice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawingView mDrawingView;
    private ImageButton mDrawingButton, mSizeButton, mColorButton, mNewButton, mVisibilityButton, back, forward;
    private TextView mDisplayedText;
    private EditText mEditText;
    private boolean mVisible = true, mDrawing = true;
    private CharSequence mInvisibleDisplayedText;
    private PopupWindow mSizePopup, mColorPickerPopup;
    private LinearLayout mTopIcons;
    private List<String> mQuickList = new ArrayList<>();
    private int mCurrentIndex = -1; //represents blank list
    private SharedPreferences mSharedPreferences;
    private CheckBox mTextCheck, mPaintCheck, mButtonCheck;
    private int mButtonColor;
    //private MenuItem menuItemStrokes, menuItemDictionary;
    private String text = "";
    private final Context context = this;

    //private TextView mListIndex;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor mSharedPreferencesEditor = mSharedPreferences.edit(); //create editor to be used in onPause
        mSharedPreferencesEditor.putInt("Text Color", mDisplayedText.getCurrentTextColor());
        mSharedPreferencesEditor.putInt("Paint Color", mDrawingView.getPaintColor());
        mSharedPreferencesEditor.putInt("Button Color", mButtonColor);
        mSharedPreferencesEditor.putFloat("Brush Size", mDrawingView.getBrushSize());
        mSharedPreferencesEditor.putFloat("Text Size", mDisplayedText.getTextSize());
        mSharedPreferencesEditor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingView = (DrawingView) findViewById(R.id.drawing);

        //TypedArray a = getBaseContext().obtainStyledAttributes(new int[] {android.R.attr.selectableItemBackground});
        //mForegroundDrawable = a.getDrawable(0);
        //mForegroundDrawable.setCallback(this);
        //a.recycle();

        mDrawingButton = (ImageButton) findViewById(R.id.draw_button);
        mDrawingButton.setOnClickListener(this);

        mNewButton = (ImageButton) findViewById(R.id.new_button);
        mNewButton.setOnClickListener(this);

        back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.SierpinskiGreen));
        forward = (ImageButton) findViewById(R.id.forward);
        forward.setOnClickListener(this);
        forward.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.SierpinskiGreen));

        //mListIndex = (TextView) findViewById(R.id.listIndex);

        mDisplayedText = (TextView) findViewById(R.id.displayText);

        mTopIcons = (LinearLayout) findViewById(R.id.topIcons);

        //mLanguage = (ImageButton) findViewById(R.id.language);

        mEditText = (EditText) findViewById(R.id.editText);
        /** WHEN NEW TEXT IS DISPLAYED **/
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int keyCode, KeyEvent event) {
                text = mEditText.getText().toString();
                mInvisibleDisplayedText = text;
                mDrawingView.startNew();
                mDisplayedText.setText(text);
                //if (mLanguageOn && text.length()==1)
                //invalidateOptionsMenu();
                addToList(text);
                return false;
            }
        });

        mVisibilityButton = (ImageButton) findViewById(R.id.visibility);
        mVisibilityButton.setOnClickListener(this);

        //set values from shared preferences
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mDrawingView.setBrushSize(mSharedPreferences.getFloat("Brush Size", 30));
        mDisplayedText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSharedPreferences.getFloat("Text Size", 250));
        //mDisplayedText.setText(String.valueOf(mDisplayedText.getTextSize()));

        //mListIndex.setText(String.valueOf(mSharedPreferences.getFloat("Text Size", 250)));


        mSizeButton = (ImageButton) findViewById(R.id.sizeButton);
        mSizeButton.setOnClickListener(this);
        final View sizeDropdown = getLayoutInflater().inflate(R.layout.size_dropdown, (ViewGroup) findViewById(R.id.size_dropdown), false);
        mSizePopup = new PopupWindow(sizeDropdown, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SeekBar drawSizeSeekbar = (SeekBar) sizeDropdown.findViewById(R.id.drawSizeSeekBar);
        drawSizeSeekbar.setProgress((int) mDrawingView.getBrushSize());
        drawSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDrawingView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar textSizeSeekbar = (SeekBar) sizeDropdown.findViewById(R.id.textSizeSeekBar);
        textSizeSeekbar.setProgress((int) mDisplayedText.getTextSize());
        textSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mDisplayedText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mColorButton = (ImageButton) findViewById(R.id.colors);
        mColorButton.setOnClickListener(this);
        final View colorPickerLayout = getLayoutInflater().inflate(R.layout.color_picker, (ViewGroup) findViewById(R.id.colorPicker), false);
        mColorPickerPopup = new PopupWindow(colorPickerLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final ColorPicker colorPicker = (ColorPicker) colorPickerLayout.findViewById(R.id.picker);
        SVBar svBar = (SVBar) colorPickerLayout.findViewById(R.id.svbar);
        mTextCheck = (CheckBox) colorPickerLayout.findViewById(R.id.textBoolean);
        mButtonCheck = (CheckBox) colorPickerLayout.findViewById(R.id.buttonsBoolean);
        mPaintCheck = (CheckBox) colorPickerLayout.findViewById(R.id.paintBoolean);
        Button mResetColors = (Button) colorPickerLayout.findViewById(R.id.reset_colors);
        mResetColors.setOnClickListener(this);
        colorPicker.addSVBar(svBar);
        colorPicker.setShowOldCenterColor(false);
        colorPicker.setColor(mDrawingView.getPaintColor());
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                //int color = colorPicker.getColor();
                if (mPaintCheck.isChecked()) {
                    mDrawingView.setColor(i);
                }
                if (mButtonCheck.isChecked()) {
                    mButtonColor = i;
                    setButtonColors(i);
                }
                if (mTextCheck.isChecked()) {
                    mDisplayedText.setTextColor(i);
                }
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Disable the default and enable the custom
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View customView = getLayoutInflater().inflate(R.layout.actionbar_title, (ViewGroup) findViewById(R.id.actionbar_layout), false);
            // Get the textview of the title
            TextView customTitle = (TextView) customView.findViewById(R.id.actionbarTitle);
            // Change the font family (optional)
            //customTitle.setTypeface(Typeface.MONOSPACE);
            // Set the on click listener for the title
            customTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Inflate the about message contents
                    View messageView = getLayoutInflater().inflate(R.layout.about, (ViewGroup) findViewById(R.id.about_layout), false);

                    // When linking text, force to always use default color. This works
                    // around a pressed color state bug.
                    TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
                    int defaultColor = textView.getTextColors().getDefaultColor();
                    textView.setTextColor(defaultColor);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle(R.string.app_name);
                    builder.setView(messageView);
                    builder.create();
                    builder.show();
                    /**AlertDialog.Builder aboutWindow = new AlertDialog.Builder(context);//creates a new instance of a dialog box
                    final TextView tx = new TextView(context);//we create a textview to store the dialog text/contents
                    tx.setText("\tCharBoard Version 0.0.1\n\tBy Developer Aaron Kirtland\n\tWebsite for contact:\n\twww.aarkir.com");//we set the text/contents
                    tx.setAutoLinkMask(RESULT_OK);//to linkify any website or email links
                    tx.setTextColor(Color.BLACK);//setting the text color
                    tx.setTextSize(15);//setting the text size
                    //again to enable any website urls or email addresses to be clickable links
                    tx.setMovementMethod(LinkMovementMethod.getInstance());
                    Linkify.addLinks(tx, Linkify.WEB_URLS);

                    aboutWindow.setIcon(R.mipmap.ic_launcher);//to show the icon next to the title "About"
                    aboutWindow.setTitle("About");//set the title of the about box to say "About"
                    aboutWindow.setView(tx);//set the textview on the dialog box

                    aboutWindow.setPositiveButton("OK", new DialogInterface.OnClickListener()//creates the OK button of the dialog box
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();//when the OK button is clicked, dismiss() is called to close it
                        }
                    });
                    aboutWindow.show();//this method call will bring up the dialog box when the user calls the AboutDialog() method
                     **/
                }
            });
            // Apply the custom view
            actionBar.setCustomView(customView);
        }

        mDrawingView.setColor(mSharedPreferences.getInt("Paint Color", 0xFF000000));
        //mTextColor = mSharedPreferences.getInt("Text Color", 0xFF000000);
        mDisplayedText.setTextColor(mSharedPreferences.getInt("Text Color", ContextCompat.getColor(getApplicationContext(), android.R.color.primary_text_dark)));
        mButtonColor = mSharedPreferences.getInt("Button Color", 0xFF000000);
        setButtonColors(mButtonColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //findViewById(R.id.home).setOnClickListener(this);
        //menuItemStrokes = menu.findItem(R.id.strokes);
        //menuItemDictionary = menu.findItem(R.id.dictionary);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            //case R.id.action_settings:
            //    return true;
            //case R.id.strokes:
            //    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://commons.wikimedia.org/wiki/File:"+mDisplayedText.getText()+"-bw.png"));
            //    startActivity(browserIntent);
            //    break;
            case R.id.dictionary:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wiktionary.org/wiki/"+mDisplayedText.getText()));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        //mListIndex.setText(String.valueOf(mDrawingView.getBrushSize()) + " " + String.valueOf(mDisplayedText.getTextSize() + " " + mDrawingView.getPaintColor()));
        //respond to clicks
        switch (view.getId()) {
            case R.id.draw_button :
                if (mDrawing) { //if currently drawing
                    mDrawingButton.setImageResource(R.drawable.eraser_50);
                    mDrawingView.setErase(true); //set to erase
                    mDrawing = false;
                }
                else { //switch to drawing
                    mDrawingButton.setImageResource(R.drawable.ic_create_black_48dp);
                    mDrawingView.setErase(false);
                    mDrawing = true;
                }
                break;
            case R.id.new_button:
                mDrawingView.startNew();
                break;
            /**
            case R.id.save_button:
                mDrawingView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), mDrawingView.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery", Toast.LENGTH_SHORT);
                    savedToast.show();
                }
                else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(), "Image could not be saved", Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                mDrawingView.destroyDrawingCache();
                break;
             **/
            case R.id.visibility:
                if (mVisible) { //set invisible
                    mVisibilityButton.setImageResource(R.drawable.ic_visibility_off_black_48dp);
                    mVisible = false;
                    mInvisibleDisplayedText = mDisplayedText.getText();
                    mDisplayedText.setText("");
                }
                else { //set visible
                    mVisibilityButton.setImageResource(R.drawable.ic_visibility_black_48dp);
                    mVisible = true;
                    mDisplayedText.setText(mInvisibleDisplayedText);
                }
                break;
            case R.id.sizeButton:
                if (!mSizePopup.isShowing()) {
                    mSizePopup.setBackgroundDrawable(new ColorDrawable());
                    mSizePopup.setOutsideTouchable(true);
                    mDrawingView.setFollowingSizeChange(true);
                    mSizePopup.showAtLocation(view.getRootView(), Gravity.TOP, 0, (int) (2.5 * mTopIcons.getBottom()));
                }
                break;
            case R.id.colors:
                mColorPickerPopup.setBackgroundDrawable(new ColorDrawable());
                mColorPickerPopup.setOutsideTouchable(true);
                mDrawingView.setFollowingColorChange(true);
                mColorPickerPopup.showAtLocation(view.getRootView(), Gravity.TOP, 0, (int) (2.5 * mTopIcons.getBottom()));
                break;
            case R.id.back:
                if (mCurrentIndex > 0) {
                    mCurrentIndex--;
                    mEditText.setText("");
                    text = mQuickList.get(mCurrentIndex);
                    mDisplayedText.setText(text);
                    invalidateOptionsMenu();
                    setDirectionColors();
                }
                else {
                    Toast.makeText(MainActivity.this, "No More in List", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forward:
                if ((mQuickList.size() >= 2) && (mCurrentIndex != mQuickList.size()-1)) {
                    mCurrentIndex++;
                    mEditText.setText("");
                    text = mQuickList.get(mCurrentIndex);
                    mDisplayedText.setText(text);
                    invalidateOptionsMenu();
                    setDirectionColors();
                }
                else {
                    Toast.makeText(MainActivity.this, "No More in List", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reset_colors:
                mButtonColor = 0;
                setButtonColors(0);
                mDrawingView.setColor(0xFF000000);
                mDisplayedText.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.primary_text_dark));
                break;
            /**
            case R.id.language:
                if (mLanguageOn) {
                    mLanguageOn = false;
                    mLanguage.setImageResource(R.drawable.ic_save_black_48dp);
                }
                else { //if off
                    mLanguageOn = true;
                    mLanguage.setImageResource(R.drawable.ic_language_black_48dp);
                    displayStrokes();
                }
             **/
        }
    }

    /**
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem item = menu.findItem(R.id.strokes);
        if ((!text.equals("")) && containsAllIdeograph(text)) {
            menuItemDictionary.setVisible(true);
            //if (text.length()==1) {
            //    menuItemStrokes.setVisible(true);
            //}
            //else {
            //    menuItemStrokes.setVisible(false);
            //}
        }
        else {
            menuItemDictionary.setVisible(false);
        }
        return true;
    }
    **/

    /**
    private boolean containsAllIdeograph(String c) {
        //if (Build.VERSION.SDK_INT >= 19) {
        //    return Character.isIdeographic(c);
        //}
        //else {
        for (int i = 0; i < c.length(); i++) {
            if (!Character.UnicodeBlock.of(c.charAt(i)).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
                return false;
            }
        }
        return true;
            //return Character.UnicodeBlock.of(c).equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        //}
        //return Build.VERSION.SDK_INT < 19 || Character.isIdeographic(c);
    }
     **/

    /**
    public static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsHanScript(String s) {
        return s.codePoints().anyMatch(codepoint -> Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
    }
     **/

    /**
    private void displayLanguageButton(String text) {
        //invalidateOptionsMenu();
        mEditText.setText("outer");
        if (text.length()==1 && Character.isIdeographic(text.charAt(0))) {
            mEditText.setText("first");
            menuItem.setVisible(true);
            invalidateOptionsMenu();
            this.invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
            this.supportInvalidateOptionsMenu();
        }
        else {
            mEditText.setText("second");
            menuItem.setVisible(false);
            invalidateOptionsMenu();
            this.invalidateOptionsMenu();
            supportInvalidateOptionsMenu();
            this.supportInvalidateOptionsMenu();
        }

    }
    **/

    private void addToList(String string) {
        //if (mQuickList.size() == 0) {
        //    mQuickList.add(string);
        //}
        if ((mQuickList.size() == 0) || (!string.equals("") && !string.equals(mQuickList.get(mQuickList.size()-1)))) { //if not empty and not last element
            mQuickList.add(string);
            mCurrentIndex = mQuickList.size()-1;
            setDirectionColors();
        }
    }

    /** changes the forward and back directions depending on the current location in the list **/
    private void setDirectionColors() {
        //mListIndex.setText(String.valueOf(mCurrentIndex));
        if (mCurrentIndex <= 0) {
            back.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.SierpinskiGreen));
            //back.setBackgroundDrawable(null);
        }
        else { //if (back.getColorFilter() != null) { //if green
            back.setColorFilter(mButtonColor);
            //back.setBackgroundDrawable(mForegroundDrawable);
        }
        if (mCurrentIndex == mQuickList.size()-1) {
            forward.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.SierpinskiGreen));
            //forward.setBackgroundDrawable(null);
        }
        else { //if (back.getColorFilter() != null) { //if green
            forward.setColorFilter(mButtonColor);
            //forward.setBackgroundDrawable(mForegroundDrawable);
        }
    }

    private void setButtonColors(int i) {
        mDrawingButton.setColorFilter(i);
        mSizeButton.setColorFilter(i);
        mColorButton.setColorFilter(i);
        mNewButton.setColorFilter(i);
        mVisibilityButton.setColorFilter(i);
        back.setColorFilter(i);
        forward.setColorFilter(i);
        //mLanguage.setColorFilter(i);
        setDirectionColors();
    }
}
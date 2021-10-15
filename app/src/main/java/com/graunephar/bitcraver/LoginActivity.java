package com.graunephar.bitcraver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitcraver.R;
import com.graunephar.bitcraver.constants.AppConstants;
import com.graunephar.bitcraver.constants.UserConstant;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginActivity mActivity;
    private Button mButton;
    private TextView mTextView;
    private UserConstant mUser;
    private EditText mPasswordField;
    private int mLoginAttempts = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.mActivity = this;
        this.mButton = findViewById(R.id.login_button);
        this.mTextView = findViewById(R.id.username);
        this.mPasswordField = findViewById(R.id.password);

        Bundle bundle = getIntent().getExtras();
        if (bundle.get(AppConstants.USER_PARAM) != null) {
            mUser = (UserConstant) bundle.get(AppConstants.USER_PARAM);
            Log.d(TAG, mUser.toString());
            if (mUser == UserConstant.DANIEL) {
                mPasswordField.setVisibility(View.GONE);
                mTextView.setText(getString(R.string.welcome_message) + " " + mUser.toString() + " " + getString(R.string.logged_in_message));
                mButton.setText(R.string.button_continue);
                enableButton();
            } else {
                mTextView.setText(getString(R.string.welcome_message) + " " + mUser.toString());
            }
        }

        this.mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    //disable
                   disableButton();
                } else {
                    //enable
                    enableButton();
                }

            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordField.setText("");
                decideWhatTODoOnClick();

            }
        });
    }

    private void disableButton() {
        mButton.setEnabled(false);
        mButton.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.button_disable));
    }

    private void enableButton() {
        mButton.setEnabled(true);
        mButton.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.button_color));
    }

    private void decideWhatTODoOnClick() {
        switch (mUser) {
            case DANIEL:
                isDaniel();
                break;
            case TOBIAS:
                isTobias();
                break;
            case SARA:
                isSara();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void isSara() {
        String pass = mPasswordField.getText().toString();
        mLoginAttempts++;
        int attempts_left = AppConstants.MAX_ATTEMPTS - mLoginAttempts;

        if (pass.equals(AppConstants.SARAS_PASSOWORD) || attempts_left <= 0) {
            launchHack();
        } else {
            mPasswordField.setError(getString(R.string.wrong_password_message));
            mTextView.setText(attempts_left + " " + getString(R.string.attempts_left));
            mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.red_warning));
        }
    }

    @SuppressLint("SetTextI18n")
    private void isTobias() {
        mLoginAttempts++;
        int attempts_left = AppConstants.MAX_ATTEMPTS - mLoginAttempts;
        if (attempts_left >= 1) {
            mPasswordField.setError(getString(R.string.wrong_password_message));
            mTextView.setText(attempts_left + " " + getString(R.string.attempts_left));
            mTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.red_warning));
        } else {
          mButton.setText(R.string.reset_pass);
            enableButton();
            mPasswordField.setVisibility(View.GONE);
            mTextView.setText(getString(R.string.no_more_attempts));
            mButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  launchSecurityQuestion();
              }
          });
        }

    }

    private void isDaniel() {
        launchHack();
    }

    private void launchSecurityQuestion() {
        Intent startIntent = new Intent(mActivity, SecurityQuestionsActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, mUser);
        startActivity(startIntent);
        finish();
    }



    private void launchHack() {
        Intent startIntent = new Intent(mActivity, HackingActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, mUser);
        startActivity(startIntent);
        finish();
    }
}
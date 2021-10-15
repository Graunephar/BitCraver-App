package com.graunephar.bitcraver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class SecurityQuestionsActivity extends AppCompatActivity {

    private static final String TAG = SecurityQuestionsActivity.class.getSimpleName();
    private EditText secQuestTwoField;
    private EditText secQuestOneField;
    private EditText secQuestThreeField;
    private Button mButton;
    private SecurityQuestionsActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        mActivity = this;

        TextView secQuestOneLabel = findViewById(R.id.question_one_label);
        TextView secQuestTwoLabel = findViewById(R.id.question_two_label);
        TextView secQuestThreeLabel = findViewById(R.id.question_three_label);
        secQuestOneField = findViewById(R.id.question_one_edit);
        secQuestTwoField = findViewById(R.id.question_two_edit);
        secQuestThreeField = findViewById(R.id.question_three_edit);

        mButton = findViewById(R.id.login_button);

        secQuestOneLabel.setText(R.string.tobias_question_one);
        secQuestTwoLabel.setText(R.string.tobias_question_two);
        secQuestThreeLabel.setText(R.string.tobias_question_three);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonStateBasedOnEditFields();

            }
        };

        secQuestOneField.addTextChangedListener(watcher);
        secQuestTwoField.addTextChangedListener(watcher);
        secQuestThreeField.addTextChangedListener(watcher);



    }

    private void updateButtonStateBasedOnEditFields() {

        boolean boool = secQuestOneField.getText().toString().equals("");
        Log.e(TAG, String.valueOf(boool));
        if (!secQuestOneField.getText().toString().equals("") && !secQuestTwoField.getText().toString().equals("") && !secQuestThreeField.getText().toString().equals("")) {
            enableButton();
            Log.e(TAG, "HEJEHEJEEH");
        } else {
            disableButton();
        }
    }

    private void click() {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        String answerOne = secQuestOneField.getText().toString().toLowerCase();
        String answerTwo = secQuestTwoField.getText().toString().toLowerCase();
        String answerThree = secQuestThreeField.getText().toString().toLowerCase();

        boolean one = answerOne.equals(AppConstants.TOBIAS_ANSWER_ONE.toLowerCase());
        boolean two = answerTwo.equals(AppConstants.TOBIAS_ANSWER_TWO.toLowerCase());
        boolean three = answerThree.equals(AppConstants.TOBIAS_ANSWER_Three.toLowerCase());

        if (!one) {
            secQuestOneField.setError(getString(R.string.security_quest_error));
        }
        if (!two) {
            secQuestTwoField.setError(getString(R.string.security_quest_error));
        }
        if (!three) {
            secQuestThreeField.setError(getString(R.string.security_quest_error));
        }

        if(one && two && three) {
            launchHack();
        }
    }

    private void launchHack() {
        Intent startIntent = new Intent(mActivity, HackingActivity.class);
        startIntent.putExtra(AppConstants.USER_PARAM, UserConstant.TOBIAS); //only tobias uses this activity
        startActivity(startIntent);
        finish();
    }


    private void disableButton() {
        mButton.setEnabled(false);
        mButton.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.button_disable));
    }

    private void enableButton() {
        mButton.setEnabled(true);
        mButton.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.button_color));
    }
}
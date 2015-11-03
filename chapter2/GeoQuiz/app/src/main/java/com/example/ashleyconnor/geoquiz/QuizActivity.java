package com.example.ashleyconnor.geoquiz;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

  private Button mTrueButton;
  private Button mFalseButton;
  private ImageButton mPreviousButton;
  private ImageButton mNextButton;
  private TextView mQuestionTextView;

  private Question[] mQuestionBank = new Question[] {
      new Question(R.string.question_oceans, true), new Question(R.string.question_mideast, false),
      new Question(R.string.question_africa, false), new Question(R.string.question_americas, true),
      new Question(R.string.question_asia, true)
  };

  private int mCurrentIndex = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quiz);

    mTrueButton = (Button) findViewById(R.id.true_button);
    mFalseButton = (Button) findViewById(R.id.false_button);
    mNextButton = (ImageButton) findViewById(R.id.next_button);
    mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
    mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

    mTrueButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        checkAnswer(true);
      }
    });

    mFalseButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        checkAnswer(false);
      }
    });

    mNextButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mCurrentIndex = (mCurrentIndex + 1)
            % mQuestionBank.length; //nifty trick to prevent index out of bound errors
        updateQuestion();
      }
    });

    mPreviousButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mCurrentIndex -= 1;
        mCurrentIndex = (mCurrentIndex % mQuestionBank.length + mQuestionBank.length) % mQuestionBank.length;
        updateQuestion();
      }
    });

    mQuestionTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mCurrentIndex = (mCurrentIndex + 1)
            % mQuestionBank.length; //nifty trick to prevent index out of bound errors
        updateQuestion();
      }
    });

    updateQuestion();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_quiz, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void updateQuestion() {
    int question = mQuestionBank[mCurrentIndex].getTextResId();
    mQuestionTextView.setText(question);
  }

  private void checkAnswer(boolean userPressedTrue) {
    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

    int messageResId = 0;

    if (userPressedTrue == answerIsTrue) {
      messageResId = R.string.correct_toast;
    } else {
      messageResId = R.string.incorrect_toast;
    }

    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
  }
}

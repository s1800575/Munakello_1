package com.example.munakello;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextInput;
    private long mStartTimeInMillis;

    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCoutDownTimer;
    private boolean mTimerRunning;

    private long mTimeLeftMillies;
    private long mEndTime;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.textView);
        mEditTextInput = findViewById(R.id.edit_text_input);


        mButtonStartPause = findViewById(R.id.kaynnista);
        mButtonReset = findViewById(R.id.nollaa);
        mButtonSet = findViewById(R.id.button_set);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Syötä minuutit", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisImput = Long.parseLong(input) * 60000;
                
                if (millisImput == 0) {
                    Toast.makeText(MainActivity.this, "Syötä positiivinen luku", Toast.LENGTH_SHORT).show();
                    return;

                }
                setTime(millisImput);
                mEditTextInput.setText("");

            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }

            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();

            }
        });

    }

    private void setTime(long milliseconds){
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
            mEndTime = System.currentTimeMillis() + mTimeLeftMillies;

        mCoutDownTimer = new CountDownTimer(mTimeLeftMillies, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftMillies = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                int minutes = (int) 0;
                int seconds = (int) 0;

                String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);

                mTextViewCountDown.setText(timeLeftFormatted);
                mTimeLeftMillies = 0;
                updateButtons();
                Toast.makeText(MainActivity.this, "Munakello on valmis!", Toast.LENGTH_SHORT).show();



            }
        }.start();

        mTimerRunning = true;
        updateButtons();

    }

    public void pauseTimer() {
        mCoutDownTimer.cancel();
        mTimerRunning = false;
     updateButtons();


    }

    public void resetTimer() {
        mTimeLeftMillies = mStartTimeInMillis;
        updateCountDownText();
        updateButtons();
    }

        public void updateCountDownText() {
            int hours = (int) (mTimeLeftMillies / 1000) / 3600;
            int minutes = (int) ((mTimeLeftMillies / 1000) % 3600) / 60;
            int seconds = (int) (mTimeLeftMillies / 1000) % 60;

            String timeLeftFormatted;

            if (hours > 0) {
                timeLeftFormatted = String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
            }




            mTextViewCountDown.setText(timeLeftFormatted);

        }

        private void updateButtons(){
        if (mTimerRunning) {
            mButtonSet.setVisibility(View.INVISIBLE);
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("pauseta");

        } else {
            mButtonStartPause.setText("Kaynnista");
            mButtonSet.setVisibility(View.VISIBLE);
            mEditTextInput.setVisibility(View.VISIBLE);

            if (mTimeLeftMillies < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);

            }
            if (mTimeLeftMillies < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }

        }

        }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//  Tämä voidaan kommentoida pois, koska "hallitsevampi tallennus" tehty alempana
//  Tällä voidaan tehdä tallennus instannien välillä, kuten jos ruutua käännetään ei laskuri
//  ei nollaudu, vaan jatkaa mihin jäi ennen ruudun kääntöä jne.
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//      super.onSaveInstanceState(outState);
//        outState.putLong("millisLeft", mTimeLeftMillies);
//        outState.putBoolean("timerRunning", mTimerRunning);
//        outState.putLong("endTime", mEndTime);
//    }

//      @Override
//      protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//      super.onRestoreInstanceState(savedInstanceState);
//      mTimeLeftMillies = savedInstanceState.getLong("millisLeft");
//      mTimerRunning = savedInstanceState.getBoolean("timerRunning");
//      updateCountDownText();
//      updateButtons();
//
//        if (mTimerRunning) {
//            mEndTime = savedInstanceState.getLong("endTime");
//            mTimeLeftMillies = mEndTime - System.currentTimeMillis();
//            startTimer();
//        }
//    }

    // Tämä tallentaa käynnissä olevan prosessin tiedot myös, vaikka sovellus ei olisi aktiivinen




    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // nyt "milliesLeft" etc. kovakoodattu, näissä tulisi käyttää kovakoodaamatonta muotoa

        editor.putLong("millisLeft", mTimeLeftMillies);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.putLong("startTimeInMillis", mStartTimeInMillis);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }


    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        // katso lifecycle video vielä tässä yhteydessä

        mStartTimeInMillis = prefs.getLong("startTimeInmillis", 60000);
        mTimeLeftMillies = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateButtons();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftMillies = mEndTime - System.currentTimeMillis();

            if (mTimeLeftMillies < 0) {
                mTimeLeftMillies = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }



    }
}


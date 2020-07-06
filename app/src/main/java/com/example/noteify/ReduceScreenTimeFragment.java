package com.example.noteify;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;


public class ReduceScreenTimeFragment extends Fragment {

    private static final long START_TIME_IN_MILLIS=600000;

    TextView timer;
    Button startPause, reset;

    CountDownTimer countDownTimer;
    private boolean timerRunnning;

    private long timeLeftInMillis;
    private long endTime;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.frag_reduce_screen_time, container, false);

        startPause=view.findViewById(R.id.start_pause);
        timer=view.findViewById(R.id.timer);
        reset=view.findViewById(R.id.reset);

        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunnning){
                    pauseTimer();
                }
                else{
                    startTimer();
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });


        return view;
    }

    private void updateButtons(){
        if (timerRunnning){
            reset.setVisibility(View.INVISIBLE);
            startPause.setText("PAUSE");
        }
        else {
            startPause.setText("START");
            if (timeLeftInMillis<1000){
                startPause.setVisibility(View.INVISIBLE);
            }
            else {
                startPause.setVisibility(View.VISIBLE);
            }

            if (timeLeftInMillis<START_TIME_IN_MILLIS){
                reset.setVisibility(View.VISIBLE);
            }
            else {
                reset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void startTimer(){

        endTime=System.currentTimeMillis()+timeLeftInMillis;

        countDownTimer=new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis=millisUntilFinished;
                updateCountdownTimer();
            }

            @Override
            public void onFinish() {
                timerRunnning=false;
                updateButtons();
            }
        }.start();

        timerRunnning=true;
        updateButtons();
    }

    private void pauseTimer(){
        countDownTimer.cancel();
        timerRunnning=false;
        updateButtons();
    }

    private void resetTimer(){
        timeLeftInMillis=START_TIME_IN_MILLIS;
        updateCountdownTimer();
        updateButtons();
    }

    private void updateCountdownTimer(){
        int minutes=(int) (timeLeftInMillis/1000)/60;
        int seconds=(int) (timeLeftInMillis/1000)%60;

        String timeLeft=String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);

        timer.setText(timeLeft);
    }
    /*
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft", timeLeftInMillis);
        outState.putBoolean("timerRunning", timerRunnning);
        outState.putLong("endTime", endTime);


    }
    *//*
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("millisLeft");
            timerRunnning = savedInstanceState.getBoolean("timerRunning");


            updateCountdownTimer();
            updateButtons();

            if (timerRunnning) {
                endTime = savedInstanceState.getLong("endTime");
                timeLeftInMillis=endTime-System.currentTimeMillis();
                startTimer();
            }
        }
    }
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences timePrefs=getActivity().getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor timeEditor=timePrefs.edit();

        timeEditor.putLong("millisLeft", timeLeftInMillis);
        timeEditor.putBoolean("timerRunning", timerRunnning);
        timeEditor.putLong("endTime", endTime);

        timeEditor.apply();

        countDownTimer.cancel();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences timePrefs=getActivity().getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);

        timeLeftInMillis=timePrefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        timerRunnning=timePrefs.getBoolean("timerRunning", false);

        updateCountdownTimer();
        updateButtons();

        if (timerRunnning){
            endTime=timePrefs.getLong("endTime", 0);
            timeLeftInMillis=endTime-System.currentTimeMillis();

            if (timeLeftInMillis<0){
                timeLeftInMillis=0;
                timerRunnning=false;
                updateCountdownTimer();
                updateButtons();
            }
            else {
                startTimer();
            }
        }
    }
}
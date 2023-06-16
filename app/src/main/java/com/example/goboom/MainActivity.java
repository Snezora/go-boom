package com.example.goboom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ApplicationExitInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    int clicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void help(View view) {
        setContentView(R.layout.help_menu);
        Log.d("OUTPUT", "Help Menu is Accessed! \n  " +
                "    1. s - Start a new game\n" +
                "      2. x - Exit the game  \n" +
                "      3. d - Draw cards from deck");
    }

    public void help_backbutton(View view) {
        setContentView(R.layout.main_menu);
    }

    public void exitButton(View view) {
        clicks++;
        Toast.makeText(this, "Press Exit again to Exit", Toast.LENGTH_SHORT).show();
        if (clicks == 2) {
           finish();
        }
    }

    public void launchGame(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    public void resumeGame(View v) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("resume", true);
        startActivity(i);
    }
}
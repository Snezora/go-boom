package com.example.goboom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

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
}
package com.hrily.zbot;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hrily.zbot.utils.Dimension;
import com.hrily.zbot.utils.Point;
import com.zaxsoft.zmachine.ZCPU;
import com.zaxsoft.zmachine.ZUserInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {

    @BindView(R.id.status_bar)
    TextView statusBar;
    @BindView(R.id.screen)
    TextView screen;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.input_container)
    ScrollView scrollView;

    GameUI gameUI;
    ZCPU zcpu;
    AsyncTask<Void, Void, Void> game;
    Thread cpuThread;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        fileName = getIntent().getExtras().getString("title", "null");
        if(fileName.equals("null"))
            return;
        gameUI = new GameUI(){
            @Override
            public void setText(String message) {
                printText(this.currentScreen, message);
            }
        };
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    gameUI.input = "" + input.getText().toString();
                    printText(0, " " + gameUI.input);
                    gameUI.isEOL = true;
                    input.setText("");
                    Log.i("ACT", "input " + gameUI.input);
                    return true;
                }
                return false;
            }
        });
        zcpu = new ZCPU(gameUI);
        try {
            zcpu.initialize(getAssets().open(fileName));
            game = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    cpuThread = zcpu.start();
                    return null;
                }
            };
            game.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void printText(final int window, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(window == 0){
                    String screenText = screen.getText().toString();
                    screenText += message + "\n";
                    screen.setText(screenText);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }else{
                    Log.i("ZUI", "Status Text : " + message);
                    statusBar.setText(message);
                }
            }
        });

    }

    public Uri getUri(File file){
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            contentUri = FileProvider.getUriForFile(GameActivity.this, "com.hrily.zbot.provider", file);
        else
            contentUri = Uri.fromFile(file);
        return contentUri;
    }


}

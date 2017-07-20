package com.hrily.zbot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.hrily.zbot.adapters.ConversationAdapter;
import com.hrily.zbot.utils.Dimension;
import com.hrily.zbot.utils.Point;
import com.zaxsoft.zmachine.ZCPU;
import com.zaxsoft.zmachine.ZUserInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {

    final int MY_PERMISSION_REQUEST = 1;

    @BindView(R.id.status_bar)
    TextView statusBar;
    @BindView(R.id.title)
    TextView titleBar;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.conversation)
    RecyclerView recyclerView;
    @BindView(R.id.switch_voice_input)
    ImageView voice_input;
    @BindView(R.id.recognition_view)
    RecognitionProgressView recognitionProgressView;

    GameUI gameUI;
    ZCPU zcpu;
    AsyncTask<Void, Void, Void> game;
    Thread cpuThread;

    String title, fileName, openFileUri;
    boolean botMode = false;

    ConversationAdapter adapter;

    SpeechRecognizer speechRecognizer;
    String speechResult = null;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        if(getIntent().getExtras() == null)
            return;
        title = getIntent().getExtras().getString("title", "null");
        fileName = getIntent().getExtras().getString("fileName", "null");
        openFileUri = getIntent().getExtras().getString("fileUri", "null");
        titleBar.setText(title);
        adapter = new ConversationAdapter(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        setInputHandler();
        initGame(openFileUri.equals("null"));
        setupTTS();
        setupSpeechRecognizer();
    }

    private void setInputHandler(){
        input.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    gameUI.input = "" + input.getText().toString();
                    addChip(gameUI.input, false);
                    gameUI.isEOL = true;
                    input.setText("");
                    return true;
                }
                return false;
            }
        });
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(gameUI.status == GameUI.READING_CHAR){
                    gameUI.input_char = s.toString();
//                    input.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupSpeechRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {

            @Override
            public void onError(int error) {
                super.onError(error);
                Log.e("SPH", "Error: "+String.valueOf(error));
                recognitionProgressView.stop();
                recognitionProgressView.play();
                ButterKnife.findById(GameActivity.this, R.id.voice_input).setVisibility(View.VISIBLE);
                ButterKnife.findById(GameActivity.this, R.id.recognition_view).setVisibility(View.GONE);
            }

            @Override
            public void onResults(Bundle fullResults) {
                super.onResults(fullResults);
                recognitionProgressView.stop();
                recognitionProgressView.play();
                ButterKnife.findById(GameActivity.this, R.id.voice_input).setVisibility(View.VISIBLE);
                ButterKnife.findById(GameActivity.this, R.id.recognition_view).setVisibility(View.GONE);
                List<String> results = fullResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(results.size()<=0) return;
                if(speechResult == null)
                    adapter.conversation.add(new Pair<>(results.get(0), false));
                speechResult = results.get(0);
                adapter.conversation.set(adapter.getItemCount()-1, new Pair<>(results.get(0), false));
                adapter.notifyDataSetChanged();
                gameUI.input = speechResult;
                gameUI.isEOL = true;
                speechResult = null;
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                super.onPartialResults(partialResults);
                List<String> results = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(speechResult == null)
                    adapter.conversation.add(new Pair<>(results.get(0), false));
                speechResult = results.get(0);
                adapter.conversation.set(adapter.getItemCount()-1, new Pair<>(results.get(0), false));
                adapter.notifyDataSetChanged();
            }
        });
        recognitionProgressView.setSingleColor(getResources().getColor(R.color.blue));
        recognitionProgressView.play();
    }

    @OnClick(R.id.switch_keyboard_input)
    public void switchToKeyboard(){
        ButterKnife.findById(this, R.id.keyboard).setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.voice).setVisibility(View.GONE);
    }

    @OnClick(R.id.recognition_view)
    public void stopSpeechRecognition(){
        speechRecognizer.stopListening();
        recognitionProgressView.stop();
        recognitionProgressView.play();
        ButterKnife.findById(GameActivity.this, R.id.voice_input).setVisibility(View.VISIBLE);
        ButterKnife.findById(GameActivity.this, R.id.recognition_view).setVisibility(View.GONE);
    }

    @OnClick(R.id.voice_input)
    public void startSpeechRecognition(){
        displaySpeechRecognizer();
    }

    // Create an intent that can start the Speech Recognizer activity
    @OnClick(R.id.switch_voice_input)
    public void displaySpeechRecognizer() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSION_REQUEST);

                // MY_PERMISSION_REQUEST is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }
        ButterKnife.findById(this, R.id.keyboard).setVisibility(View.GONE);
        ButterKnife.findById(this, R.id.voice).setVisibility(View.VISIBLE);
        ButterKnife.findById(this, R.id.voice_input).setVisibility(View.GONE);
        ButterKnife.findById(this, R.id.recognition_view).setVisibility(View.VISIBLE);
        recognitionProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS
                        , 20 *1000);
                speechRecognizer.startListening(intent);
            }
        }, 50);
    }

    private void setupTTS(){
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TTS", "This Language is not supported");
                    }
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }
                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TTS", "Done");
                            if(botMode) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displaySpeechRecognizer();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                } else {
                    Log.e("TTS", "Initialization Failed!");
                }
            }
        });
    }

    private void speak(String text){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "zbot_tts");
        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "zbot_tts");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, params, "zbot_tts");
        }else{
            tts.speak(text, TextToSpeech.QUEUE_ADD, map);
        }
    }

    private void addChip(String message, boolean isCpu){
        adapter.conversation.add(new Pair<>(message, isCpu));
        adapter.notifyDataSetChanged();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        }, 50);
        if(botMode) speak(message);
    }

    @OnClick(R.id.toggle_botmode)
    public void toggleBotMode(){
        botMode = !botMode;
        if(!botMode && tts.isSpeaking())
            tts.stop();
        Toast.makeText(this, "Bot Mode " + ((botMode)?"on":"off"), Toast.LENGTH_SHORT).show();
        ((ImageView)ButterKnife.findById(this, R.id.toggle_botmode))
                .setImageResource((botMode)
                        ?R.drawable.ic_volume_up
                        :R.drawable.ic_volume_off);
    }

    private void initGame(boolean isAssetFile){
        gameUI = new GameUI(){
            @Override
            public String getFilename(String title, String suggested, boolean saveFlag) {
                if(suggested != null)
                    return getFilesDir() + fileName + "_" + suggested + ".zave";
                return getFilesDir() + fileName + ".zave";
            }

            @Override
            public void setText(String message) {
                printText(this.currentScreen, message);
            }
        };
        zcpu = new ZCPU(gameUI);
        try {
            if(isAssetFile)
                zcpu.initialize(getAssets().open(fileName));
            else {
                File file = new File(openFileUri);
                FileInputStream fileInputStream = new FileInputStream(file);
                zcpu.initialize(fileInputStream);
            }
            if(!gameUI.isInitialized)
                return;
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
                    String msg = message.replace("\n>", "");
                    addChip(msg, true);
                }else{
                    Log.i("ZUI", "Status Text : " + message);
                    statusBar.setVisibility(View.VISIBLE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    displaySpeechRecognizer();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}

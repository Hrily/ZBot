package com.hrily.zbot;

import android.util.Log;
import android.widget.TextView;

import com.hrily.zbot.utils.Dimension;
import com.hrily.zbot.utils.Point;
import com.zaxsoft.zmachine.ZUserInterface;

import java.util.Vector;

/**
 * Created by hrishi on 16/6/17.
 */

public class GameUI implements ZUserInterface{

    public static int IDLE = 0;
    public static int READING_LINE = 1;
    public static int READING_CHAR = 1;

    public int currentScreen;
    Vector terminatingCharacters; // List of terminating characters for READ operations
    public int version;

    public boolean isEOL = false;
    public String input, input_char;

    public boolean isInitialized = true;

    final int INF = 10000;

    public int status = IDLE;

    /*******************************
     *    ZUserInterface methods   *
     *******************************/

    @Override
    public void fatal(String message) {
        Log.e("ZUI", message);
        setText(message);
    }

    @Override
    public void initialize(int version) {
        // Set up the terminating characters.  Carriage Return
        // (13) is always a terminating character.  Also LF (10).
        terminatingCharacters = new Vector();
        terminatingCharacters.addElement(new Integer(13));
        terminatingCharacters.addElement(new Integer(10));

        this.version = version;

        // Depending on which storyfile version this is, we set
        // up differently.
        if ((this.version == 1) || (this.version == 2)) { // V1-2
            // For version 1-2, we set up a status bar and a
            // lower window.
            currentScreen = 0;
            return;
        }
        if (this.version == 3) { // V3
            // For V3, we set up a status bar AND two windows.
            // This all may change.
            currentScreen = 0;
            return;
        }
        if (((this.version >= 4) && (this.version <= 8)) && (this.version != 6)) {
            // V4-5,7-8; Use an upper window and a lower window.
            currentScreen = 0;
            return;
        }
        fatal("Unsupported Story File version.");
        isInitialized = false;
    }

    @Override
    public void setTerminatingCharacters(Vector characters) {
        // TODO: understand and implement this
    }

    @Override
    public boolean hasStatusLine() {
        if (version >= 3)
            return true;
        return false;
    }

    @Override
    public boolean hasUpperWindow() {
        if (version >= 3)
            return true;
        return false;
    }

    @Override
    public boolean defaultFontProportional() {
        return false;
    }

    @Override
    public boolean hasColors() {
        return true;
    }

    @Override
    public boolean hasBoldface() {
        return true;
    }

    @Override
    public boolean hasItalic() {
        return true;
    }

    @Override
    public boolean hasFixedWidth() {
        return true;
    }

    @Override
    public boolean hasTimedInput() {
        return true;
    }

    @Override
    public Dimension getScreenCharacters() {
        return new Dimension(INF, INF);
    }

    @Override
    public Dimension getScreenUnits() {
        return new Dimension(INF, INF);
    }

    @Override
    public Dimension getFontSize() {
        return new Dimension(1, 1);
    }

    @Override
    public Dimension getWindowSize(int window) {
        return new Dimension(INF, INF);
    }

    @Override
    public int getDefaultForeground() {
        return 6;
    }

    @Override
    public int getDefaultBackground() {
        return 9;
    }

    @Override
    public Point getCursorPosition() {
        return new Point(0, 0);
    }

    @Override
    public void showStatusBar(String s, int a, int b, boolean flag) {
        String status;
        String s1, s2, s3;

        s1 = " " + s + " ";
        if (flag) {
            s2 = " |  Time: " + a + ":";
            if (b < 10)
                s2 += "0";
            s2 = s2 + b;
            s3 = " ";
        }
        else {
            s2 = " | Score: " + a + " ";
            s3 = " Turns: " + b + " ";
        }

        status = "" + s1;
        status = status + s2 + s3;
        currentScreen = 1;
        setText(status);
        currentScreen = 0;
    }

    @Override
    public void splitScreen(int lines) {
        // TODO: understand and implement this
    }

    @Override
    public void setCurrentWindow(int window) {
        currentScreen = window;
    }

    @Override
    public void setCursorPosition(int x, int y) {
        // TODO: understand and implement this
    }

    @Override
    public void setColor(int foreground, int background) {
        // TODO: understand and implement this
    }

    @Override
    public void setTextStyle(int style) {
        // TODO: understand and  implement this
    }

    @Override
    public void setFont(int font) {
        // TODO: understand and implement this
    }

    @Override
    public int readLine(StringBuffer buffer, int time) {
        Log.i("ZUI", "Waiting for readLine");
        try {
            status = READING_LINE;
            while(!isEOL) Thread.sleep(100);
            buffer.append(input);
            Log.i("ZUI", "Buffer: " + input);
            input = null;
            isEOL = false;
            status = IDLE;
            return 10;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int readChar(int time) {
        Log.i("ZUI", "Waiting for readChar");
        try {
            status = READING_CHAR;
            while (input_char == null || input_char.length()!=1)
                Thread.sleep(100);
            int ch = (int) input_char.charAt(0);
            input_char = null;
            status = IDLE;
            return ch;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void showString(String s) {
        setText(s);
    }

    @Override
    public void scrollWindow(int lines) {

    }

    @Override
    public void eraseLine(int s) {

    }

    @Override
    public void eraseWindow(int window) {

    }

    @Override
    public String getFilename(String title, String suggested, boolean saveFlag) {
        return null;
    }

    @Override
    public void quit() {

    }

    @Override
    public void restart() {

    }

    public void setText(String message){

    }

}

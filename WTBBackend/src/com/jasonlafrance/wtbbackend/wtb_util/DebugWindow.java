package com.jasonlafrance.wtbbackend.wtb_util;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Jason LaFrance
 */

// Singleton class to manage a debugging text output window
// Kinda handy because it keeps debugging text out of the console output.

public class DebugWindow {

    private static DebugWindow sInstance = null;
    private final JFrame mFrame;
    private final JTextArea mTextArea;
    private final JScrollPane mScrollPane;
    private StringBuilder mSB;

    public static DebugWindow getInstance() {
        if (sInstance == null) {
            sInstance = new DebugWindow();
        }
        return sInstance;
    }

    private DebugWindow() {
        mFrame = new JFrame();
        mFrame.setTitle("Debug Window");
        mFrame.setSize(200, 600);
        mTextArea = new JTextArea();
        mTextArea.setEditable(false);
        mScrollPane = new JScrollPane(mTextArea);
        mFrame.add(mScrollPane);
        mFrame.setVisible(true);
        mSB = new StringBuilder();
    }
    
    public void addText(String in){
        mSB.append(in).append("\n");
        mTextArea.setText(mSB.toString());
    }
    
    public void clearText(){
        mSB = new StringBuilder();
        mTextArea.setText(mSB.toString());
    }
    
    public void closeWindow(){
        mFrame.setVisible(false);
    }
}

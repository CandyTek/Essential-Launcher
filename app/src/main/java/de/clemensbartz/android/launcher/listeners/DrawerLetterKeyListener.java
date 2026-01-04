package de.clemensbartz.android.launcher.listeners;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.clemensbartz.android.launcher.adapters.DrawerListAdapter;
import de.clemensbartz.android.launcher.models.ApplicationModel;

public final class DrawerLetterKeyListener implements View.OnKeyListener {
    private static final String TAG = DrawerLetterKeyListener.class.getSimpleName();
    private final AbsListView listView;
    private final DrawerListAdapter adapter;

    /** 记录每个字母当前是“中文 / 英文” */
    private final Map<Character,Boolean> letterToggleMap = new HashMap<>();

    public DrawerLetterKeyListener(AbsListView listView,
                                   DrawerListAdapter adapter) {
        this.listView = listView;
        this.adapter = adapter;
    }

    @Override
    public boolean onKey(View v,int keyCode,KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }

        char letter = keyCodeToLetter(keyCode);
        if (letter == 0 || adapter == null) {
            return false;
        }

        int position = findTargetPosition(letter);
        if (position >= 0) {
            listView.setSelection(position);
            listView.requestFocus();
            return true;
        }
        return false;
    }

    // ==========================
    // 内部实现
    // ==========================

    private char keyCodeToLetter(int keyCode) {
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            return (char) ('a' + (keyCode - KeyEvent.KEYCODE_A));
        }
        return 0;
    }

    private int findTargetPosition(char letter) {
        int chinesePos = -1;
        int englishPos = -1;

        List<ApplicationModel> list = adapter.getItems();
        if (list == null || list.isEmpty()) {
            return -1;
        }

        for (int i = 0;i < list.size();i++) {
            ApplicationModel model = list.get(i);
            if (model.hidden) {
                continue;
            }

            String label = model.label;
            if (label == null || label.isEmpty()) {
                continue;
            }
            char firstChar = Character.toLowerCase(label.charAt(0));

            // 中文：拼音首字母
            if (chinesePos == -1
                    && isChinese(firstChar)
                    && model.pinyinFirstChar == letter) {
                chinesePos = i;
            }

            // 英文
            if (englishPos == -1 && firstChar == letter) {
                englishPos = i;
            }

            if (chinesePos != -1 && englishPos != -1) {
                break;
            }
        }

        boolean toggle = letterToggleMap.containsKey(letter)
                ? letterToggleMap.get(letter)
                : false;
        letterToggleMap.put(letter,!toggle);

        if (!toggle && chinesePos != -1) {
            return chinesePos;
        }
        if (toggle && englishPos != -1) {
            return englishPos;
        }
        return chinesePos != -1 ? chinesePos : englishPos;
    }

    private int findTargetPosition2(char letter) {
        int chinesePos = -1;
        int englishPos = -1;

        List<ApplicationModel> list = adapter.getItems();
        if (list == null || list.isEmpty()) {
            return -1;
        }

        for (int i = 0;i < list.size();i++) {
            ApplicationModel model = list.get(i);
            if (model.hidden) {
                continue;
            }

            String label = model.label;
            if (label == null || label.isEmpty()) {
                continue;
            }

            char firstChar = Character.toLowerCase(label.charAt(0));

            // 中文：拼音首字母
            if (chinesePos == -1
                    && isChinese(firstChar)
                    && model.pinyinFirstChar == letter) {
                chinesePos = i;
            }

            // 英文
            if (englishPos == -1 && firstChar == letter) {
                englishPos = i;
            }

            if (chinesePos != -1 && englishPos != -1) {
                break;
            }
        }

        boolean toggle = letterToggleMap.containsKey(letter)
                ? letterToggleMap.get(letter)
                : false;
        letterToggleMap.put(letter,!toggle);

        if (!toggle && chinesePos != -1) {
            return chinesePos;
        }
        if (toggle && englishPos != -1) {
            return englishPos;
        }
        return chinesePos != -1 ? chinesePos : englishPos;
    }

    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FFF;
    }
}

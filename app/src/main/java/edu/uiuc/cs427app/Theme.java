package edu.uiuc.cs427app;

import android.annotation.SuppressLint;

import androidx.annotation.IdRes;

public interface Theme {
    /*
    * This method retrieves the theme value that user selected from signup page
    */
    static Integer getCurrentThemeSpinnerPosition(String name) {
        switch (name) {
            case "light":
                return 0;
            case "dark":
                return 1;
            case "pink":
                return 2;
            case "purple":
                return 3;
            case "green":
                return 4;
        }
        return -1;
    }

    /*
    * This method returns the theme that user selected
    */
    @SuppressLint("ResourceType")
    @IdRes
    static int getTheme(String name) {
        switch (name) {
            case "light":
                return R.style.Theme_BaseTheme;
            case "dark":
                return R.style.Theme_Dark;
            case "pink":
                return R.style.Theme_Pink;
            case "purple":
                return R.style.Theme_Purple;
            case "green":
                return R.style.Theme_Green;
        }
        return R.style.Theme_BaseTheme;
    }

    /*
    * This method returns the name of the current theme
    */
    static String getCurrentThemeName(Integer position) {
        switch (position) {
            case 0:
                return "light";
            case 1:
                return "dark";
            case 2:
                return "pink";
            case 3:
                return "purple";
            case 4:
                return "green";
        }
        return "light";
    }
}


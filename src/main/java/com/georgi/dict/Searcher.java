package com.georgi.dict;

public class Searcher {

    public static boolean subStringSearch(String word, String substring) {
        return patternSearch(substring, word);
    }

    static void prepareLPSArray(String pattern, int lps[]) {
        int patternLength = pattern.length();
        int len = 0, i = 1;
        while (i < patternLength) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len == 0) {
                    lps[i] = len;
                    i++;
                } else {
                    len = lps[len - 1];
                }
            }
        }
    }

    static boolean patternSearch(String pattern, String text) {
        int patternLength = pattern.length();
        int textLength = text.length();
        int[] lps = new int[patternLength];
        prepareLPSArray(pattern, lps);

        int i = 0;
        int j = 0;
        while (i < textLength) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == patternLength) {
                return true;
            }

            if (i < textLength && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return false;
    }
}

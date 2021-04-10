package com.georgi.dict;

import java.util.Map;
import java.util.Stack;

public class SequenceStringSearcher implements Searcher{
    @Override
    public boolean search(String word, String substring){
        return findByStack(word, substring);
    }
    private boolean findByStack(String word, String substring){
        Stack<Character> letters = new Stack<>();
        for (Character w : substring.toCharArray()){
            letters.push(w);
        }
        for (int i = word.length() - 1; i >= 0; i--){
            if (letters.peek() == word.charAt(i)){
                letters.pop();
                if (letters.empty()){
                    return true;
                }
            }
        }
        return false;
    }

}

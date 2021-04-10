package com.georgi.dict;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchAgent implements Runnable {
    private final LinkedBlockingQueue<String> words;
    int number;
    private final String substring;
    private final DictSearcher dictSearcher;
    boolean interrupted = false;
    Stack<String> returnBatch;

    public SearchAgent(int threadNumber, LinkedBlockingQueue<String> linkedBlockingQueue, String substring, DictSearcher dictSearcher) {
        this.substring = substring;
        this.number = threadNumber;
        this.words = linkedBlockingQueue;
        this.dictSearcher = dictSearcher;
        returnBatch = new Stack<>();
    }

    public void interrupt() {
        interrupted = true;
    }

    @Override
    public void run() {
        while (!interrupted) {
            try {
                String word = words.take().toLowerCase();
                if (word.equals("-1")) {
                    if (returnBatch.size() > 0){
                        if (!interrupted) dictSearcher.addToList(returnBatch);
                    }
                    return;
                }
                if (Searcher.subStringSearch(word, substring)) {
                    returnBatch.push(word);
                    if (returnBatch.size() == 10){
                        if (!interrupted) dictSearcher.addToList(returnBatch);
                        returnBatch.empty();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
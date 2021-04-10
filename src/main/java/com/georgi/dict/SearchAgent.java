package com.georgi.dict;

import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchAgent implements Runnable {
    private final LinkedBlockingQueue<String> words;
    int number;
    private final String substring;
    private final DictSearcher dictSearcher;
    Stack<String> returnBatch;
    Searcher searcher;
    boolean interrupted;

    public void interrupt(){
        interrupted = true;
    }
    public SearchAgent(int threadNumber, LinkedBlockingQueue<String> linkedBlockingQueue, String substring, DictSearcher dictSearcher, boolean type) {
        this.substring = substring;
        this.number = threadNumber;
        this.words = linkedBlockingQueue;
        this.dictSearcher = dictSearcher;
        returnBatch = new Stack<>();
        if (type) {
            searcher = new SequenceStringSearcher();

        } else {
            searcher = new SubStringSearcher();
        }
    }

    @Override
    public void run() {
        while (!interrupted) {
            try {
                String word = words.take().toLowerCase();
                if (word.equals("-1")) {
                    if (returnBatch.size() > 0) {
                        dictSearcher.addToList(returnBatch);
                    }
                    return;
                }
                if (searcher.search(word, substring)) {
                    returnBatch.push(word);
                    if (returnBatch.size() == 5) {
                        dictSearcher.addToList(returnBatch);
                        returnBatch.empty();
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
package com.georgi.dict;


import java.io.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ProcessController {
    private SearchAgent[] runnablePool;
    private final int threads;
    private final File parsedFile;
    private final DictSearcher fatherGui;
    private ParseAgent parseAgent;
    private List<Thread> executionList;
    boolean type;

    public List<Thread> getExecutionList() {
        return executionList;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public ParseAgent getParseAgent() {
        return parseAgent;
    }

    public SearchAgent[] getRunnablePool() {
        return runnablePool;
    }

    public ProcessController(File file, DictSearcher father, boolean type) {
        this.fatherGui = father;
        this.threads = Runtime.getRuntime().availableProcessors();
        this.parsedFile = file;
        this.type = type;
    }

    public void findSimilarWords(String searchedWord)  {
        if (parsedFile.exists()) {

            this.runnablePool = new SearchAgent[threads];
            LinkedBlockingQueue<String>[] queues = new LinkedBlockingQueue[threads];
            for (int i = 0; i < threads; i++) {
                queues[i] = new LinkedBlockingQueue<>();
                runnablePool[i] = new SearchAgent(i, queues[i], searchedWord, fatherGui, type);
            }

            parseAgent = new ParseAgent(parsedFile, queues, threads);

            Thread parseThread = new Thread(parseAgent);
            parseThread.start();
            executionList = new LinkedList<>();
            for (SearchAgent run : runnablePool) {
                Thread thread = new Thread(run);
                thread.start();
                executionList.add(thread);
            }

        }
    }
}

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

    public List<Thread> getExecutionList() {
        return executionList;
    }

    public ParseAgent getParseAgent() {
        return parseAgent;
    }

    public SearchAgent[] getRunnablePool() {
        return runnablePool;
    }

    public ProcessController(File file, DictSearcher father) {
        this.fatherGui = father;
        this.threads = Runtime.getRuntime().availableProcessors();
        this.parsedFile = file;
    }

    public void findSimilarWords(String searchedWord)  {
        if (parsedFile.exists()) {

            this.runnablePool = new SearchAgent[threads];
            LinkedBlockingQueue<String>[] queues = new LinkedBlockingQueue[threads];
            for (int i = 0; i < threads; i++) {
                queues[i] = new LinkedBlockingQueue<>();
                runnablePool[i] = new SearchAgent(i, queues[i], searchedWord, fatherGui);
            }

            long startTime = System.nanoTime();
            parseAgent = new ParseAgent(parsedFile, queues, threads);

            Thread parseThread = new Thread(parseAgent);
            parseThread.start();
            long endTime = System.nanoTime();
            System.out.println((endTime - startTime) / 1000000 + "ms");
            executionList = new LinkedList<>();
            for (SearchAgent run : runnablePool) {
                Thread thread = new Thread(run);
                thread.start();
                executionList.add(thread);
            }

        }
    }
}

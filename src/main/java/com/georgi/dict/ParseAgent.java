package com.georgi.dict;

import java.io.*;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class ParseAgent implements Runnable {
    boolean interrupted;
    File parsedFile;
    LinkedBlockingQueue[] queues;
    int threads;
    ParseAgent(File file, LinkedBlockingQueue[] queue, int threads) {
        parsedFile = file;
        this.queues = queue;
        this.threads = threads;

    }

    public void interrupt() {
        interrupted = true;
    }

    @Override
    public void run() {
        InputStream fis = null;
        try {
            fis = new FileInputStream(parsedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert fis != null;
        BufferedReader bf = new BufferedReader(new InputStreamReader(fis));


        String word = null;
        try {
            word = bf.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        long iter = 0;
        while (word != null) {
            if (interrupted) {
                return;
            }
            queues[(int) (iter % threads)].add(word);
            iter++;
            try {
                word = bf.readLine();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        for (int i = 0; i < threads; i++) {
            queues[i].add("-1");
        }
    }
}

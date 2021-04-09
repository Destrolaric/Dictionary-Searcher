package com.georgi.dict;

import java.io.*;

import java.util.concurrent.*;

public class ProcessController {
    private static class SearchAgent implements Runnable {
        private LinkedBlockingQueue<String> words;
        int number;
        private String substring;
        private DictSearcher dictSearcher;

        public SearchAgent(int threadNumber, LinkedBlockingQueue<String> linkedBlockingQueue, String substring, DictSearcher dictSearcher) {
            this.substring = substring;
            this.number = threadNumber;
            this.words = linkedBlockingQueue;
            this.dictSearcher = dictSearcher;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String word = words.take().toLowerCase();
                    if (word.equals("-1")) {
                        return;
                    }
                    if (Searcher.subStringSearch(word, substring)) {
                        dictSearcher.addToList(word);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    ConcurrentLinkedQueue<String> searchResult;
    private int chunksize = 500;
    private File parsedFile;
    DictSearcher fatherGui;

    public ProcessController(File file, DictSearcher father) {
        this.fatherGui = father;
        this.chunksize = Runtime.getRuntime().availableProcessors();
        this.parsedFile = file;
    }

    public void findSimilarWords(String searchedWord) throws IOException {

        if (parsedFile.exists()) {
            InputStream fis = new FileInputStream(parsedFile);
            BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
            SearchAgent threadPool[] = new SearchAgent[chunksize];
            LinkedBlockingQueue[] queues = new LinkedBlockingQueue[chunksize];
            for (int i = 0; i < chunksize; i++) {
                queues[i] = new LinkedBlockingQueue();
                threadPool[i] = new SearchAgent(i, queues[i], searchedWord, fatherGui);
            }
            String word = bf.readLine();
            long iter = 0;
            while (word != null) {
                queues[(int) (iter % chunksize)].add(word);
                iter++;
                word = bf.readLine();
            }
            for (int i = 0; i < chunksize; i++) {
                queues[i].add("-1");
                threadPool[i].run();

            }

        }
    }
}

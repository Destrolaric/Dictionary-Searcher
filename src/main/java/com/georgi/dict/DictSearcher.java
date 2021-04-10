package com.georgi.dict;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

public class DictSearcher {
    private final DictSearcher Me = this;
    private File dictionary;

    private JButton pathButton;
    private JTextField searchBar;
    private ProcessController processController;
    private JPanel Manager;
    private JCheckBox searchBySequenceCheckBox;

    //We are not using JList due to hidden size limitations, which on large values can cause exceptions;
    private JTextArea resultList;

    public void main(String[] args) {
        JFrame frame = new JFrame("DictSearcher");
        frame.setContentPane(new DictSearcher().Manager);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    public DictSearcher() {
        pathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int result = fc.showOpenDialog(Manager);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    dictionary = file;
                    processController = new ProcessController(file, Me, searchBySequenceCheckBox.isSelected());
                }
            }
        });
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });
        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        searchBySequenceCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (processController != null) {
                    processController.setType(searchBySequenceCheckBox.isSelected());
                    killSearch();
                    search();
                }
            }
        });
    }

    private void killSearch() {
        if (processController != null && processController.getRunnablePool() != null) {
            for (SearchAgent searchAgent : processController.getRunnablePool()) {
                searchAgent.interrupt();
            }
            for (Thread thread : processController.getExecutionList()) {
                thread.interrupt();
            }
            if (processController.getParseAgent() != null) {
                processController.getParseAgent().interrupt();
            }
        }
    }

    private void search() {
        killSearch();
        resultList.setText("");

        if (searchBar.getText() != null && !searchBar.getText().equals("") && dictionary != null && dictionary.exists()) {
            processController.findSimilarWords(searchBar.getText().toLowerCase());
        }
    }

    synchronized public void addToList(Stack<String> stack) throws InterruptedException {
        synchronized (this) {
            StringBuilder text = new StringBuilder(resultList.getText());
            for (String word : stack) {
                text.append(word).append(System.lineSeparator());
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    resultList.setText(text.toString());

                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }


    ;
}

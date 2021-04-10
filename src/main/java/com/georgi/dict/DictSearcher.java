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
    private JList resultList;
    private JButton pathButton;
    private JTextField searchBar;
    private ProcessController processController;

    public void main(String[] args) {
        JFrame frame = new JFrame("DictSearcher");
        frame.setContentPane(new DictSearcher().Manager);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel Manager;


    public DictSearcher() {
        pathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int result = fc.showOpenDialog(Manager);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    dictionary = file;
                    processController = new ProcessController(file, Me);
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
    }

    private void killSearch() {
        if (processController != null && processController.getRunnablePool() != null) {
            for (Thread thread : processController.getExecutionList()){
                thread.interrupt();
            }
            if (processController.getParseAgent() != null) {
                processController.getParseAgent().interrupt();
            }
        }
    }

    private void search() {
        killSearch();
        DefaultListModel listModel = (DefaultListModel) resultList.getModel();
        listModel.removeAllElements();
        resultList.setModel(listModel);
        if (searchBar.getText() != null && !searchBar.getText().equals("") && dictionary != null && dictionary.exists()) {
            processController.findSimilarWords(searchBar.getText().toLowerCase());
        }
    }

    synchronized public void addToList(Stack<String> stack) throws InterruptedException {
        DefaultListModel<String> model = (DefaultListModel) resultList.getModel();
        //This is so fast, what i need to slow it down otherwise gui will catch Out OF Bounds error due to trying to update

        for (String word : stack) {
            model.addElement(word);
        }
        resultList.setModel(model);
    }
}

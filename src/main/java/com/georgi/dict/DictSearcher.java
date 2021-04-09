package com.georgi.dict;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

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
        searchBar.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                if (searchBar.getText() != null && dictionary.exists()) {
                    try {
                        processController.findSimilarWords(searchBar.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {
                if (searchBar.getText() != null && dictionary.exists()) {
                    try {
                        processController.findSimilarWords(searchBar.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        searchBar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (searchBar.getText() != null && dictionary.exists()) {
                    try {
                        DefaultListModel listModel = (DefaultListModel) resultList.getModel();
                        listModel.removeAllElements();
                        processController.findSimilarWords(searchBar.getText());
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    public void addToList(String word) {
        System.out.println(word);
        DefaultListModel<String> model = (DefaultListModel) resultList.getModel();
        model.addElement(word);
        resultList.setModel(model);
    }
}

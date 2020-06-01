package io.fqsh.quickSort;

import com.google.inject.Singleton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class QuickSortConsolePanel {
    private JTextArea quickSortConsoleTextArea;
    private JScrollPane quickSortConsoleScrollPane;

    public JScrollPane build() {
        quickSortConsoleTextArea = new JTextArea();
        quickSortConsoleTextArea.setRows(5);
        quickSortConsoleTextArea.setEditable(false);
        quickSortConsoleTextArea.setMargin(new Insets(5, 5, 5, 5));

        quickSortConsoleScrollPane = new JScrollPane(quickSortConsoleTextArea);
        quickSortConsoleScrollPane.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(0, 2, 0, 2),
            BorderFactory.createLineBorder(new Color(184, 207, 229))
        ));

        return quickSortConsoleScrollPane;
    }

    public void write(String message) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        quickSortConsoleTextArea.append(String.format(
            "[%s] %s" + System.lineSeparator(),
            dateTimeFormatter.format(LocalDateTime.now()),
            message
        ));
        quickSortConsoleTextArea.setCaretPosition(quickSortConsoleTextArea.getDocument().getLength());
    }

    public void clearConsole() {
        quickSortConsoleTextArea.setText("");
    }
}

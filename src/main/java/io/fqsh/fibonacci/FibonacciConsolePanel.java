package io.fqsh.fibonacci;

import com.google.inject.Singleton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class FibonacciConsolePanel {
    private JTextArea fibonacciConsoleTextArea;
    private JScrollPane fibonacciConsoleScrollPane;

    public JScrollPane build() {
        fibonacciConsoleTextArea = new JTextArea();
        fibonacciConsoleTextArea.setRows(5);
        fibonacciConsoleTextArea.setEditable(false);
        fibonacciConsoleTextArea.setMargin(new Insets(5, 5, 5, 5));

        fibonacciConsoleScrollPane = new JScrollPane(fibonacciConsoleTextArea);
        fibonacciConsoleScrollPane.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(0, 2, 0, 2),
            BorderFactory.createLineBorder(new Color(184, 207, 229))
        ));

        return fibonacciConsoleScrollPane;
    }

    public void write(String message) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        fibonacciConsoleTextArea.append(String.format(
            "[%s] %s" + System.lineSeparator(),
            dateTimeFormatter.format(LocalDateTime.now()),
            message
        ));

        fibonacciConsoleTextArea.setCaretPosition(fibonacciConsoleTextArea.getDocument().getLength());
    }

    public void clearConsole() {
        fibonacciConsoleTextArea.setText("");
    }
}

package io.fqsh.hanoi;

import com.google.inject.Singleton;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class TowerOfHanoiConsolePanel {
    private JTextArea hanoiConsoleTextArea;
    private JScrollPane hanoiConsoleScrollPane;

    public JScrollPane build() {
        hanoiConsoleTextArea = new JTextArea();
        hanoiConsoleTextArea.setRows(5);
        hanoiConsoleTextArea.setEditable(false);
        hanoiConsoleTextArea.setMargin(new Insets(5, 5, 5, 5));

        hanoiConsoleScrollPane = new JScrollPane(hanoiConsoleTextArea);
        hanoiConsoleScrollPane.setBorder(new CompoundBorder(
            BorderFactory.createEmptyBorder(0, 2, 0, 2),
            BorderFactory.createLineBorder(new Color(184, 207, 229))
        ));

        return hanoiConsoleScrollPane;
    }

    public void write(String message) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        hanoiConsoleTextArea.append(String.format(
            "[%s] %s" + System.lineSeparator(),
            dateTimeFormatter.format(LocalDateTime.now()),
            message
        ));
        hanoiConsoleTextArea.setCaretPosition(hanoiConsoleTextArea.getDocument().getLength());
    }

    public void clearConsole() {
        hanoiConsoleTextArea.setText("");
    }
}

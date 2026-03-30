package com.library;

import com.library.dao.BookDAO;
import com.library.ui.LibraryGUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            Font globalFont = new Font("Segoe UI", Font.PLAIN, 16);
            UIManager.put("Label.font", globalFont);
            UIManager.put("Button.font", globalFont);
            UIManager.put("TextField.font", globalFont);
            UIManager.put("Table.font", globalFont);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 16));

            Color grayBg = new Color(240, 240, 240);
            UIManager.put("Panel.background", grayBg);
            UIManager.put("OptionPane.background", grayBg);

        } catch (Exception ignored) {}

        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) return;
            props.load(input);
        } catch (IOException ex) { return; }

        BookDAO bookDAO = new BookDAO(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );

        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI(bookDAO);
            gui.setVisible(true);
        });
    }
}
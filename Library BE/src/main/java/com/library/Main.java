package com.library;

import com.library.dao.BookDAO;
import com.library.ui.LibraryGUI;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
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

        ConfigurableApplicationContext context = new SpringApplicationBuilder(Main.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .run(args);

        BookDAO bookDAO = context.getBean(BookDAO.class);

        SwingUtilities.invokeLater(() -> {
            new LibraryGUI(bookDAO).setVisible(true);
        });
    }
}
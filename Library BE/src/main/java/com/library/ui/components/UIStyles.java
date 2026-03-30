package com.library.ui.components;

import javax.swing.*;
import java.awt.*;
import java.time.Year;

public class UIStyles {
    public static final Color BG_MAIN = new Color(245, 246, 248);
    public static final Color BG_PANEL = Color.WHITE;
    public static final Color TEXT_DARK = new Color(40, 40, 40);
    public static final Color HEADER_BG = new Color(230, 232, 235);
    public static final Color GRID_COLOR = new Color(200, 205, 210);
    public static final Color ACCENT_ROW = new Color(212, 237, 218);
    public static final Color ICON_COLOR = new Color(130, 135, 140);

    public static final Color BTN_GREEN = new Color(40, 167, 69);
    public static final Color BTN_ORANGE = new Color(253, 126, 20);
    public static final Color BTN_RED = new Color(220, 53, 69);

    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_DARK);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return label;
    }

    public static JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT_DARK);
        tf.setCaretColor(TEXT_DARK);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setPreferredSize(new Dimension(200, 40));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRID_COLOR),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return tf;
    }

    public static boolean validateBookData(String author, String title, String yearStr, String copiesStr, Component parent) {
        if (author.isBlank() || title.isBlank() || yearStr.isBlank() || copiesStr.isBlank()) {
            JOptionPane.showMessageDialog(parent, "Поля не можуть бути пустими!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (author.trim().length() < 3 || title.trim().length() < 3) {
            JOptionPane.showMessageDialog(parent, "Автор та назва мають містити мінімум 3 символи!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int year = Integer.parseInt(yearStr.trim());
            int currentYear = Year.now().getValue();
            if (year > currentYear) {
                JOptionPane.showMessageDialog(parent, "Рік видання не може бути більшим за поточний!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Рік видання має бути коректним числом!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            if (Integer.parseInt(copiesStr.trim()) < 1) {
                JOptionPane.showMessageDialog(parent, "Кількість має бути від 1 і більше!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Кількість має бути цілим числом!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static boolean validateReturnData(String author, String title, String countStr, Component parent) {
        if (author.isBlank() || title.isBlank() || countStr.isBlank()) {
            JOptionPane.showMessageDialog(parent, "Поля не можуть бути пустими!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (author.trim().length() < 3 || title.trim().length() < 3) {
            JOptionPane.showMessageDialog(parent, "Автор та назва мають містити мінімум 3 символи!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            if (Integer.parseInt(countStr.trim()) < 1) {
                JOptionPane.showMessageDialog(parent, "Кількість має бути від 1 і більше!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Кількість має бути цілим числом!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
package com.library.ui.dialogs;

import com.library.dao.BookDAO;
import com.library.ui.LibraryGUI;
import com.library.ui.components.ModernButton;
import com.library.ui.components.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class WriteOffBookDialog extends JDialog {
    public WriteOffBookDialog(LibraryGUI parent, BookDAO bookDAO, int bookId, String author, String title, int currentCopies) {
        super(parent, "Списання книги", true);
        getContentPane().setBackground(UIStyles.BG_PANEL);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 20));
        formPanel.setBackground(UIStyles.BG_PANEL);
        formPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        JTextField countField = UIStyles.createStyledTextField();
        countField.setText("1");

        formPanel.add(UIStyles.createStyledLabel("Обрана книга:"));
        formPanel.add(UIStyles.createStyledLabel("<html><b>" + author + "</b><br>" + title + "</html>"));

        formPanel.add(UIStyles.createStyledLabel("Доступно екземплярів:"));
        formPanel.add(UIStyles.createStyledLabel("<html><b>" + currentCopies + " шт.</b></html>"));

        formPanel.add(UIStyles.createStyledLabel("Кількість для списання:"));
        formPanel.add(countField);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        actionPanel.setBackground(UIStyles.BG_PANEL);
        actionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        ModernButton btnCancel = new ModernButton("Скасувати", UIStyles.BTN_RED);
        ModernButton btnSubmit = new ModernButton("Списати", UIStyles.BTN_GREEN);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> {
            try {
                int copiesToOff = Integer.parseInt(countField.getText().trim());

                if (copiesToOff < 1) {
                    JOptionPane.showMessageDialog(this, "Кількість має бути від 1 і більше", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (copiesToOff > currentCopies) {
                    JOptionPane.showMessageDialog(this, "Не можна списати більше, ніж є в наявності", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                bookDAO.writeOffBook(bookId, copiesToOff);
                dispose();
                parent.refreshData();
                parent.showSuccessMessage("Успішно списано (" + copiesToOff + " шт.)");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введіть коректне ціле число", "Помилка", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Помилка БД: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        });

        actionPanel.add(btnCancel);
        actionPanel.add(btnSubmit);

        add(formPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        pack();
        setSize(new Dimension(650, 400));
        setLocationRelativeTo(parent);
    }
}
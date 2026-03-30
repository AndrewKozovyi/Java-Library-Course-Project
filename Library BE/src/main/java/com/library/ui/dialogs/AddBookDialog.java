package com.library.ui.dialogs;

import com.library.dao.BookDAO;
import com.library.ui.LibraryGUI;
import com.library.ui.components.ModernButton;
import com.library.ui.components.UIStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class AddBookDialog extends JDialog {
    private final JTextField authorField;
    private final JTextField titleField;
    private final JTextField yearField;
    private final JPopupMenu authorPopup;
    private final JPopupMenu titlePopup;
    private final JPopupMenu yearPopup;
    private final JList<String> authorList;
    private final JList<String> titleList;
    private final JList<String> yearList;
    private final BookDAO bookDAO;
    private boolean isUpdating = false;

    public AddBookDialog(LibraryGUI parent, BookDAO bookDAO) {
        super(parent, "Додавання нової книги", true);
        this.bookDAO = bookDAO;
        getContentPane().setBackground(UIStyles.BG_PANEL);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 20));
        formPanel.setBackground(UIStyles.BG_PANEL);
        formPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        formPanel.setFocusable(true);

        authorField = UIStyles.createStyledTextField();
        titleField = UIStyles.createStyledTextField();
        yearField = UIStyles.createStyledTextField();
        JTextField copiesField = UIStyles.createStyledTextField();

        authorPopup = new JPopupMenu();
        authorPopup.setFocusable(false);
        authorList = new JList<>();
        authorList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        authorList.setFixedCellHeight(30);
        JScrollPane authorScroll = new JScrollPane(authorList);
        authorScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        authorScroll.setBorder(BorderFactory.createEmptyBorder());
        authorScroll.getVerticalScrollBar().setUnitIncrement(20);
        authorPopup.add(authorScroll);

        titlePopup = new JPopupMenu();
        titlePopup.setFocusable(false);
        titleList = new JList<>();
        titleList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        titleList.setFixedCellHeight(30);
        JScrollPane titleScroll = new JScrollPane(titleList);
        titleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        titleScroll.setBorder(BorderFactory.createEmptyBorder());
        titleScroll.getVerticalScrollBar().setUnitIncrement(20);
        titlePopup.add(titleScroll);

        yearPopup = new JPopupMenu();
        yearPopup.setFocusable(false);
        yearList = new JList<>();
        yearList.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        yearList.setFixedCellHeight(30);
        JScrollPane yearScroll = new JScrollPane(yearList);
        yearScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        yearScroll.setBorder(BorderFactory.createEmptyBorder());
        yearScroll.getVerticalScrollBar().setUnitIncrement(20);
        yearPopup.add(yearScroll);

        setupAutocomplete(authorField, authorPopup, authorList, "AUTHOR");
        setupAutocomplete(titleField, titlePopup, titleList, "TITLE");
        setupAutocomplete(yearField, yearPopup, yearList, "YEAR");

        formPanel.add(UIStyles.createStyledLabel("Прізвище та ініціали автора:"));
        formPanel.add(authorField);
        formPanel.add(UIStyles.createStyledLabel("Назва книги:"));
        formPanel.add(titleField);
        formPanel.add(UIStyles.createStyledLabel("Рік видання:"));
        formPanel.add(yearField);
        formPanel.add(UIStyles.createStyledLabel("Кількість екземплярів:"));
        formPanel.add(copiesField);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        actionPanel.setBackground(UIStyles.BG_PANEL);
        actionPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        ModernButton btnCancel = new ModernButton("Скасувати", UIStyles.BTN_RED);
        ModernButton btnSubmit = new ModernButton("Додати", UIStyles.BTN_GREEN);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> {
            String author = authorField.getText();
            String title = titleField.getText();
            String year = yearField.getText();
            String copies = copiesField.getText();

            if (UIStyles.validateBookData(author, title, year, copies, this)) {
                try {
                    bookDAO.addBook(author.trim(), title.trim(), Integer.parseInt(year.trim()), Integer.parseInt(copies.trim()));
                    dispose();
                    parent.refreshData();
                    parent.showSuccessMessage("Нову книгу успішно додано");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Помилка бази даних: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(btnCancel);
        actionPanel.add(btnSubmit);

        add(formPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        pack();
        setSize(new Dimension(650, 420));
        setLocationRelativeTo(parent);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                formPanel.requestFocusInWindow();
            }
        });
    }

    private void setupAutocomplete(JTextField field, JPopupMenu popup, JList<String> list, String type) {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFocusable(false);

        JScrollPane scroll = (JScrollPane) popup.getComponent(0);
        scroll.setFocusable(false);
        scroll.getVerticalScrollBar().setFocusable(false);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (list.getSelectedValue() != null) {
                    isUpdating = true;
                    field.setText(list.getSelectedValue());
                    popup.setVisible(false);
                    isUpdating = false;
                }
            }
        });

        Runnable updateSuggestions = () -> {
            if (isUpdating) return;
            SwingUtilities.invokeLater(() -> {
                try {
                    String text = field.getText().trim();
                    List<String> suggestions = new ArrayList<>();

                    if (type.equals("AUTHOR")) {
                        suggestions = bookDAO.getAuthorsByPrefixAndTitle(text, titleField.getText().trim());
                    } else if (type.equals("TITLE")) {
                        suggestions = bookDAO.getTitlesByPrefixAndAuthor(text, authorField.getText().trim());
                    } else if (type.equals("YEAR")) {
                        for (int i = 2026; i >= 0; i--) {
                            String yearStr = String.valueOf(i);
                            if (yearStr.startsWith(text)) {
                                suggestions.add(yearStr);
                            }
                        }
                    }

                    if (suggestions.isEmpty()) {
                        popup.setVisible(false);
                    } else {
                        list.setListData(suggestions.toArray(new String[0]));
                        int height = Math.min(suggestions.size() * 30 + 5, 150);

                        scroll.setPreferredSize(new Dimension(field.getWidth(), height));
                        popup.pack();

                        if (field.hasFocus()) {
                            popup.setVisible(false);
                            popup.show(field, 0, field.getHeight());
                            field.requestFocus();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        };

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSuggestions.run(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateSuggestions.run(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSuggestions.run(); }
        });

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateSuggestions.run();
            }

            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (focusOwner != null && !SwingUtilities.isDescendingFrom(focusOwner, popup)) {
                        popup.setVisible(false);
                    }
                });
            }
        });

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateSuggestions.run();
            }
        });

        MouseWheelListener wheelListener = e -> {
            if (popup.isVisible()) {
                JScrollBar bar = scroll.getVerticalScrollBar();
                int direction = e.getWheelRotation();
                bar.setValue(bar.getValue() + (direction * bar.getUnitIncrement()));
                e.consume();
            }
        };

        field.addMouseWheelListener(wheelListener);
        list.addMouseWheelListener(wheelListener);
        scroll.addMouseWheelListener(wheelListener);
        popup.addMouseWheelListener(wheelListener);
    }
}
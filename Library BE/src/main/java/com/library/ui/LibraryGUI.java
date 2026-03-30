package com.library.ui;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.ui.components.ModernButton;
import com.library.ui.components.UIStyles;
import com.library.ui.dialogs.AddBookDialog;
import com.library.ui.dialogs.ReturnBookDialog;
import com.library.ui.dialogs.WriteOffBookDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibraryGUI extends JFrame {
    private final BookDAO bookDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel totalLabel;

    public LibraryGUI(BookDAO bookDAO) {
        super("Система обліку книг у бібліотеці");
        this.bookDAO = bookDAO;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIStyles.BG_MAIN);

        String[] columns = {"№", "ID", "Автор", "Назва", "Рік видання", "Екземплярів"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 4 || columnIndex == 5) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setBackground(UIStyles.BG_PANEL);
        table.setForeground(UIStyles.TEXT_DARK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(45);
        table.setShowGrid(true);
        table.setGridColor(UIStyles.GRID_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(UIStyles.ACCENT_ROW);
        table.setSelectionForeground(Color.BLACK);
        table.setFocusable(false);

        DefaultTableCellRenderer indexRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setText(String.valueOf(row + 1));
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        };

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return this;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 0) {
                table.getColumnModel().getColumn(i).setCellRenderer(indexRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING)));

        sorter.setSortable(0, false);

        table.getTableHeader().setBackground(UIStyles.HEADER_BG);
        table.getTableHeader().setForeground(UIStyles.TEXT_DARK);
        table.getTableHeader().setPreferredSize(new Dimension(100, 50));
        table.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        table.getTableHeader().setReorderingAllowed(false);

        TableCellRenderer headerRenderer = new TableCellRenderer() {
            private final JPanel panel = new JPanel(new BorderLayout());
            private final JLabel textLabel = new JLabel();
            private final JLabel iconLabel = new JLabel();

            {
                panel.add(textLabel, BorderLayout.CENTER);
                panel.add(iconLabel, BorderLayout.EAST);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                panel.setBackground(UIStyles.HEADER_BG);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 1, UIStyles.GRID_COLOR),
                        new EmptyBorder(0, 15, 0, 15)
                ));

                textLabel.setText(value != null ? value.toString() : "");
                textLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                textLabel.setForeground(UIStyles.TEXT_DARK);

                iconLabel.setText("");
                iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                iconLabel.setForeground(UIStyles.ICON_COLOR);

                if (column != 0 && table.getRowSorter() != null) {
                    java.util.List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
                    if (!sortKeys.isEmpty() && sortKeys.get(0).getColumn() == column) {
                        SortOrder sortOrder = sortKeys.get(0).getSortOrder();
                        if (sortOrder == SortOrder.ASCENDING) {
                            iconLabel.setText("▲");
                        } else if (sortOrder == SortOrder.DESCENDING) {
                            iconLabel.setText("▼");
                        }
                    }
                }

                if (column == 0) {
                    textLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, UIStyles.GRID_COLOR));
                } else {
                    textLabel.setHorizontalAlignment(SwingConstants.LEFT);
                }

                return panel;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.GRID_COLOR));
        scrollPane.getViewport().setBackground(UIStyles.BG_MAIN);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(UIStyles.BG_MAIN);
        tableContainer.setBorder(new EmptyBorder(20, 20, 0, 20));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIStyles.BG_MAIN);
        southPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        ModernButton btnAddNew = new ModernButton("Додати нову", UIStyles.BTN_GREEN);
        ModernButton btnReturn = new ModernButton("Повернути книгу", UIStyles.BTN_ORANGE);
        ModernButton btnDelete = new ModernButton("Списати книгу", UIStyles.BTN_RED);

        buttonPanel.add(btnAddNew);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnDelete);

        buttonPanel.add(Box.createHorizontalStrut(20));
        totalLabel = new JLabel("Всього записів: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(UIStyles.TEXT_DARK);
        buttonPanel.add(totalLabel);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(UIStyles.BTN_GREEN);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        southPanel.add(buttonPanel, BorderLayout.WEST);
        southPanel.add(statusLabel, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);

        btnAddNew.addActionListener(e -> new AddBookDialog(this, bookDAO).setVisible(true));
        btnReturn.addActionListener(e -> new ReturnBookDialog(this, bookDAO).setVisible(true));
        btnDelete.addActionListener(e -> openWriteOffDialog());
    }

    private void openWriteOffDialog() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Оберіть рядок у таблиці для списання", "Увага", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        int bookId = (int) tableModel.getValueAt(modelRow, 1);
        String author = (String) tableModel.getValueAt(modelRow, 2);
        String title = (String) tableModel.getValueAt(modelRow, 3);
        int currentCopies = (int) tableModel.getValueAt(modelRow, 5);

        new WriteOffBookDialog(this, bookDAO, bookId, author, title, currentCopies).setVisible(true);
    }

    public void showSuccessMessage(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(4000, e -> statusLabel.setText(""));
        timer.setRepeats(false);
        timer.start();
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Book> books = bookDAO.getAllBooks();
            for (Book book : books) {
                tableModel.addRow(new Object[]{
                        0,
                        book.getId(), book.getAuthor(), book.getTitle(),
                        book.getYear(), book.getCopies()
                });
            }
            totalLabel.setText("Всього різновидів книг: " + books.size());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження: " + ex.getMessage());
        }
    }
}
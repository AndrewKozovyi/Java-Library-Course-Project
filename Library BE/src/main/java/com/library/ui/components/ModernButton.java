package com.library.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    private final Color normalColor;
    private final Color hoverColor;
    private final Color pressedColor;

    public ModernButton(String text, Color bgColor) {
        super(text);
        this.normalColor = bgColor;
        this.hoverColor = bgColor.darker();
        this.pressedColor = bgColor.darker().darker();

        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(180, 45));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(hoverColor); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(normalColor); }
            @Override
            public void mousePressed(MouseEvent e) { setBackground(pressedColor); }
            @Override
            public void mouseReleased(MouseEvent e) { setBackground(hoverColor); }
        });
        setBackground(normalColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        super.paintComponent(g);
        g2.dispose();
    }
}
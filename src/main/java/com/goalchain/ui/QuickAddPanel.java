package com.goalchain.ui;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class QuickAddPanel extends JPanel
{
    private final FlatTextField inputField;
    private final JButton addButton;

    public QuickAddPanel(Consumer<String> onSubmit)
    {
        setLayout(new BorderLayout(5, 0)); // Horizontal layout with small spacing
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Input field
        inputField = new FlatTextField();
        inputField.setHoverBackgroundColor(ColorScheme.DARKER_GRAY_COLOR);
        inputField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        inputField.setPreferredSize(new Dimension(0, 12)); // Flexible width, fixed height

        // Trigger add on Enter key
        inputField.addActionListener(e -> submit(onSubmit));

        // Add button
        addButton = new JButton("+");
        addButton.setFocusPainted(false);
        addButton.setFont(addButton.getFont().deriveFont(Font.BOLD, 16f));
        addButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(40, 30));
        addButton.setBorder(BorderFactory.createLineBorder(ColorScheme.DARK_GRAY_COLOR));
        addButton.setToolTipText("Add Task");

        addButton.addActionListener((ActionEvent e) -> submit(onSubmit));

        // Assemble panel
        add(inputField, BorderLayout.CENTER);
        add(addButton, BorderLayout.EAST);
    }

    private void submit(Consumer<String> onSubmit)
    {
        final String text = inputField.getText().trim();
        if (!text.isEmpty())
        {
            onSubmit.accept(text);
            inputField.setText("");
        }
    }
}

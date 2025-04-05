// main/java/com/goalchain/ui/InactiveListPanel.java
package com.goalchain.ui;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager; // Import GoalManager
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager; // For consistent font styles

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Use specific import
import java.awt.*;
import java.util.List; // Use List interface

@Slf4j // Add logging
public class InactiveListPanel extends JPanel {

    private final JPanel goalListContainer;
    private final GoalManager goalManager; // Store GoalManager (optional, depends if needed here)
    private final Runnable refreshCallback; // Store refresh callback (optional, depends if needed here)

    // Accept GoalManager and Runnable, though they might not be directly used
    // by this panel if it's purely display. Still good practice for consistency.
    public InactiveListPanel(GoalManager goalManager, Runnable refreshCallback) {
        log.info("Building InactiveListPanel");
        this.goalManager = goalManager;
        this.refreshCallback = refreshCallback;

        // --- Layout similar to other list panels ---
        setLayout(new BorderLayout(0, 5)); // Add small vertical gap
        setBackground(ColorScheme.DARKER_GRAY_COLOR); // Consistent background
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding

        // Header
        JLabel header = new JLabel("Inactive / Pending Tasks");
        header.setForeground(Color.WHITE);
        header.setFont(FontManager.getRunescapeBoldFont()); // Use consistent bold font
        // Add a bottom border to the header for separation
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR.brighter()), // Bottom line
                new EmptyBorder(0, 0, 5, 0) // Padding below the line
        ));
        add(header, BorderLayout.NORTH);

        // Container for the goal labels
        goalListContainer = new JPanel();
        goalListContainer.setLayout(new BoxLayout(goalListContainer, BoxLayout.Y_AXIS));
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR); // Match background

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(goalListContainer);
        scrollPane.setBorder(null); // No border for the scroll pane itself
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        // Style the scrollbar
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        scrollPane.getVerticalScrollBar().setBackground(ColorScheme.DARK_GRAY_COLOR);
        // scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() { ... }); // For more custom scrollbar

        add(scrollPane, BorderLayout.CENTER);
        // --- End Layout ---

        // Initial population - get inactive goals from manager
        // Note: GoalManager needs to be passed if you fetch directly here.
        // Otherwise, GoalChainPluginPanel will call refresh externally.
        // refresh(goalManager.getInactiveGoals()); // Can be done here or externally
    }

    // Refresh the list display
    public void refresh(List<Goal> inactiveGoals) {
        log.info("Refreshing InactiveListPanel with {} goals", inactiveGoals.size());
        goalListContainer.removeAll(); // Clear previous items

        if (inactiveGoals.isEmpty()) {
            // Display a message when the list is empty
            JLabel emptyLabel = new JLabel("No inactive tasks");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setForeground(ColorScheme.MEDIUM_GRAY_COLOR); // Dimmer text color
            // Add padding around the empty label
            emptyLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
            goalListContainer.add(emptyLabel);
        } else {
            // Add labels for each inactive goal
            for (Goal goal : inactiveGoals) {
                JPanel goalPanel = new JPanel(new BorderLayout()); // Use a panel for each goal for better spacing/borders
                goalPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                goalPanel.setBorder(new EmptyBorder(2, 5, 2, 5)); // Padding within the goal item

                JLabel goalLabel = new JLabel(goal.getText());
                goalLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR); // Slightly dimmer text for inactive
                goalLabel.setToolTipText(goal.getText() + " (Inactive - Prerequisites not met)");
                // Optional: Add indication of prerequisites if needed
                // goalLabel.setText(goal.getText() + " (Requires: " + String.join(", ", goal.getPrerequisiteIds()) + ")");

                goalPanel.add(goalLabel, BorderLayout.CENTER);
                // Optional: Add icons or buttons if needed later

                goalListContainer.add(goalPanel);
                // goalListContainer.add(Box.createVerticalStrut(1)); // Minimal spacing between items
            }
        }

        // Important: Revalidate and repaint the container
        goalListContainer.revalidate();
        goalListContainer.repaint();
        // Also repaint the scrollpane viewport in case scrollbar visibility changes
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, goalListContainer);
            if (scrollPane != null) {
                scrollPane.getViewport().revalidate();
                scrollPane.getViewport().repaint();
            }
        });
    }
}
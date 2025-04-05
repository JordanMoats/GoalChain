// main/java/com/goalchain/ui/CompletedListPanel.java
package com.goalchain.ui;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager; // Import GoalManager
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.List; // Use List interface

@Slf4j // Add logging
public class CompletedListPanel extends JPanel {

    private final JPanel goalListContainer;
    private final GoalManager goalManager;
    private final Runnable refreshCallback;

    // Accept GoalManager and Runnable
    public CompletedListPanel(GoalManager goalManager, Runnable refreshCallback) {
        log.info("Building CompletedListPanel");
        this.goalManager = goalManager;
        this.refreshCallback = refreshCallback;

        // --- Layout similar to ActiveListPanel ---
        setLayout(new BorderLayout(0, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR); // Consistent background
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel header = new JLabel("Completed Tasks");
        header.setForeground(Color.WHITE); // White text
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        add(header, BorderLayout.NORTH);

        goalListContainer = new JPanel();
        goalListContainer.setLayout(new BoxLayout(goalListContainer, BoxLayout.Y_AXIS));
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JScrollPane scrollPane = new JScrollPane(goalListContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        scrollPane.getVerticalScrollBar().setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(scrollPane, BorderLayout.CENTER);
        // --- End Layout ---

        // Initial population
        refresh(goalManager.getCompletedGoals());
    }

    // Refresh the list display
    public void refresh(List<Goal> completedGoals) {
        log.info("Refreshing CompletedListPanel with {} goals", completedGoals.size());
        goalListContainer.removeAll();

        if (completedGoals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No completed tasks");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            goalListContainer.add(emptyLabel);
        } else {
            for (Goal goal : completedGoals) {
                // We can reuse ActiveGoalPanel, or create a dedicated CompletedGoalPanel
                // if styling needs to differ significantly (e.g., strikethrough text).
                // For now, let's reuse ActiveGoalPanel. The listener logic handles the state change.
                ActiveGoalPanel taskPanel = new ActiveGoalPanel(goal, goalManager, refreshCallback);
                // Optional: Style completed tasks differently (e.g., grayed out, strikethrough)
                // JLabel textLabel = (JLabel) taskPanel.getComponent(1); // Assuming label is the second component
                // Font f = textLabel.getFont();
                // textLabel.setFont(f.deriveFont(f.getStyle() | Font.ITALIC)); // Italic example
                // textLabel.setForeground(Color.GRAY);
                goalListContainer.add(taskPanel);
                goalListContainer.add(Box.createVerticalStrut(3));
            }
        }

        goalListContainer.revalidate();
        goalListContainer.repaint();
    }
}
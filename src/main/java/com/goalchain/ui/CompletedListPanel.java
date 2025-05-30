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

    // Private static inner class that handles scrolling behavior (copied from ActiveListPanel)
    private static class ScrollableGoalContainer extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16 * 10;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

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
        header.setForeground(Color.LIGHT_GRAY);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(header, BorderLayout.NORTH);

        // Instantiate the custom scrollable panel
        goalListContainer = new ScrollableGoalContainer();
        goalListContainer.setLayout(new BoxLayout(goalListContainer, BoxLayout.Y_AXIS));
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JScrollPane scrollPane = new JScrollPane(goalListContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        scrollPane.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        scrollPane.getVerticalScrollBar().setBackground(ColorScheme.DARK_GRAY_COLOR);

        add(scrollPane, BorderLayout.CENTER);
        // --- End Layout ---

        // Initial population
        refresh(goalManager.getCompletedGoals());
    }

    // Refresh the list display
    public void refresh(List<Goal> completedGoals) {
        goalListContainer.removeAll();
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

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
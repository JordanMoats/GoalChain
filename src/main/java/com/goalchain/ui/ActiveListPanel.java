// main/java/com/goalchain/ui/ActiveListPanel.java
package com.goalchain.ui;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager; // Import GoalManager
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.List; // Use List interface

@Slf4j
public class ActiveListPanel extends JPanel
{
    private final JPanel goalListContainer;
    private final GoalManager goalManager; // Store GoalManager
    private final Runnable refreshCallback; // Store refresh callback

    // Private static inner class that handles scrolling behavior
    private static class ScrollableGoalContainer extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16; // Standard unit scroll increment
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16 * 10; // Standard block scroll increment
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            // Force the panel's width to match the viewport's width.
            // This prevents horizontal scrolling and causes components (like GoalPanels)
            // to wrap or truncate their content based on the available width.
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            // Allow the panel's height to be determined by its content (GoalPanels).
            // This enables vertical scrolling when the content exceeds the viewport height.
            return false;
        }
    }

    // Accept GoalManager and Runnable in constructor
    public ActiveListPanel(GoalManager goalManager, Runnable refreshCallback)
    {
        log.info("Building ActiveListPanel");
        this.goalManager = goalManager;
        this.refreshCallback = refreshCallback;

        // --- Existing Layout Code ---
        setLayout(new BorderLayout(0, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel header = new JLabel("Active Tasks");
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
        // --- End Existing Layout Code ---

        // Initial population - get active goals from manager
        refresh(goalManager.getActiveGoals());
    }

    // Refresh the list display
    public void refresh(List<Goal> activeGoals)
    {
        goalListContainer.removeAll();
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        if (activeGoals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No active tasks");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
            goalListContainer.add(emptyLabel);
        } else {
            for (Goal goal : activeGoals)
            {
                // Pass GoalManager and refreshCallback to each ActiveGoalPanel
                ActiveGoalPanel taskPanel = new ActiveGoalPanel(goal, goalManager, refreshCallback);
                goalListContainer.add(taskPanel);
                goalListContainer.add(Box.createVerticalStrut(3)); // small spacing
            }
        }


        goalListContainer.revalidate();
        goalListContainer.repaint();
    }
}
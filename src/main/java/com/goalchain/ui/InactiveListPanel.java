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
        header.setForeground(ColorScheme.LIGHT_GRAY_COLOR); // Less bright color
        header.setFont(FontManager.getRunescapeBoldFont());
        // Add consistent padding
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(header, BorderLayout.NORTH);

        // Instantiate the custom scrollable panel
        goalListContainer = new ScrollableGoalContainer();
        goalListContainer.setLayout(new BoxLayout(goalListContainer, BoxLayout.Y_AXIS));
        // Ensure container background is explicitly dark
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(goalListContainer);
        scrollPane.setBorder(null);
        // Ensure scroll pane background is explicitly dark
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        scrollPane.getViewport().setBackground(ColorScheme.DARKER_GRAY_COLOR); // Also set viewport background
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
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR); // Re-apply background after removeAll if needed

        if (inactiveGoals.isEmpty()) {
            // Display a message when the list is empty
            JLabel emptyLabel = new JLabel("No inactive tasks");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setForeground(ColorScheme.MEDIUM_GRAY_COLOR); // Dimmer text color
            // Add padding around the empty label
            emptyLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
            goalListContainer.add(emptyLabel);
        } else {
            // Add an InactiveGoalPanel for each inactive goal
            for (Goal goal : inactiveGoals) {
                // Create the specific panel for inactive goals
                InactiveGoalPanel goalPanel = new InactiveGoalPanel(goal, goalManager, refreshCallback);
                goalListContainer.add(goalPanel);
                // goalListContainer.add(Box.createVerticalStrut(1)); // Optional spacing
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
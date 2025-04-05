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
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        add(header, BorderLayout.NORTH);

        goalListContainer = new JPanel();
        goalListContainer.setLayout(new BoxLayout(goalListContainer, BoxLayout.Y_AXIS));
        goalListContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR); // Match background

        JScrollPane scrollPane = new JScrollPane(goalListContainer);
        scrollPane.setBorder(null); // Remove scrollpane border
        scrollPane.setBackground(ColorScheme.DARKER_GRAY_COLOR); // Match background
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, Integer.MAX_VALUE)); // Slightly wider scrollbar
        scrollPane.getVerticalScrollBar().setBackground(ColorScheme.DARK_GRAY_COLOR); // Scrollbar background

        add(scrollPane, BorderLayout.CENTER);
        // --- End Existing Layout Code ---

        // Initial population - get active goals from manager
        refresh(goalManager.getActiveGoals());
    }

    // Refresh the list display
    public void refresh(List<Goal> activeGoals)
    {
        log.info("Refreshing ActiveListPanel with {} goals", activeGoals.size());
        goalListContainer.removeAll();

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
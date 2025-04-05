package com.goalchain.ui;

import com.goalchain.data.GoalManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class GoalChainPluginPanel extends PluginPanel {

    private final GoalManager goalManager;
    private final ActiveListPanel activeListPanel;
    private final InactiveListPanel inactivePanel;
    private final CompletedListPanel completedPanel;

    public GoalChainPluginPanel(GoalManager goalManager)
    {
        super();
        getParent().setLayout(new BorderLayout());
        getParent().add(this, BorderLayout.CENTER);
        setLayout(new BorderLayout());

        this.goalManager = goalManager;

        // Quick Add Panel
        QuickAddPanel quickAddPanel = new QuickAddPanel(goalText -> {
            goalManager.quickAddGoal(goalText);
            refreshGoals();
        });

        // List Panels
        activeListPanel = new ActiveListPanel(goalManager, this::refreshGoals);
        inactivePanel = new InactiveListPanel(goalManager, this::refreshGoals);
        completedPanel = new CompletedListPanel(goalManager, this::refreshGoals);

        JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inactivePanel, completedPanel);
        innerSplitPane .setResizeWeight(0.5);
        innerSplitPane.setOneTouchExpandable(true);
        innerSplitPane.setBackground(ColorScheme.BORDER_COLOR);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, activeListPanel, innerSplitPane);
        mainSplitPane.setResizeWeight(0.33);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setBackground(ColorScheme.BORDER_COLOR);

        add(quickAddPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);

        log.info("Panel Initialized.");
        SwingUtilities.invokeLater(this::refreshGoals);
    }

    private void refreshGoals() {
        // Rebuild Active Goal
        activeListPanel.refresh(goalManager.getActiveGoals());
        inactivePanel.refresh(goalManager.getInactiveGoals());
        completedPanel.refresh(goalManager.getCompletedGoals());
    }

}
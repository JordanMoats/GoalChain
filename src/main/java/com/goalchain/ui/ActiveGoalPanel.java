package com.goalchain.ui;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.JCheckBox;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ActiveGoalPanel extends JPanel {
    // Consider loading icons only once if possible, e.g., static final fields
    final BufferedImage icon1 = ImageUtil.loadImageResource(getClass(), "/reset.png");
    final BufferedImage icon2 = ImageUtil.loadImageResource(getClass(), "/reset.png");

    private final GoalManager goalManager;

    public ActiveGoalPanel(Goal goal, GoalManager goalManager, Runnable refreshCallback) {
        this.goalManager = goalManager;

        setLayout(new BorderLayout(5, 0)); // Add some horizontal gap
        // Add some padding inside the panel itself for better spacing
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // --- Components ---
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(goal.isCompleted());
        checkBox.setToolTipText("Mark as completed");
        add(checkBox, BorderLayout.WEST);

        // Goal text in the center
        JLabel goalTextLabel = new JLabel(goal.getText());
        goalTextLabel.setToolTipText(goal.getText());
        add(goalTextLabel, BorderLayout.CENTER);

        // Button container on the right
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0)); // Use FlowLayout for buttons, right-aligned, small gap
        buttonContainer.setOpaque(false); // Make background transparent if needed

        JButton addButton = new JButton("+"); // Consider using icons (e.g., from ImageUtil or FlatSVGIcon)
        JButton removeButton = new JButton("-");
        // Style buttons if desired (size, border, etc.)
        // Example: Setting preferred size
        Dimension buttonSize = new Dimension(25, 25);
        addButton.setPreferredSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);

        buttonContainer.add(addButton);
        buttonContainer.add(removeButton);
        add(buttonContainer, BorderLayout.EAST);

        // CheckBox Action
        checkBox.addActionListener(e -> {
            boolean isSelected = checkBox.isSelected();
            goalManager.updateGoalCompletion(goal, isSelected);
            refreshCallback.run(); // Trigger UI refresh
        });

        // --- Size Constraint ---
        Dimension prefSize = getPreferredSize();
        setMaximumSize(new Dimension(Integer.MAX_VALUE, prefSize.height));

    }
}

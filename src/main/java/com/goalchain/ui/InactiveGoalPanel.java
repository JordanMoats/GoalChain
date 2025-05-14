package com.goalchain.ui;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class InactiveGoalPanel extends JPanel {

    private final Goal goal;
    private final GoalManager goalManager;
    private final Runnable refreshCallback;

    // UI Components for inline editing
    private JPanel centerPanel;
    private CardLayout cardLayout;
    private JLabel goalTextLabel;
    private JTextField goalEditTextField;
    private JPanel actionPanel;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;

    private static final String LABEL_VIEW = "LABEL_VIEW";
    private static final String TEXTFIELD_VIEW = "TEXTFIELD_VIEW";

    // Icons (assuming they exist in the classpath relative to this class)
    private static final ImageIcon EDIT_ICON = new ImageIcon(ImageUtil.resizeImage(Objects.requireNonNull(ImageUtil.loadImageResource(InactiveGoalPanel.class, "/pencil.png")), 16, 16));
    private static final ImageIcon SAVE_ICON = new ImageIcon(ImageUtil.resizeImage(Objects.requireNonNull(ImageUtil.loadImageResource(InactiveGoalPanel.class, "/green-tick.png")), 16, 16));
    private static final ImageIcon CANCEL_ICON = new ImageIcon(ImageUtil.resizeImage(Objects.requireNonNull(ImageUtil.loadImageResource(InactiveGoalPanel.class, "/grey-cross.png")), 16, 16));

    // Font sizes for dynamic adjustment
    private static final float DEFAULT_FONT_SIZE = 14f; // Assuming base size is 12, +2 = 14
    private static final float MINIMUM_FONT_SIZE = 11f; // Example minimum

    private JLabel prereqCountLabel;
    private JLabel dependentCountLabel;

    public InactiveGoalPanel(Goal goal, GoalManager goalManager, Runnable refreshCallback) {
        this.goal = goal;
        this.goalManager = goalManager;
        this.refreshCallback = refreshCallback;

        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);

        // --- Center: Goal Text (Label/TextField) ---
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(false); // Match background

        goalTextLabel = new JLabel(goal.getText());
        // Use lighter gray for better contrast on darker inactive background
        goalTextLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        Font currentFont = goalTextLabel.getFont();
        goalTextLabel.setFont(currentFont.deriveFont(DEFAULT_FONT_SIZE));
        goalTextLabel.setVerticalAlignment(SwingConstants.CENTER);
        goalTextLabel.setHorizontalAlignment(SwingConstants.LEFT);

        goalEditTextField = new JTextField(goal.getText());
        goalEditTextField.setFont(currentFont.deriveFont(DEFAULT_FONT_SIZE));
        goalEditTextField.setBorder(BorderFactory.createCompoundBorder(
                goalEditTextField.getBorder(),
                BorderFactory.createEmptyBorder(0, 2, 0, 0))); // Small left padding
        // Potentially style the text field differently for inactive?
        // goalEditTextField.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());

        centerPanel.add(goalTextLabel, LABEL_VIEW);
        centerPanel.add(goalEditTextField, TEXTFIELD_VIEW);
        add(centerPanel, BorderLayout.CENTER);

        // --- Right Side: Counts + Actions ---
        JPanel eastPanel = new JPanel(); // Panel to hold counts and actions
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.X_AXIS)); // Use BoxLayout
        eastPanel.setOpaque(false);

        // Count Panel (existing)
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        countPanel.setOpaque(false);
        prereqCountLabel = new JLabel();
        prereqCountLabel.setFont(FontManager.getRunescapeSmallFont());
        prereqCountLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        countPanel.add(prereqCountLabel);
        dependentCountLabel = new JLabel();
        dependentCountLabel.setFont(FontManager.getRunescapeSmallFont());
        dependentCountLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        countPanel.add(dependentCountLabel);
        eastPanel.add(countPanel); // Add counts directly

        // Add glue to push actions to the right
        eastPanel.add(Box.createHorizontalGlue());

        // Action Panel (new for edit/save/cancel)
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 0)); // Tighter horizontal gap
        actionPanel.setOpaque(false);

        editButton = new JButton(EDIT_ICON);
        editButton.setToolTipText("Edit goal text");
        styleIconButton(editButton);

        saveButton = new JButton(SAVE_ICON);
        saveButton.setToolTipText("Save changes (Enter)");
        styleIconButton(saveButton);

        cancelButton = new JButton(CANCEL_ICON);
        cancelButton.setToolTipText("Discard changes (Esc)");
        styleIconButton(cancelButton);

        actionPanel.add(editButton); // Start with only edit button visible
        eastPanel.add(actionPanel); // Add actions after glue

        add(eastPanel, BorderLayout.EAST); // Add the combined east panel

        // Update counts initially
        updateRelationCounts();

        // Show label view initially
        cardLayout.show(centerPanel, LABEL_VIEW);

        // Add Context Menu listener (remains the same)
        MouseAdapter contextMenuListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        addMouseListener(contextMenuListener);
        goalTextLabel.addMouseListener(contextMenuListener);
        countPanel.addMouseListener(contextMenuListener);
        actionPanel.addMouseListener(contextMenuListener);

        // --- Size Constraint ---
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

        // ADD EDITING LISTENERS (to be done in next step)
        addEditingListeners();

        // Adjust font after initial layout
        SwingUtilities.invokeLater(this::adjustLabelFont);
    }

    // ADD showEditMode() and showDisplayMode() methods (to be done in next step)
    private void showEditMode() {
        cardLayout.show(centerPanel, TEXTFIELD_VIEW);
        goalEditTextField.setText(goal.getText());

        actionPanel.remove(editButton);
        actionPanel.add(saveButton);
        actionPanel.add(cancelButton);
        actionPanel.revalidate();
        actionPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            goalEditTextField.requestFocusInWindow();
            goalEditTextField.selectAll();
        });
    }

    private void showDisplayMode() {
        cardLayout.show(centerPanel, LABEL_VIEW);
        goalTextLabel.setText(goal.getText());
        // Inactive panel tooltip is set in updateRelationCounts, no need to set here
        adjustLabelFont(); // Adjust font when showing label

        actionPanel.remove(saveButton);
        actionPanel.remove(cancelButton);
        actionPanel.add(editButton);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private void saveChanges() {
        String newText = goalEditTextField.getText().trim();
        if (!newText.isEmpty() && !newText.equals(goal.getText())) {
            // goalManager.updateGoalText(goal, newText); // COMMENTED OUT UNTIL IMPLEMENTED
            showDisplayMode();
            // TODO: Uncomment refreshCallback() once goalManager.updateGoalText is implemented
            // refreshCallback.run();
        } else {
            showDisplayMode();
        }
    }

    private void addEditingListeners() {
        editButton.addActionListener(e -> showEditMode());

        saveButton.addActionListener(e -> saveChanges());

        cancelButton.addActionListener(e -> showDisplayMode());

        goalEditTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveChanges();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    showDisplayMode();
                }
            }
        });
    }

    private void updateRelationCounts() {
        List<Goal> prerequisites = goalManager.getPrerequisiteGoals(goal);
        int prereqCount = prerequisites.size();
        int dependentCount = goal.getDependentIds() != null ? goal.getDependentIds().size() : 0;

        // Set the label text (unchanged)
        prereqCountLabel.setText(prereqCount > 0 ? "[P:" + prereqCount + "]" : "");
        dependentCountLabel.setText(dependentCount > 0 ? "[D:" + dependentCount + "]" : "");

        // --- Set Tooltip for the entire panel --- 
        String panelTooltipText;
        if (prereqCount > 0 && prereqCount < 6) {
            StringBuilder tooltipBuilder = new StringBuilder("<html>");
            tooltipBuilder.append(goal.getText()).append(" (Inactive)<br/>"); // Add goal text first
            tooltipBuilder.append("Prerequisites:<br/>");
            for (Goal prereq : prerequisites) {
                tooltipBuilder.append("- ").append(prereq.getText()).append("<br/>");
            }
            tooltipBuilder.append("</html>");
            panelTooltipText = tooltipBuilder.toString();
        } else if (prereqCount >= 6) {
            panelTooltipText = goal.getText() + " (Inactive - " + prereqCount + " prerequisites not met)";
        } else { // 0 prerequisites
            panelTooltipText = goal.getText() + " (Inactive - prerequisites not met)"; // Default inactive text
        }
        this.setToolTipText(panelTooltipText);

        // Clear tooltips from individual labels as they are now on the panel
        prereqCountLabel.setToolTipText(null);
        dependentCountLabel.setToolTipText(null);
    }

    private void showContextMenu(Component invoker, int x, int y) {
        JPopupMenu contextMenu = new JPopupMenu();

        // -- Add Prerequisite -- (Still allow adding, even if inactive)
        JMenuItem addPrereqItem = new JMenuItem("Add Prerequisite...");
        addPrereqItem.addActionListener(e -> showAddPrerequisiteDialog());
        contextMenu.add(addPrereqItem);

        // -- Add Dependent --
        JMenuItem addDependentItem = new JMenuItem("Add Dependent...");
        addDependentItem.addActionListener(e -> showAddDependentDialog());
        contextMenu.add(addDependentItem);

        // --- Remove Relationships ---
        List<Goal> prerequisites = goalManager.getPrerequisiteGoals(goal);
        if (!prerequisites.isEmpty()) {
            JMenu removePrereqMenu = new JMenu("Remove Prerequisite");
            for (Goal prereq : prerequisites) {
                JMenuItem removeItem = new JMenuItem(prereq.getText());
                removeItem.setToolTipText("Stop '" + prereq.getText() + "' being a prerequisite for this goal");
                removeItem.addActionListener(e -> {
                    goalManager.removePrereq(prereq, goal);
                    refreshCallback.run();
                });
                removePrereqMenu.add(removeItem);
            }
            contextMenu.add(removePrereqMenu);
        }

        List<Goal> dependents = goalManager.getDependentGoals(goal);
        if (!dependents.isEmpty()) {
            JMenu removeDependentMenu = new JMenu("Remove Dependent");
            for (Goal dependent : dependents) {
                JMenuItem removeItem = new JMenuItem(dependent.getText());
                removeItem.setToolTipText("Stop this goal being a prerequisite for '" + dependent.getText() + "'");
                removeItem.addActionListener(e -> {
                    goalManager.removePrereq(goal, dependent); // Current goal is the prereq being removed
                    refreshCallback.run();
                });
                removeDependentMenu.add(removeItem);
            }
            contextMenu.add(removeDependentMenu);
        }

        contextMenu.addSeparator();

        // -- Delete Goal --
        JMenuItem deleteItem = new JMenuItem("Delete Goal");
        deleteItem.setForeground(Color.RED);
        deleteItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the goal: \"" + goal.getText() + "\"?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                goalManager.deleteGoal(goal);
                refreshCallback.run();
            }
        });
        contextMenu.add(deleteItem);

        contextMenu.show(invoker, x, y);
    }

    // --- Dialog Logic (Copied from ActiveGoalPanel, potentially refactor later) ---

    private void showAddPrerequisiteDialog() {
        // Or keep same logic: only non-completed, non-self, non-cycle
        List<Goal> potentialPrereqs = goalManager.getGoalMap().values().stream()
                .filter(p -> !p.getId().equals(goal.getId())) // Exclude self
                //.filter(p -> !p.isCompleted()) // Maybe allow completed?
                .filter(p -> goal.getPrerequisiteIds() == null || !goal.getPrerequisiteIds().contains(p.getId())) // Exclude already added
                .filter(p -> p.getPrerequisiteIds() == null || !p.getPrerequisiteIds().contains(goal.getId())) // Basic cycle check
                .collect(Collectors.toList());

        // --- Create Custom Panel --- 
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;

        JRadioButton selectExistingRadio = new JRadioButton("Select Existing Goal:", !potentialPrereqs.isEmpty());
        JComboBox<Goal> goalComboBox = new JComboBox<>(potentialPrereqs.toArray(new Goal[0]));
        goalComboBox.setRenderer(new DefaultListCellRenderer() { // Show goal text
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Goal) {
                    setText(((Goal) value).getText());
                }
                return this;
            }
        });

        JRadioButton createNewRadio = new JRadioButton("Create New Goal:", potentialPrereqs.isEmpty());
        JTextField newGoalTextField = new JTextField(20);

        ButtonGroup choiceGroup = new ButtonGroup();
        choiceGroup.add(selectExistingRadio);
        choiceGroup.add(createNewRadio);

        // Enable/disable based on radio selection
        goalComboBox.setEnabled(selectExistingRadio.isSelected());
        newGoalTextField.setEnabled(createNewRadio.isSelected());
        selectExistingRadio.addActionListener(e -> {
            goalComboBox.setEnabled(true);
            newGoalTextField.setEnabled(false);
        });
        createNewRadio.addActionListener(e -> {
            goalComboBox.setEnabled(false);
            newGoalTextField.setEnabled(true);
        });

        // Add components to panel
        selectionPanel.add(selectExistingRadio, gbc);
        gbc.insets = new Insets(2, 20, 2, 2); // Indent combo box
        selectionPanel.add(goalComboBox, gbc);
        gbc.insets = new Insets(10, 2, 2, 2); // Add space before next radio
        selectionPanel.add(createNewRadio, gbc);
        gbc.insets = new Insets(2, 20, 2, 2); // Indent text field
        selectionPanel.add(newGoalTextField, gbc);

        // --- Show Custom Dialog --- 
        int result = JOptionPane.showConfirmDialog(
                this, 
                selectionPanel, 
                "Add Prerequisite for: " + goal.getText(), 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Goal prereqToAdd = null;
            if (selectExistingRadio.isSelected()) {
                prereqToAdd = (Goal) goalComboBox.getSelectedItem();
            } else { // Create New
                String newGoalText = newGoalTextField.getText().trim();
                if (!newGoalText.isEmpty()) {
                    prereqToAdd = goalManager.quickAddGoalAndGet(newGoalText); 
                    if (prereqToAdd == null) {
                         JOptionPane.showMessageDialog(this, "Failed to create new goal.", "Error", JOptionPane.ERROR_MESSAGE);
                         return; 
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "New goal text cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return; 
                }
            }

            // --- Set Prerequisite --- 
            if (prereqToAdd != null) {
                if (prereqToAdd.getId().equals(goal.getId())) {
                     JOptionPane.showMessageDialog(this, "Cannot set a goal as its own prerequisite.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                goalManager.setPrereq(prereqToAdd, goal);
                refreshCallback.run();
            } else if (selectExistingRadio.isSelected()){
                JOptionPane.showMessageDialog(this, "No prerequisite goal selected.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showAddDependentDialog() {
        List<Goal> potentialDependents = goalManager.getGoalMap().values().stream()
                .filter(d -> !d.getId().equals(goal.getId())) // Exclude self
                //.filter(d -> !d.isCompleted()) // Maybe allow depending on completed?
                .filter(d -> d.getPrerequisiteIds() == null || !d.getPrerequisiteIds().contains(goal.getId())) // Exclude already dependent
                .filter(d -> goal.getPrerequisiteIds() == null || !goal.getPrerequisiteIds().contains(d.getId())) // Basic cycle check
                .collect(Collectors.toList());

        // --- Create Custom Panel --- 
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;

        JRadioButton selectExistingRadio = new JRadioButton("Select Existing Goal:", !potentialDependents.isEmpty());
        JComboBox<Goal> goalComboBox = new JComboBox<>(potentialDependents.toArray(new Goal[0]));
        goalComboBox.setRenderer(new DefaultListCellRenderer() { // Show goal text
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Goal) {
                    setText(((Goal) value).getText());
                }
                return this;
            }
        });

        JRadioButton createNewRadio = new JRadioButton("Create New Goal:", potentialDependents.isEmpty());
        JTextField newGoalTextField = new JTextField(20);

        ButtonGroup choiceGroup = new ButtonGroup();
        choiceGroup.add(selectExistingRadio);
        choiceGroup.add(createNewRadio);

        // Enable/disable based on radio selection
        goalComboBox.setEnabled(selectExistingRadio.isSelected());
        newGoalTextField.setEnabled(createNewRadio.isSelected());
        selectExistingRadio.addActionListener(e -> {
            goalComboBox.setEnabled(true);
            newGoalTextField.setEnabled(false);
        });
        createNewRadio.addActionListener(e -> {
            goalComboBox.setEnabled(false);
            newGoalTextField.setEnabled(true);
        });

        // Add components to panel
        selectionPanel.add(selectExistingRadio, gbc);
        gbc.insets = new Insets(2, 20, 2, 2); // Indent combo box
        selectionPanel.add(goalComboBox, gbc);
        gbc.insets = new Insets(10, 2, 2, 2); // Add space before next radio
        selectionPanel.add(createNewRadio, gbc);
        gbc.insets = new Insets(2, 20, 2, 2); // Indent text field
        selectionPanel.add(newGoalTextField, gbc);

        // --- Show Custom Dialog --- 
        int result = JOptionPane.showConfirmDialog(
                this,
                selectionPanel,
                "Add Dependent for: " + goal.getText(), // Updated title
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Goal dependentToAdd = null;
            if (selectExistingRadio.isSelected()) {
                dependentToAdd = (Goal) goalComboBox.getSelectedItem();
            } else { // Create New
                String newGoalText = newGoalTextField.getText().trim();
                if (!newGoalText.isEmpty()) {
                    dependentToAdd = goalManager.quickAddGoalAndGet(newGoalText);
                    if (dependentToAdd == null) {
                        JOptionPane.showMessageDialog(this, "Failed to create new goal.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Stop if goal creation failed
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "New goal text cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return; // Don't proceed if text is empty
                }
            }

            // --- Set Dependent (Set Prerequisite with current goal as prereq) --- 
            if (dependentToAdd != null) {
                if (dependentToAdd.getId().equals(goal.getId())) {
                     JOptionPane.showMessageDialog(this, "Cannot set a goal as its own dependent.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                goalManager.setPrereq(goal, dependentToAdd); // Current goal is the prerequisite
                refreshCallback.run();
            } else if (selectExistingRadio.isSelected()){
                JOptionPane.showMessageDialog(this, "No dependent goal selected.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Helper method to style icon buttons
    private void styleIconButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Optional: Adjust margins if needed
        // button.setMargin(new Insets(0, 0, 0, 0));
    }

    private void adjustLabelFont() {
        Font labelFont = goalTextLabel.getFont();
        String labelText = goalTextLabel.getText();
        int labelWidth = goalTextLabel.getWidth();

        if (labelWidth <= 0 || labelText == null || labelText.isEmpty()) {
            // Component not laid out yet or no text, keep default font for now
            goalTextLabel.setFont(labelFont.deriveFont(DEFAULT_FONT_SIZE));
            return;
        }

        // Try default size first
        Font currentFont = labelFont.deriveFont(DEFAULT_FONT_SIZE);
        FontMetrics metrics = goalTextLabel.getFontMetrics(currentFont);
        int textWidth = metrics.stringWidth(labelText);

        if (textWidth > labelWidth) {
            // Doesn't fit at default, try minimum size with a cleaner font
            currentFont = new Font(Font.SANS_SERIF, Font.PLAIN, (int) MINIMUM_FONT_SIZE);
            metrics = goalTextLabel.getFontMetrics(currentFont);
            textWidth = metrics.stringWidth(labelText);
            // If it still doesn't fit at minimum, the label's ellipsis will handle it
        }

        // Set the determined font
        goalTextLabel.setFont(currentFont);
    }
} 
package com.goalchain.data;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// todo Fix later when you figure out read write lmao
@Slf4j
public class GoalManager {
    private List<Goal> allGoals;

    public GoalManager() {
        allGoals = new ArrayList<>();
    }

    public void quickAddGoal(String s) {
        Goal newGoal = new Goal(s);
        allGoals.add(newGoal);
    }

    public void addGoal(Goal goal) {
        allGoals.add(goal);
    }

    public void updateGoalCompletion(Goal goalToUpdate, boolean isCompleted) {
        for (Goal goal : allGoals) {
            if (goal.equals(goalToUpdate)) {
                goal.setCompleted(isCompleted);
                log.info("Updated '{}' completion status to: '{}'", goal.getText(), isCompleted);
                break;
            }
        }
    }

    public List<Goal> getActiveGoals() {
        return allGoals.stream()
                .filter(goal -> goal.isActive() && !goal.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Goal> getInactiveGoals() {
        return allGoals.stream()
                .filter(goal -> !goal.isActive() && !goal.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Goal> getCompletedGoals() {
        return allGoals.stream()
                .filter(Goal::isCompleted)
                .collect(Collectors.toList());
    }
}

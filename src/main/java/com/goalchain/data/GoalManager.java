package com.goalchain.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class GoalManager {
    @Getter
    private final Map<String, Goal> goalMap;
    private final Runnable dataChangeCallback;

    public GoalManager(Map<String, Goal> initialGoalMap, Runnable dataChangeCallback) {
        // todo figure out read write here
        this.goalMap = initialGoalMap;
        this.dataChangeCallback = dataChangeCallback;
        log.info("GoalManager initialized with  {} goals.", this.goalMap.size());
    }

    private void notifyDataChanged() {
        if (dataChangeCallback != null) {
            log.debug("Running data change callback now!");
            dataChangeCallback.run();
        }
    }

    public void quickAddGoal(String s) {
        log.debug("quick adding goal");
        Goal newGoal = new Goal(s);
        goalMap.put(newGoal.getId(), newGoal);
        notifyDataChanged();
        log.debug("Added goal: {}", newGoal.getId());
    }

    public void updateGoalCompletion(Goal goal, boolean isCompleted) {
        Goal managedGoal = goalMap.get(goal.getId());
        if (managedGoal != null) {
            managedGoal.setCompleted(isCompleted);
            notifyDataChanged();
            log.debug("Updated goal completeioon: {} to {}", goal.getId(), isCompleted);
        } else {
            log.warn("Attempted to update non-existent goal: {}", goal.getId());
        }
    }

    public boolean checkActive(Goal goal) {
        if (goal == null) return false;
        if (goal.isCompleted()) {
            return false;
        }
        if (goal.getPrerequisiteIds().isEmpty()) {
            return true;
        }
        for (String id : goal.getPrerequisiteIds()) {
            Goal prereqGoal = goalMap.get(id);
            // If
            if (prereqGoal == null || !prereqGoal.isCompleted()) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Goal> getActiveGoals() {
          return goalMap.values()
                .stream()
                .filter(this::checkActive)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Goal> getInactiveGoals() {
        return goalMap.values()
                .stream()
                .filter(goal -> !goal.isCompleted() && !checkActive(goal))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Goal> getCompletedGoals() {
        return goalMap.values()
                .stream()
                .filter(Goal::isCompleted)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setPrereq(Goal prereqGoal, Goal dependentGoal) {
        if (prereqGoal != null && dependentGoal != null) {
            dependentGoal.addPrereq(prereqGoal.getId());
            notifyDataChanged();
            log.debug("Set prereq: {} -> {}", prereqGoal.getId(), dependentGoal.getId());
        } else {
            log.warn("Attempted to set prereq with null goal(s)");
        }
    }

    public void deleteGoal(Goal goal) {
        if (goal != null && goalMap.containsKey(goal.getId())) {
            goalMap.remove(goal.getId());

            // Remove this goal from the prereqs of all other goals.
            goalMap.values().forEach(g -> g.getPrerequisiteIds().remove(goal.getId()));
            notifyDataChanged();
            log.debug("Deleted goal: {}", goal.getId());
        }
    }
}

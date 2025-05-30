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

    // New method to add a goal and return it
    public Goal quickAddGoalAndGet(String s) {
        log.debug("Quick adding goal and returning: {}", s);
        Goal newGoal = new Goal(s);
        goalMap.put(newGoal.getId(), newGoal);
        notifyDataChanged();
        log.debug("Added goal: {}", newGoal.getId());
        return newGoal;
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
        if (prereqGoal != null && dependentGoal != null && !prereqGoal.getId().equals(dependentGoal.getId())) {
            // Check for cycles (basic): don't add if dependentGoal is already a prereq for prereqGoal
            if (prereqGoal.getPrerequisiteIds().contains(dependentGoal.getId())) {
                log.warn("Attempted to create circular dependency: {} <-> {}", prereqGoal.getId(), dependentGoal.getId());
                // Optionally show a message to the user here
                return;
            }
            // Add prereq to the dependent goal
            dependentGoal.addPrereq(prereqGoal.getId());
            // Add dependent to the prerequisite goal
            prereqGoal.addDependent(dependentGoal.getId());
            notifyDataChanged();
            log.debug("Set relationship: {} is prerequisite for {}", prereqGoal.getId(), dependentGoal.getId());
        } else {
            log.warn("Attempted to set prereq with null or identical goal(s)");
        }
    }

    // Add method to remove a prerequisite relationship
    public void removePrereq(Goal prereqGoal, Goal dependentGoal) {
        if (prereqGoal != null && dependentGoal != null) {
            dependentGoal.removePrereq(prereqGoal.getId());
            prereqGoal.removeDependent(dependentGoal.getId());
            notifyDataChanged();
            log.debug("Removed relationship: {} is no longer prerequisite for {}", prereqGoal.getId(), dependentGoal.getId());
        } else {
            log.warn("Attempted to remove prereq with null goal(s)");
        }
    }

    // Helper to get Goal objects from IDs
    private List<Goal> getGoalsByIds(List<String> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(goalMap::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Get prerequisite goals for a given goal
    public List<Goal> getPrerequisiteGoals(Goal goal) {
        return getGoalsByIds(goal.getPrerequisiteIds());
    }

    // Get dependent goals for a given goal
    public List<Goal> getDependentGoals(Goal goal) {
        return getGoalsByIds(goal.getDependentIds());
    }

    public void deleteGoal(Goal goal) {
        if (goal != null && goalMap.containsKey(goal.getId())) {
            String goalId = goal.getId();
            goalMap.remove(goalId);

            // Remove this goal from the prereqs list of its dependents
            getDependentGoals(goal).forEach(dependent -> dependent.removePrereq(goalId));

            // Remove this goal from the dependents list of its prerequisites
            getPrerequisiteGoals(goal).forEach(prereq -> prereq.removeDependent(goalId));

            notifyDataChanged();
            log.debug("Deleted goal: {}", goalId);
        }
    }
}

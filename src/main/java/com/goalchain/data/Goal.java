package com.goalchain.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Goal {
    @Setter
    @Getter
    private String text;
    @Getter
    private final String id;
    @Getter
    private List<String> prerequisiteIds;
    @Getter
    private List<String> dependentIds;
    @Setter
    @Getter
    private boolean prioritized;
    @Getter
    @Setter
    private boolean completed;

    public Goal(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.prerequisiteIds = new ArrayList<>();
        this.dependentIds = new ArrayList<>();
        this.prioritized = false;
        this.completed = false;
    }

    public Goal(String text, List<String> prerequisiteIds) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.prerequisiteIds = prerequisiteIds;
        this.dependentIds = new ArrayList<>();
        this.prioritized = false;
        this.completed = false;
    }

    public void addPrereq(String prereqId) {
        this.prerequisiteIds.add(prereqId);
    }

    public void addDependent(String dependentId) {
        if (this.dependentIds == null) {
            this.dependentIds = new ArrayList<>();
        }
        this.dependentIds.add(dependentId);
    }

    public void removePrereq(String prereqId) {
        if (this.prerequisiteIds != null) {
            this.prerequisiteIds.remove(prereqId);
        }
    }

    public void removeDependent(String dependentId) {
        if (this.dependentIds != null) {
            this.dependentIds.remove(dependentId);
        }
    }
}

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
        this.prioritized = false;
        this.completed = false;
    }

    public Goal(String text, List<String> prerequisiteIds) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.prerequisiteIds = prerequisiteIds;
        this.prioritized = false;
        this.completed = false;
    }

    public void addPrereq(String prereqId) {
        this.prerequisiteIds.add(prereqId);
    }
}

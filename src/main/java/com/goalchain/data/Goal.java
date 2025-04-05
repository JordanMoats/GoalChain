package com.goalchain.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Goal {
    public String text;
    public List<String> prerequisiteIds;
    public boolean isPriority;
    @Setter
    public boolean completed;

    public Goal(String text) {
        this.text = text;
        this.prerequisiteIds = new ArrayList<>();
        this.isPriority = false;
        this.completed = false;
    }

    public Goal(String text, List<String> prerequisiteIds) {
        this.text = text;
        this.prerequisiteIds = prerequisiteIds;
        this.isPriority = false;
        this.completed = false;
    }

    public boolean isActive() {
        return prerequisiteIds.isEmpty();
    }
}

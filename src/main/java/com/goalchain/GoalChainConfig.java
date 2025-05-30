package com.goalchain;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("goalchain")
public interface GoalChainConfig extends Config
{
	@ConfigItem(
			keyName = "goalMapData", // Key for storing the data
			name = "Goal Data (JSON)", // Name displayed in settings
			description = "Serialized data of the goals. Do not edit manually unless you know what you are doing.",
			position = 2, // Optional: Define order
			hidden = true
	)
	default String goalMapData()
	{
		return "{}"; // Default to an empty JSON object string
	}

	@ConfigItem(
			keyName = "goalMapData",
			name = "",
			description = "",
			hidden = true
	)
	void setGoalMapData(String data);
}

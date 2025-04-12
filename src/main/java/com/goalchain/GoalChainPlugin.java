package com.goalchain;

import com.goalchain.data.Goal;
import com.goalchain.data.GoalManager;
import com.goalchain.ui.GoalChainPluginPanel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Goal Chain"
)
public class GoalChainPlugin extends Plugin
{
	// Config group name constant
	private static final String CONFIG_GROUP = "goalchain";

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private GoalChainConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

    private NavigationButton navButton;
	private GoalManager goalManager;
	private GoalChainPluginPanel panel;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Goal Chain starting...");

		// Load goals from config
		Map<String, Goal> loadedGoals = loadGoals();

		// Initialize GoalManager with loaded goals and save goals callback
		this.goalManager = new GoalManager(loadedGoals, this::saveGoals);

		// Initialize panel
        GoalChainPluginPanel panel = new GoalChainPluginPanel(goalManager);

		// Create the sidebar button
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/reset.png");
		navButton = NavigationButton.builder()
			.tooltip("Goal Chain")
			.icon(icon)
			.priority(100)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);

		log.info("Goal Chain started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Goal Chain stopping.");

		clientToolbar.removeNavigation(navButton);
		this.panel = null;
		this.goalManager = null;

		log.info("Goal Chain stopped!");
	}

	// Method to load the goals from the config
	private Map<String, Goal> loadGoals() {
		String json = config.goalMapData();
		if (json == null || json.isEmpty() || json.equals("{}")) {
			log.info("No existing goal data found in config.");
			return new HashMap<>();
		}

		try {
			// Define the serialization type
			Type type = new TypeToken<Map<String, Goal>>(){}.getType();
			Map<String, Goal> loadedMap = gson.fromJson(json, type);
			log.info("Successfully loaded {} goals from config.", loadedMap.size());
			return loadedMap != null ? loadedMap : new HashMap<>();
		} catch (Exception e) {
			log.error("Failed to parse goal data from config!", e);
			return new HashMap<>();
		}
	}

	private void saveGoals() {
		log.info("~Saving goals");
		if (goalManager == null) {
			log.warn("GoalManager not initialized, cannot save goals.");
			return;
		}
		log.info("~Saving goals 1");
		Map<String, Goal> currentGoals = goalManager.getGoalMap();
		log.info("~Saving goals 2");
		String json = gson.toJson(currentGoals);
		log.info("~Saving goals 3");
		configManager.setConfiguration(CONFIG_GROUP, "goalMapData", json);
		log.info("~Saving goals 4");
		log.debug("Saved {} goals to config.", currentGoals.size());
	}

	@Provides
	GoalChainConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoalChainConfig.class);
	}
}

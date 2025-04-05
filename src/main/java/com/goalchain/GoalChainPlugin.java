package com.goalchain;

import com.goalchain.data.GoalManager;
import com.goalchain.ui.GoalChainPluginPanel;
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

@Slf4j
@PluginDescriptor(
	name = "Goal Chain"
)
public class GoalChainPlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private GoalChainConfig config;

    private NavigationButton navButton;
	private GoalManager goalManager;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Goal Chain starting...");

		this.goalManager = new GoalManager();
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

		log.info("Example stopped!");
	}

	@Provides
	GoalChainConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoalChainConfig.class);
	}
}

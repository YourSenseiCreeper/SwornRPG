package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * @author t7seven7t
 * @editor dmulloy2
 */

public class TagListener implements Listener {
	
	public TagListener(SwornRPG plugin) {
	}

	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().getName().equals("dmulloy2")) {
			event.setTag(ChatColor.AQUA + "dmulloy2");
		}
		if (event.getNamedPlayer().getName().equals("minermac8521")) {
			event.setTag(ChatColor.GREEN + "minermac8521");
		}
		if (event.getNamedPlayer().getName().equals("brett_setchfield")) {
			event.setTag(ChatColor.AQUA + "brett_" + ChatColor.LIGHT_PURPLE + "setchfield");
		}
		if (event.getNamedPlayer().getName().equals("irene325")) {
			event.setTag(ChatColor.DARK_RED + "irene325");
		}
	}
}

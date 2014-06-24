package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;

import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdStaffList extends SwornRPGCommand
{
	public CmdStaffList(SwornRPG plugin)
	{
		super(plugin);
		this.name = "stafflist";
		this.aliases.add("staff");
		this.optionalArgs.add("group");
		this.description = "List online staff";
		this.permission = Permission.STAFFLIST;
	}

	@Override
	public void perform()
	{
		// Calculate online staff
		Map<String, List<String>> staffMap = new HashMap<String, List<String>>();

		int total = 0;

		for (Player player : plugin.getServer().getOnlinePlayers())
		{
			if (plugin.getPermissionHandler().hasPermission(player, Permission.STAFF))
			{
				net.milkbowl.vault.permission.Permission permission = plugin.getVaultHandler().getPermission();
				if (permission != null)
				{
					String group = permission.getPrimaryGroup(player);
					if (group != null)
					{
						if (! staffMap.containsKey(group))
						{
							staffMap.put(group, new ArrayList<String>());
						}

						staffMap.get(group).add((player.isOp() ? "&b" : "&e") + player.getName());
					}
				}
				else
				{
					if (! staffMap.containsKey("staff"))
					{
						staffMap.put("staff", new ArrayList<String>());
					}

					staffMap.get("staff").add(player.isOp() ? "&b" : "&e" + player.getName());
				}

				total++;
			}
		}

		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(getMessage("stafflist_header"), total, plugin.getServer().getMaxPlayers()));
		lines.add(line.toString());

		// Specific Group

		String group = "all";
		if (args.length > 0)
		{
			group = args[0];
		}

		if (staffMap.containsKey(group))
		{
			line = new StringBuilder();
			line.append("&3" + WordUtils.capitalize(group) + "&e: ");

			for (String player : staffMap.get(group))
			{
				line.append("&e" + player + "&b, ");
			}

			if (line.lastIndexOf("&b, ") >= 0)
			{
				line.replace(line.lastIndexOf("&"), line.lastIndexOf(" "), "");
			}

			lines.add(line.toString());
			
			for (String string : lines)
				sendMessage(string);

			return;
		}

		// All online staff

		for (Entry<String, List<String>> entry : staffMap.entrySet())
		{
			line = new StringBuilder();
			line.append("&3" + WordUtils.capitalize(entry.getKey()) + "&e: ");

			for (String player : entry.getValue())
			{
				line.append("&e" + player + "&b, ");
			}

			if (line.lastIndexOf("&b, ") >= 0)
			{
				line.replace(line.lastIndexOf("&"), line.lastIndexOf(" "), "");
			}

			lines.add(line.toString());
		}

		for (String string : lines)
			sendMessage(string);
	}
}
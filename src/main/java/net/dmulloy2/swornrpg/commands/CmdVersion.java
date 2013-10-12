package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

/**
 * @author dmulloy2
 */

public class CmdVersion extends SwornRPGCommand
{
	public CmdVersion(SwornRPG plugin)
	{
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Display SwornRPG version";
		this.permission = Permission.VERSION;

		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		sendMessage(plugin.getMessage("version_header"));
		
		StringBuilder line = new StringBuilder();
		line.append(plugin.getMessage("version_author") + " ");

		for (String author : plugin.getDescription().getAuthors())
		{
			line.append("&e" + author + ", ");
		}
		
		if (line.lastIndexOf(",") >= 0)
		{
			line.deleteCharAt(line.lastIndexOf(","));
		}

		sendMessage(line.toString());
		
		sendMessage(plugin.getMessage("version_loaded"), plugin.getDescription().getFullName());
		sendMessage(plugin.getMessage("version_update"), plugin.updateNeeded() ? "true" : "false");
		sendMessage(plugin.getMessage("version_download"));
	}
}
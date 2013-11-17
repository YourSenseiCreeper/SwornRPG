package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdTagReset extends SwornRPGCommand
{
	public CmdTagReset(SwornRPG plugin)
	{
		super(plugin);
		this.name = "tagreset";
		this.aliases.add("tagr");
		this.optionalArgs.add("player");
		this.description = "Reset a player's tag";
		this.permission = Permission.TAG_RESET;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			err(plugin.getMessage("no_tagapi"));
			plugin.debug(plugin.getMessage("log_tagapi_null"));
			return;
		}

		if (args.length == 0)
		{
			plugin.getTagHandler().removeTagChange(player);
			sendpMessage(plugin.getMessage("tag_reset_self"));
		}
		else if (args.length == 1)
		{
			if (! hasPermission(Permission.TAG_RESET_OTHERS))
			{
				err(getMessage("noperm"));
				return;
			}

			if (args[0].length() > 16)
			{
				err(plugin.getMessage("username_too_large"));
				return;
			}

			Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				err(getMessage("noplayer"));
				return;
			}

			plugin.getTagHandler().removeTagChange(target);
			sendpMessage(plugin.getMessage("tag_reset_resetter"), target.getName());
			sendMessageTarget(plugin.getMessage("tag_reset_resetee"), target);
		}
	}
}
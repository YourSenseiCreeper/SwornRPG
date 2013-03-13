package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.Perms;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdLevelr implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdLevelr(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		if (Perms.has(sender, plugin.adminResetPerm))
		{
			if (args.length == 0)
			{
				if (sender instanceof Player)
				{
					final PlayerData data = plugin.getPlayerDataCache().getData(sender.getName());
					final long now = System.currentTimeMillis();
					data.setPlayerxp(0);
					data.setFrenzyusedlevel(0);
					data.setLastUpdateTimeSpent(now);
//					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have reset your level");
				}
				else
				{
					sender.sendMessage(plugin.mustbeplayer);
				}
			}
			else if (args.length == 1)
			{
				Player target = Util.matchPlayer(args[0]);
				if (target != null)
				{
					String targetp = target.getName();
					final PlayerData data = plugin.getPlayerDataCache().getData(targetp);
					final long now = System.currentTimeMillis();
					data.setPlayerxp(0);
					data.setFrenzyusedlevel(0);
					data.setLastUpdateTimeSpent(now);
					plugin.getPlayerDataCache().save();
					sender.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have reset " + targetp + "'s level");
				}
				else
				{
					sender.sendMessage(plugin.noplayer);
				}
			}
			else
			{
				sender.sendMessage(plugin.invalidargs + "(/levelr [player])");
			}
		}
		else
		{
			sender.sendMessage(plugin.noperm);
		}
		
		return true;
	}
}
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdEject implements CommandExecutor
{
	public SwornRPG plugin;
	public CmdEject(SwornRPG plugin)  
	{
		this.plugin = plugin;
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	{    
		Player player = null;
		if (sender instanceof Player) 
		{
			player = (Player) sender;
			if(args.length > 0)
			{
				player.sendMessage(plugin.invalidargs + "(/eject)");
			}
			else
			{
				if (player.getPassenger() != null)
				{
					player.eject();
				}
				else
				{
					player.sendMessage(plugin.prefix + ChatColor.RED + "Error, nobody is riding you");
				}
			}
		}
		else
		{
			sender.sendMessage(plugin.mustbeplayer);
		}
		
		return true;
	}
}
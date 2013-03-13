package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author dmulloy2
 */

public class ExperienceListener implements Listener 
{

	private SwornRPG plugin;
	public ExperienceListener(SwornRPG plugin) 
	{
		this.plugin = plugin;
	}
	
	//Rewards XP in PvP situations
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event)
	{
		if (plugin.playerkills == false)
			return;
		Player killed = event.getEntity().getPlayer();
		Player killer = event.getEntity().getKiller();
		//Checks to see if it was PvP
		if (killer instanceof Player)
		{
			//Factions Warzone check, helpful for pvpboxes
			PluginManager pm = Bukkit.getServer().getPluginManager();
			if ((pm.getPlugin("Factions") != null)||(pm.getPlugin("SwornNations") != null))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				Faction otherFaction2 = Board.getFactionAt(new FLocation(killed.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction2.isWarZone()))
					return;
			}
			String killerp = killer.getName();
			String killedp = killed.getName();
			//Checks for suicide
			if (killedp == killerp)
				return;
			//Killer xp gain
			int killxp = plugin.killergain;
			pm.callEvent(new PlayerXpGainEvent (killer, killxp));
			killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing " + ChatColor.RED + killedp);
			//Killed xp loss
			int killedxp = -(plugin.killedloss);
			int msgxp = Math.abs(killedxp);
			pm.callEvent(new PlayerXpGainEvent (killed, killedxp));
			killed.sendMessage(plugin.prefix + ChatColor.YELLOW + "You lost " + ChatColor.RED + msgxp + ChatColor.YELLOW + " xp after getting killed by " + ChatColor.RED + killerp);
		}
	}
	
	//Rewards XP in PvE situations
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (plugin.mobkills == false)
			return;
		Entity kill = event.getEntity().getKiller();
		Entity killed = event.getEntity();
		if (killed instanceof Player)
			return;
		if (kill instanceof Player)
		{
			Player killer = event.getEntity().getKiller();
			String mobname = event.getEntity().getType().toString().toLowerCase().replaceAll("_", " ");
			PluginManager pm = Bukkit.getServer().getPluginManager();
			//Factions exploit check
			if ((pm.getPlugin("Factions") != null)||(pm.getPlugin("SwornNations") != null))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction.isSafeZone()))
					return;
			}
			int killxp = 5;
			pm.callEvent(new PlayerXpGainEvent (killer, killxp));
			if (mobname.startsWith("e")||mobname.startsWith("o")||mobname.startsWith("i"))
				
			{
				killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing an " + ChatColor.RED + mobname);
			}
			else
			{
				killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing a " + ChatColor.RED + mobname);
			}
		}
	}
	
	//Rewards XP on Minecraft xp levelup
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		if (plugin.xplevel == false)
			return;
		Player player = event.getPlayer();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		//Factions exploit check
		if ((pm.getPlugin("Factions") != null)||(pm.getPlugin("SwornNations") != null))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			if (otherFaction.isWarZone())
				return;
		}
		int oldlevel = event.getOldLevel();
		int newlevel = event.getNewLevel();
		if (newlevel - oldlevel != 1)
			return;
		int xpgained = plugin.xplevelgain;
		Bukkit.getServer().getPluginManager().callEvent(new PlayerXpGainEvent (player, xpgained));
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You gained " + ChatColor.GREEN + xpgained + ChatColor.YELLOW + " xp for gaining Minecraft xp");
	}
	
	//Rewards items and money on player levelup
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelup(PlayerLevelupEvent event)
	{
		Player player = event.getPlayer();
		String playerp = player.getName();
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		data.setFrenzyused(false);
		data.setOldlevel(data.getPlayerxp()/125);
		int oldlevel = data.getOldlevel();
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have leveled up to level " + ChatColor.GREEN + oldlevel + ChatColor.YELLOW + "!");
		plugin.getPlayerDataCache().save();
		//Awards money if money rewards are enabled
		if (plugin.money == true)
		{
			//Checks for vault
			if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
			{
				Economy economy = plugin.getEconomy();
				double money = (int) oldlevel*plugin.basemoney;
				economy.depositPlayer(player.getName(), money);
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " +  ChatColor.GREEN + "$" + money + ChatColor.YELLOW + " for leveling up");
			}
		}
		//Awards items if money rewards are enabled
		if (plugin.items == true)
		{
			int level = data.getPlayerxp()/125;
			int rewardamt = level*plugin.itemperlevel;
			ItemStack item = new ItemStack(plugin.itemreward, rewardamt);
			String friendlyitem = item.getType().toString().toLowerCase().replaceAll("_", " ");
			InventoryWorkaround.addItems(player.getInventory(), item);
			player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + rewardamt + " " + friendlyitem + ChatColor.YELLOW + "(s)");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerXpGain(PlayerXpGainEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int xpgained = event.getXpGained();
		//Add the xp gained to their overall xp
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		int oldlevel = data.getOldlevel();
		int newlevel = data.getPlayerxp()/125;
		//If the player leveled up, call the appropriate event
		plugin.getPlayerDataCache().save();
		if (newlevel > oldlevel)
		{
			Bukkit.getServer().getPluginManager().callEvent(new PlayerLevelupEvent (player, newlevel, oldlevel));
		}
		else
		{
			data.setOldlevel(newlevel);
		}
	}
}

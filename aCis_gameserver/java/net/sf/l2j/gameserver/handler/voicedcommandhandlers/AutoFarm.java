package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.type.Bot;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AutoFarm implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"autofarm",
		"farm_"
	
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		final Bot bot = player.getBot();
		if (command.equals("autofarm"))
			dashboard(player);
		else if (command.startsWith("farm_"))
		{
			String addcmd = command.substring(5).trim();
			if (addcmd.startsWith("on"))
			{
				bot.start();
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("off"))
			{
				bot.stop();
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("inc_lowlife"))
			{
				bot.setLowLifePercentage(bot.getLowLifePercentage() + 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("dec_lowlife"))
			{
				bot.setLowLifePercentage(bot.getLowLifePercentage() - 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("inc_chance"))
			{
				bot.setChancePercentage(bot.getChancePercentage() + 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("dec_chance"))
			{
				bot.setChancePercentage(bot.getChancePercentage() - 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("inc_hp_pot"))
			{
				bot.setHpPotionPercentage(bot.getHpPotionPercentage() + 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("dec_hp_pot"))
			{
				bot.setHpPotionPercentage(bot.getHpPotionPercentage() - 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("inc_mp_pot"))
			{
				bot.setMpPotionPercentage(bot.getMpPotionPercentage() + 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("dec_mp_pot"))
			{
				bot.setMpPotionPercentage(bot.getMpPotionPercentage() - 5);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("inc_radius"))
			{
				bot.setRadius(bot.getRadius() + 100);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("dec_radius"))
			{
				bot.setRadius(bot.getRadius() - 100);
				dashboard(player);
				return true;
			}
			else if (addcmd.startsWith("buff_protect"))
			{
				if(bot.isNoBuffProtected())
				{
					bot.setNoBuffProtection(false);
					player.sendMessage("No buff protected has been activated.");
				}
				else
				{
					bot.setNoBuffProtection(true);
					player.sendMessage("No buff protected has been desactivated.");
				}
				
				dashboard(player);
				return true;
			}
		}
		return true;
	}
	
	public static void dashboard(Player player)
	{
		final Bot bot = player.getBot();
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(player.isLang() + "mods/farm-dashboard.htm");
		
		htm.replace("%name%", player.getName());
		
		htm.replace("%state%", bot.isActive() ? "<font color=00FF00>Active</font>" : "<font color=FF0000>Inactive</font>");
		
		htm.replace("%lowlife%", String.valueOf(bot.getLowLifePercentage()));
		htm.replace("%chance%", String.valueOf(bot.getChancePercentage()));
		htm.replace("%hpPotion%", String.valueOf(bot.getHpPotionPercentage() == 0 ? "<font color=FF0000>OFF" : "<font color=FA8072>" + bot.getHpPotionPercentage()));
		htm.replace("%mpPotion%", String.valueOf(bot.getMpPotionPercentage() == 0 ? "<font color=FF0000>OFF" : "<font color=3399CC>" + bot.getMpPotionPercentage()));
		htm.replace("%radius%", StringUtil.formatNumber(bot.getRadius()));
		htm.replace("%protection%", bot.isNoBuffProtected() ? "l2ui.CheckBox_checked" : "l2ui.CheckBox");
		htm.replace("%button%", (bot.isActive() ? "value=\"Stop\" action=\"bypass -h farm_off\"" : "value=\"Start\" action=\"bypass -h farm_on\""));
		htm.replace("%radius%", StringUtil.formatNumber(bot.getRadius()));
		
		player.sendPacket(htm);
		
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
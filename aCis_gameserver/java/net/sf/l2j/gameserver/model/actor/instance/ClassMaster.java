package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.util.LocalizationStorage;

public final class ClassMaster extends Merchant
{
	public ClassMaster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	private String makeMessage(Player player)
	{
		ClassId classId = player.getClassId();
		int jobLevel = classId.getLevel() + 1;
		int level = player.getStatus().getLevel();
		StringBuilder sb = new StringBuilder();
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			jobLevel = 4;
		}
		
		if ((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= 76 && jobLevel == 3) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			int jobLevelPriceIdx = jobLevel - 1;
			Item item = ItemData.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM[jobLevelPriceIdx]);
			if (Config.CLASS_MASTERS_PRICE_LIST[jobLevelPriceIdx] > 0)
			{
				sb.append("Price: ").append(StringUtil.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevelPriceIdx])).append(" ").append(item.getName()).append("<br1>");
			}
			for (ClassId cid : ClassId.VALUES)
			{
				if (!cid.isChildOf(classId) || cid.getLevel() != classId.getLevel() + 1)
					continue;
				sb.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_change_class ").append(cid.getId()).append(" ").append(jobLevelPriceIdx).append("\">").append(StringUtil.htmlClassName(cid.getId(), player)).append("</a><br>");
			}
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		else
		{
			switch (jobLevel)
			{
				case 1:
				{
					sb.append(LocalizationStorage.getInstance().getString(player.isLangString(), "Need20Level"));
					break;
				}
				case 2:
				{
					sb.append(LocalizationStorage.getInstance().getString(player.isLangString(), "Need40Level"));
					break;
				}
				case 3:
				{
					sb.append(LocalizationStorage.getInstance().getString(player.isLangString(), "Need76Level"));
					break;
				}
				case 4:
				{
					sb.append(LocalizationStorage.getInstance().getString(player.isLangString(), "NothingToUp"));
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command);
		if (st.nextToken().equals("change_class"))
		{
			int val = Integer.parseInt(st.nextToken());
			int idx = Integer.parseInt(st.nextToken());
			if (idx < Config.CLASS_MASTERS_PRICE_ITEM.length && idx < Config.CLASS_MASTERS_PRICE_LIST.length)
			{
				int itemId = Config.CLASS_MASTERS_PRICE_ITEM[idx];
				long itemCount = Config.CLASS_MASTERS_PRICE_LIST[idx];
				if (player.getInventory().destroyItemByItemId("change_class", itemId, (int) itemCount, player, null) != null)
				{
					changeClass(player, val);
					if (Config.CLASS_MASTERS_REWARD_ITEM.length > idx && Config.CLASS_MASTERS_REWARD_ITEM[idx] > 0 && Config.CLASS_MASTERS_REWARD_AMOUNT.length > idx && Config.CLASS_MASTERS_REWARD_AMOUNT[idx] > 0)
					{
						player.getInventory().addItem("", Config.CLASS_MASTERS_REWARD_ITEM[idx], (int) Config.CLASS_MASTERS_REWARD_AMOUNT[idx], player, player);
					}
				}
				else if (itemId == 57)
				{
					player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				}
				else
				{
					player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
				}
			}
			else
			{
				System.out.println("Incorect job index " + idx);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
		
	}
	
	private void changeClass(Player player, int val)
	{
		if (player.getClassId().getLevel() == 3)
		{
			player.sendPacket(SystemMessageId.THIRD_CLASS_TRANSFER);
		}
		else
		{
			player.sendPacket(SystemMessageId.CLASS_TRANSFER);
		}
		player.setClassId(val);
		player.broadcastCharInfo();
		player.broadcastPacket(new MagicSkillUse(player, player, 4339, 1, 0, 0));
	}
	
	@Override
	public String getHtmlPath(Player player, final int npcId, final int val)
	{
		return player.isLang() + "custom/" + npcId + "" + (val == 0 ? "" : "-" + val) + ".htm";
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(getHtmlPath(player, getNpcId(), 0));
		html.replace("%classmaster%", makeMessage(player));
		player.sendPacket(html);
	}
	
}
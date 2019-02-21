package com.anomalytea.HorseInspector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerAttackHorseListener implements Listener {

	private final HorseInspector plugin;

	public PlayerAttackHorseListener(HorseInspector plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerHitHorse(EntityDamageByEntityEvent e) {
		
		// Only catch player attacks
		if(!(e.getDamager() instanceof Player && e.getCause().equals(DamageCause.ENTITY_ATTACK))) {
			return;
		}
		
		// Only catch horse type victims
		if(!(e.getEntityType().equals(EntityType.HORSE)
				|| e.getEntityType().equals(EntityType.SKELETON_HORSE)
				|| e.getEntityType().equals(EntityType.ZOMBIE_HORSE)
				|| e.getEntityType().equals(EntityType.DONKEY)
				|| e.getEntityType().equals(EntityType.MULE)
				|| e.getEntityType().equals(EntityType.LLAMA)))
		{
			return;
		}
		
		// Only catch whacks with configured item
		if( !((Player) e.getDamager()).getInventory().getItemInMainHand().getType().equals(Material.matchMaterial(plugin.getConfig().getString("item"))) ) {
			return;
		}
		
		// Cancel damage
		e.setCancelled(true);
		
		// Get horse data
		AbstractHorse horse = (AbstractHorse) e.getEntity();
		double speed = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
		double jump = horse.getJumpStrength();
		double hp = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		
		// Convert internal values into meaningful numbers
		double speedBlocks = speed * 43; // m/s
		double jumpBlocks = jumpStrToBlocks(jump); // m
		double hearts = hp / 2.0;
		
		// Round values for display
		speedBlocks = round(speedBlocks, 2);
		jumpBlocks = round(jumpBlocks, 2);
		hearts = round(hearts, 2);
		
		// Calculate percentages
		double speedPercent = 100 * (speedBlocks - 4.8375) / (14.5125 - 4.8375);
		double jumpPercent = 100 * (jumpBlocks - jumpStrToBlocks(0.4)) / (jumpStrToBlocks(1.0) - jumpStrToBlocks(0.4));
		double hpPercent = 100 * (hp - 15.0) / (30.0 - 15.0);
		
		// Round percentages for display
		speedPercent = round(speedPercent, 2);
		jumpPercent = round(jumpPercent, 2);
		hpPercent = round(hpPercent, 2);
		
		// Compose message to send to player
		ChatColor labelColor = ChatColor.GREEN;
		ChatColor percentColor = ChatColor.DARK_AQUA;
		ChatColor resetColor = ChatColor.RESET;
		ArrayList<String> msg = new ArrayList<>();
		boolean calcPercent = e.getEntityType().equals(EntityType.HORSE)
				|| e.getEntityType().equals(EntityType.SKELETON_HORSE)
				|| e.getEntityType().equals(EntityType.ZOMBIE_HORSE)
				|| e.getEntityType().equals(EntityType.MULE);
		
		String titleLine = labelColor + "--";
		if (horse.getType().equals(EntityType.DONKEY)) {
			titleLine += "Donkey";
		} else if (horse.getType().equals(EntityType.MULE)) {
			titleLine += "Mule";
		} else if (horse.getType().equals(EntityType.LLAMA)) {
			titleLine += "Llama";
		} else {
			titleLine += "Horse";
		}
		titleLine += " Info";
		if (horse.getCustomName() != null) {
			titleLine += ": " + resetColor + horse.getCustomName();
		}
		titleLine += labelColor + "--";
		msg.add(titleLine);
		// Speed
		if (calcPercent) {
			msg.add(labelColor + "Speed: " + resetColor + speedBlocks + " m/s " + percentColor + "(" + speedPercent + "% of max)" + resetColor);
		} else {
			msg.add(labelColor + "Speed: " + resetColor + speedBlocks + " m/s"); // Speed isn't variable for Donkeys and Llamas
		}
		// HP
		msg.add(labelColor + "HP: " + resetColor + hearts + " hearts " + percentColor + "(" + hpPercent + "% of max)" + resetColor);
		// Jump Height
		if (calcPercent) {
			msg.add(labelColor + "Jump height: " + resetColor + jumpBlocks + " m " + percentColor + "(" + jumpPercent + "% of max)" + resetColor);
		} else {
			msg.add(labelColor + "Jump height: " + resetColor + jumpBlocks + " m"); // Jump height isn't variable for Donkeys and Llamas
		}
		// Tamer
		if (!plugin.getConfig().getBoolean("show-tamer")) {
			// skip this line of the output
		} else if (horse.getOwner() == null) {
			msg.add(labelColor + "Tamer: " + resetColor + "Not tamed");
		} else if (horse.getOwner().getName() == null) {
			msg.add(labelColor + "Tamer: " + resetColor + horse.getOwner().getUniqueId());
		} else {
			msg.add(labelColor + "Tamer: " + resetColor + horse.getOwner().getName());
		}
		
		// Send message to player
		for (String m : msg) {
			e.getDamager().sendMessage(m);
		}
	}
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double jumpStrToBlocks(double jumpStr) {
		return -0.1817584952 * Math.pow(jumpStr, 3) + 3.689713992*Math.pow(jumpStr, 2) + 2.128599134 * jumpStr - 0.343930367;
	}

}

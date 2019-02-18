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
		
		// Only catch whacks with stick
		if( !((Player) e.getDamager()).getInventory().getItemInMainHand().getType().equals(Material.STICK) ) {
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
		double speedblocks = speed * 43; // m/s
		double jumpblocks = -0.1817584952 * Math.pow(jump, 3) + 3.689713992*Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367; // m
		double hearts = hp / 2.0;
		
		// Round values for display
		speedblocks = round(speedblocks, 2);
		jumpblocks = round(jumpblocks, 2);
		hearts = round(hearts, 2);
		
		// Calculate percentages
		double speedpercent = 100 * (speed - 0.1125) / (0.3375 - 0.1125);
		double jumppercent = 100 * (jump - 0.4) / (1.0 - 0.4);
		double hppercent = 100 * (hp - 15.0) / (30.0 - 15.0);
		
		// Round percentages for display
		speedpercent = round(speedpercent, 2);
		jumppercent = round(jumppercent, 2);
		hppercent = round(hppercent, 2);
		
		// Compose message to send to player
		ChatColor labelColor = ChatColor.GREEN;
		ChatColor percentColor = ChatColor.DARK_AQUA;
		ChatColor resetColor = ChatColor.RESET;
		ArrayList<String> msg = new ArrayList<String>();
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
			titleLine += ": " + horse.getCustomName();
		}
		titleLine += "--";			
		msg.add(titleLine);
		// Speed
		if (calcPercent) {
			msg.add(labelColor + "Speed: " + resetColor + speedblocks + " m/s " + percentColor + "(" + String.valueOf(speedpercent) + "% of max)" + resetColor);
		} else {
			msg.add(labelColor + "Speed: " + resetColor + speedblocks + " m/s"); // Speed isn't variable for Donkeys and Llamas
		}
		// HP
		msg.add(labelColor + "HP: " + resetColor + String.valueOf(hearts) + " hearts " + percentColor + "(" + String.valueOf(hppercent) + "% of max)" + resetColor);
		// Jump Height
		if (calcPercent) {
			msg.add(labelColor + "Jump height: " + resetColor + jumpblocks + " m " + percentColor + "(" + String.valueOf(jumppercent) + "% of max)" + resetColor);
		} else {
			msg.add(labelColor + "Jump height: " + resetColor + jumpblocks + " m"); // Jump height isn't variable for Donkeys and Llamas
		}
		// Tamer
		if (horse.getOwner() == null) {
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

}

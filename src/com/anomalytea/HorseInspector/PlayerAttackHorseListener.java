package com.anomalytea.HorseInspector;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerAttackHorseListener implements Listener {
	
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
		double speedblocks = speed*43; // scale internal value into m/s
		speedblocks = Math.round(speedblocks*100000.0) / 100000.0; // janky rounding to 5 decimal places
		double speedpercent = 100 * (speed-0.1125) / (0.3375 - 0.1125);
		speedpercent = Math.round(speedpercent*100.0) / 100.0; // janky rounding to 2 decimal places
		double jump = horse.getJumpStrength();
		double jumpblocks = -0.1817584952*Math.pow(jump, 3) + 3.689713992*Math.pow(jump, 2) + 2.128599134*jump - 0.343930367; // scale internal value into m (approx.)
		jumpblocks = Math.round(jumpblocks*100000.0) / 100000.0; // janky rounding to 5 decimal places
		double jumppercent = 100 * (jump - 0.4) / (1.0 - 0.4);
		jumppercent = Math.round(100 * jump * 100.0) / 100.0; // janky rounding to 2 decimal places
		double hp = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double hppercent = 100 * (hp - 15) / (30.0 - 15);
		hppercent = Math.round(hppercent * 100.0) / 100.0; // janky rounding to 2 decimal places
		
		// Compose message to send to player
		ArrayList<String> msg = new ArrayList<String>();
		msg.add("--Horse Info--");
		msg.add("Speed: " + speedblocks + " m/s (" + String.valueOf(speedpercent) + "% of max)");
		msg.add("HP: " + String.valueOf(hp/2) + " hearts (" + String.valueOf(hppercent) + "% of max)");
		msg.add("Jump height: " + jumpblocks + " m (" + String.valueOf(jumppercent) + "% of max)");
		if (horse.getOwner() == null) {
			msg.add("Tamer:  Not tamed");
		} else if (horse.getOwner().getName() == null) {
			msg.add("Tamer:  " + horse.getOwner().getUniqueId());
		} else {
			msg.add("Tamer:  " + horse.getOwner().getName());
		}
		
		// Send message to player
		for (String m : msg) {
			e.getDamager().sendMessage(m);
		}
	}

}

package com.anomalytea.HorseInspector;

import org.bukkit.Material;
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
				|| e.getEntityType().equals(EntityType.ZOMBIE_HORSE)))
		{
			return;
		}
		
		// Only catch whacks with stick
		if( !((Player) e.getDamager()).getInventory().getItemInMainHand().getType().equals(Material.STICK) ) {
			return;
		}
		
		// Event caught; do stuff here

	}

}

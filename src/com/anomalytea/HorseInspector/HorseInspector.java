package com.anomalytea.HorseInspector;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseInspector extends JavaPlugin {
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new PlayerAttackHorseListener(), this);
	}
	
	@Override
	public void onDisable() {
		
	}

}

package com.anomalytea.HorseInspector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class PlayerAttackHorseListener implements Listener {

  private final HorseInspector plugin;

  public PlayerAttackHorseListener(HorseInspector plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerHitHorse(EntityDamageByEntityEvent e) {

    // Only catch player attacks
    if(!(e.getDamager() instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK)) {
      return;
    }

    // Only catch horse type victims
    EntityType entityType = e.getEntityType();
    if (!(entityType == EntityType.HORSE
        || entityType == EntityType.SKELETON_HORSE
        || entityType == EntityType.ZOMBIE_HORSE
        || entityType == EntityType.DONKEY
        || entityType == EntityType.MULE
        || entityType == EntityType.LLAMA
        || entityType == EntityType.TRADER_LLAMA
        )) {
      return;
    }

    ItemStack item = ((Player) e.getDamager()).getInventory().getItemInMainHand();

    // Only catch whacks with configured item
    if (item.getType() != Material.matchMaterial(plugin.getConfig().getString("item"))) {
      return;
    }

    // If item name is configured, only catch whacks that match
    if (plugin.getConfig().isSet("item-name")) {
      String itemName = "";

      if (item.getItemMeta().hasDisplayName()) {
        itemName = item.getItemMeta().getDisplayName();
      }

      if (!itemName.equals(plugin.getConfig().getString("item-name"))) {
        return;
      }

    }

    // Check permissions
    if (!e.getDamager().hasPermission("horseinspector.use")) {
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
    double jumpPercent = 100 * (jumpBlocks - jumpStrToBlocks(0.4))
        / (jumpStrToBlocks(1.0) - jumpStrToBlocks(0.4));
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

    String titleLine = labelColor + "--";
    switch (entityType) {
      case DONKEY:
        titleLine += "Donkey";
        break;
      case MULE:
        titleLine += "Mule";
        break;
      case LLAMA:
      case TRADER_LLAMA:
        titleLine += "Llama";
        break;
      default:
        titleLine += "Horse";
        break;
    }
    titleLine += " Info";
    if (horse.getCustomName() != null) {
      titleLine += ": " + resetColor + horse.getCustomName();
    }
    titleLine += labelColor + "--";
    msg.add(titleLine);

    // Speed
    String speedString = labelColor
        + "Speed: "
        + resetColor
        + speedBlocks
        + " m/s ";
    switch (entityType) {
      case HORSE:
      case SKELETON_HORSE:
      case ZOMBIE_HORSE:
      case MULE:
        speedString += percentColor
            + "("
            + speedPercent
            + "% of max)"
            + resetColor;
        break;
      default:
        break;
    }
    msg.add(speedString);


    // HP
    msg.add(labelColor
        + "HP: "
        + resetColor
        + hearts
        + " hearts "
        + percentColor
        + "("
        + hpPercent
        + "% of max)"
        + resetColor);

    // Jump Height
    String jumpString = labelColor
        + "Jump height: "
        + resetColor
        + jumpBlocks
        + " m ";
    switch (entityType) {
      case HORSE:
      case SKELETON_HORSE:
      case ZOMBIE_HORSE:
      case MULE:
        jumpString += percentColor
            + "("
            + jumpPercent
            + "% of max)"
            + resetColor;
        msg.add(jumpString);
        break;
      case LLAMA:
      case TRADER_LLAMA:
        // skip jump height line entirely on Llamas
        break;
      default:
        msg.add(jumpString);
        break;
    }

    // Strength (if llama)
    switch (entityType) {
      case LLAMA:
      case TRADER_LLAMA:
        int strength = ((Llama) horse).getStrength();
        msg.add(labelColor
            + "Strength: "
            + resetColor
            + strength
            + percentColor
            + " ("
            + (strength * 20)
            + "% of max)"
            + resetColor);
        break;
      default:
        break;
    }

    // Tamer
    if (!plugin.getConfig().getBoolean("show-tamer")) {
      // skip this line of the output
    } else if (horse.getOwner() == null) {
      msg.add(labelColor
          + "Tamer: "
          + resetColor
          + "Not tamed");
    } else if (horse.getOwner().getName() == null) {
      msg.add(labelColor
          + "Tamer: "
          + resetColor
          + horse.getOwner().getUniqueId());
    } else {
      msg.add(labelColor
          + "Tamer: "
          + resetColor
          + horse.getOwner().getName());
    }

    // Send message to player
    for (String m : msg) {
      e.getDamager().sendMessage(m);
    }
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();
    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public static double jumpStrToBlocks(double jumpStr) {
    return -0.1817584952 * Math.pow(jumpStr, 3) + 3.689713992*Math.pow(jumpStr, 2) + 2.128599134
        * jumpStr - 0.343930367;
  }

}

package xyz.oribuin.eternalcrates.nms;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface NMSHandler {

    Entity createClientsideEntity(Player player, Location loc, EntityType entityType);

    ItemStack setString(ItemStack item, String key, String value);

    ItemStack setInt(ItemStack item, String key, int value);

    ItemStack setLong(ItemStack item, String key, long value);

    ItemStack setDouble(ItemStack item, String key, double value);

    ItemStack setBoolean(ItemStack item, String key, boolean value);

    Firework spawnClientFirework(Player player, Location loc, FireworkEffect effect);

    void detonateFirework(Firework firework, Player player);


//    Entity updateEntity(Player player, Entity entity);
}

package com.undeadinventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class DeathListener implements Listener {
    private static final Set<EntityType> HUMANOID_MOBS = EnumSet.of(
            EntityType.ZOMBIE,
            EntityType.HUSK,
            EntityType.DROWNED,
            EntityType.SKELETON,
            EntityType.BOGGED,
            EntityType.STRAY,
            EntityType.WITHER_SKELETON,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.VILLAGER,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.WITCH,
            EntityType.EVOKER,
            EntityType.VINDICATOR,
            EntityType.PILLAGER
    );

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        LivingEntity killer = player.getKiller();
        if (killer == null) return;
        if (!HUMANOID_MOBS.contains(killer.getType())) return;

        // Transfer player's inventory to the killer mob (main hand, off hand, armor slots)
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("UndeadInventory"), () -> {
            // Change mob's name
            killer.setCustomName("Killer of " + player.getName());
            killer.setCustomNameVisible(true);

            // Transfer main hand
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand != null && mainHand.getType() != Material.AIR) {
                killer.getEquipment().setItemInMainHand(mainHand.clone());
            }
            // Transfer off hand
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (offHand != null && offHand.getType() != Material.AIR) {
                killer.getEquipment().setItemInOffHand(offHand.clone());
            }
            // Transfer armor
            ItemStack[] armor = player.getInventory().getArmorContents();
            killer.getEquipment().setArmorContents(Arrays.stream(armor).map(item -> item == null ? null : item.clone()).toArray(ItemStack[]::new));

            // Store the rest of the player's inventory in the mob's persistent data container
            org.bukkit.persistence.PersistentDataContainer data = killer.getPersistentDataContainer();
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(Bukkit.getPluginManager().getPlugin("UndeadInventory"), "player_inventory");
            java.util.List<String> serialized = new java.util.ArrayList<>();
            // If the mob already has inventory, append to it
            if (data.has(key, org.bukkit.persistence.PersistentDataType.STRING)) {
                String existing = data.get(key, org.bukkit.persistence.PersistentDataType.STRING);
                if (existing != null && !existing.isEmpty()) {
                    serialized.addAll(java.util.Arrays.asList(existing.split(";")));
                }
            }
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    try {
                        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                        org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
                        config.set("item", item);
                        config.save(baos);
                        String base64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
                        serialized.add(base64);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            data.set(key, org.bukkit.persistence.PersistentDataType.STRING, String.join(";", serialized));

            // Clear player's inventory
            player.getInventory().clear();
        }, 1L);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!entity.getCustomNameVisible() || entity.getCustomName() == null) return;
        if (!entity.getCustomName().startsWith("Killer of ")) return;

        org.bukkit.persistence.PersistentDataContainer data = entity.getPersistentDataContainer();
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(Bukkit.getPluginManager().getPlugin("UndeadInventory"), "player_inventory");
        if (!data.has(key, org.bukkit.persistence.PersistentDataType.STRING)) return;
        String serialized = data.get(key, org.bukkit.persistence.PersistentDataType.STRING);
        if (serialized == null || serialized.isEmpty()) return;

        // Give the inventory to the player who killed the mob
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
            Player killer = event.getEntity().getKiller();
            String[] items = serialized.split(";");
            for (String base64 : items) {
                try {
                    byte[] bytes = java.util.Base64.getDecoder().decode(base64);
                    java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
                    org.bukkit.configuration.file.YamlConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(bais));
                    ItemStack item = config.getItemStack("item");
                    if (item != null && item.getType() != Material.AIR) {
                        killer.getInventory().addItem(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Optionally, clear the mob's inventory data after giving it to the player
            data.remove(key);
        }
    }

    // All unused code removed.
}

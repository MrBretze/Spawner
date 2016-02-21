package fr.bretzel.spawner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Axelo on 14/08/2015.
 */

public class Event implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Spawner spawner = Main.getSpawnerByPlayer(event.getPlayer());

        if (spawner == null) {
            Main.spawners.add(new Spawner(event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerBlace(BlockPlaceEvent event) {
        if (event.getBlock().getState() instanceof CreatureSpawner && event.getItemInHand().hasItemMeta()) {
            String mob = event.getItemInHand().getItemMeta().getDisplayName().replace(ChatColor.GREEN + "Spawner de: " + ChatColor.AQUA, "");
            if (CreatureType.fromName(mob) != null || mob.equalsIgnoreCase("Iron Golem")) {
                Spawner spawner = Main.getSpawnerByPlayer(event.getPlayer());

                if (mob.equalsIgnoreCase("Iron Golem")) {
                    CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlock().getState();
                    creatureSpawner.setSpawnedType(EntityType.IRON_GOLEM);
                    spawner.addSpawner(event.getBlock().getLocation());
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Un spawner a eter placer a la cordoner: " + creatureSpawner.getBlock().getX() + ", " + creatureSpawner.getBlock().getY() + ", " + creatureSpawner.getBlock().getZ());
                    return;
                }

                if (spawner != null) {
                    CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlock().getState();
                    creatureSpawner.setCreatureType(CreatureType.fromName(mob));
                    spawner.addSpawner(event.getBlock().getLocation());
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Un spawner a eter placer a la cordoner: " + creatureSpawner.getBlock().getX() + ", " + creatureSpawner.getBlock().getY() + ", " + creatureSpawner.getBlock().getZ());
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED + "Une erreur et la !");
                    return;
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "Le spawner n'a pas une entite valide !");
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            Spawner owner = Main.getSpawnerByCreatureSpawner(spawner);
            if (owner != null) {
                Spawner pspawner = Main.getSpawnerByPlayer(event.getPlayer());

                if (!Objects.equals(owner.getOwner(), pspawner.getOwner())) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED + "Se n'est pas ton spawner !");
                    return;
                }
                owner.remove(event.getBlock().getLocation());
                event.setExpToDrop(0);
                spawner.getWorld().dropItemNaturally(spawner.getLocation().add(0.5D, 0.1D, 0.5D), Command.toStak(spawner.getSpawnedType().getName()));
                event.getPlayer().sendMessage(ChatColor.RED + "Le spawner a eter detruit !");

                if (owner.contains(event.getBlock().getLocation())) {
                    owner.remove(event.getBlock().getLocation());
                    event.getPlayer().sendMessage(ChatColor.RED + "Le spawner a eter detruit avec une mauvaise pioche !");
                    return;
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "Se spawner ne peut pas etre casser !");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockExplod(EntityExplodeEvent event) {
        ArrayList<Block> supr = new ArrayList<>();
        for (Block b : event.blockList()) {
            if (b.getState() instanceof CreatureSpawner) {
                supr.add(b);
            }
        }

        for (Block b : supr) {
            event.blockList().remove(b);
        }
    }
}

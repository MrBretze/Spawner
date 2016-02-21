package fr.bretzel.spawner;

import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by MrBretzel on 13/08/2015.
 */

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (player.hasPermission("spawner.command.give")) {
                        if (args.length > 1) {
                            if (Main.mobs.get(args[1]) != null || Main.irons.contains(args[1])) {
                                String mob = args[1];

                                if (Main.irons.contains(mob.toLowerCase())) {
                                    ItemStack stack = new ItemStack(Material.MOB_SPAWNER, 1);
                                    ItemMeta meta = stack.getItemMeta();
                                    meta.setDisplayName(ChatColor.GREEN + "Spawner de: " + ChatColor.AQUA + "Iron Golem");
                                    stack.setItemMeta(meta);
                                    if (player.getInventory().firstEmpty() == -1) {
                                        player.getWorld().dropItemNaturally(player.getLocation().add(0.0D, 0.5D, 0.0D), stack);
                                    } else {
                                        player.getInventory().addItem(stack);
                                    }
                                } else {
                                    CreatureType type = Main.mobs.get(mob);
                                    givePlayer(player, type.toEntityType());
                                }
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "l'entite choisie n'est pas valid !");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /spawner give <mob>");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("takeall")) {
                    if (player.hasPermission("spawner.command.takeall")) {
                        takeAll(player);
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                        return true;
                    }
                } else if (Bukkit.getPlayer(args[0]) != null && player.hasPermission("spawner.command.admin")) {
                    if (args.length > 1) {
                        Player selected = Bukkit.getPlayer(args[0]);
                        if (args[1].equalsIgnoreCase("give")) {
                            if (player.hasPermission("spawner.command.admin.give")) {
                                if (args.length > 2) {
                                    if (Main.mobs.get(args[2]) != null || Main.irons.contains(args[2])) {
                                        String mob = args[2];
                                        if (Main.irons.contains(mob.toLowerCase())) {
                                            ItemStack stack = new ItemStack(Material.MOB_SPAWNER, 1);
                                            ItemMeta meta = stack.getItemMeta();
                                            meta.setDisplayName(ChatColor.GREEN + "Spawner de: " + ChatColor.AQUA + "Iron Golem");
                                            stack.setItemMeta(meta);
                                            if (player.getInventory().firstEmpty() == -1) {
                                                player.getWorld().dropItemNaturally(player.getLocation().add(0.0D, 0.5D, 0.0D), stack);
                                            } else {
                                                player.getInventory().addItem(stack);
                                            }
                                        } else {
                                            CreatureType type = Main.mobs.get(mob);
                                            givePlayer(selected, type.toEntityType());
                                        }
                                        return true;
                                    } else {
                                        player.sendMessage(ChatColor.RED + "l'entite choisie n'est pas valid !");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                                return true;
                            }
                        } else if (args[1].equalsIgnoreCase("takeall")) {
                            if (player.hasPermission("spawner.command.admin.takeall")) {
                                takeAll(selected);
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                            player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /spawner give <mob>");
                    player.sendMessage(ChatColor.RED + "Usage: /spawner takeall");

                    if (player.hasPermission("spawner.command.admin")) {
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /spawner give <mob>");
                player.sendMessage(ChatColor.RED + "Usage: /spawner takeall");

                if (player.hasPermission("spawner.command.admin")) {
                    player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                    player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                }
                return true;
            }
            return true;
        } else if (commandSender instanceof ConsoleCommandSender) {
            ConsoleCommandSender player = (ConsoleCommandSender) commandSender;
            if (Bukkit.getPlayer(args[0]) != null && player.hasPermission("spawner.command.admin")) {
                if (args.length > 1) {
                    Player selected = Bukkit.getPlayer(args[0]);
                    if (args[1].equalsIgnoreCase("give")) {
                        if (player.hasPermission("spawner.command.admin.give")) {
                            if (args.length > 2) {
                                if (Main.mobs.get(args[2]) != null || Main.irons.contains(args[2])) {
                                    String mob = args[2];
                                    if (Main.irons.contains(mob.toLowerCase())) {
                                        ItemStack stack = new ItemStack(Material.MOB_SPAWNER, 1);
                                        ItemMeta meta = stack.getItemMeta();
                                        meta.setDisplayName(ChatColor.GREEN + "Spawner de: " + ChatColor.AQUA + "Iron Golem");
                                        stack.setItemMeta(meta);
                                        if (selected.getInventory().firstEmpty() == -1) {
                                            selected.getWorld().dropItemNaturally(selected.getLocation().add(0.0D, 0.5D, 0.0D), stack);
                                        } else {
                                            selected.getInventory().addItem(stack);
                                        }
                                    } else {
                                        CreatureType type = Main.mobs.get(mob);
                                        givePlayer(selected, type.toEntityType());
                                    }
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.RED + "l'entite choisie n'est pas valid !");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("takeall")) {
                        if (player.hasPermission("spawner.command.admin.takeall")) {
                            takeAll(selected);
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "Tu na pas la permission pour executer cette command !");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                        player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                        return true;
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                    player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /spawner <player> give <mob>");
                player.sendMessage(ChatColor.RED + "Usage: /spawner <player> takeall");
                return true;
            }
        } else {
            return true;
        }
    }

    public static void takeAll(Player player) {
        Spawner spawner = Main.getSpawnerByPlayer(player);
        int u = 0;
        Iterator<Location> locationIterator = spawner.getLocations().iterator();
        List<Location> supr = new ArrayList<>();
        while (locationIterator.hasNext()) {
            Location l = locationIterator.next();
            if (l.getBlock().getState() instanceof CreatureSpawner) {
                u++;
                CreatureSpawner y = (CreatureSpawner) l.getBlock().getState();
                Chunk chunk = y.getChunk();
                if (!chunk.isLoaded()) {
                    chunk.load(false);
                    y.getWorld().getBlockAt(y.getLocation()).setType(Material.AIR);
                    chunk.unload();
                } else {
                    y.getWorld().getBlockAt(y.getLocation()).setType(Material.AIR);
                }
                givePlayer(player, y.getSpawnedType());
                supr.add(l);
            }
        }
        for (Location location : supr) {
            spawner.remove(location);
        }
        player.sendMessage(ChatColor.GREEN.toString() + u + " de spawner son retourner vers leur proprietaire !");
    }

    public static void givePlayer(Player player, EntityType entityType) {
        if (player != null && entityType != null) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation().add(0.0D, 0.5D, 0.0D), toStak(entityType.getName()));
                return;
            } else {
                player.getInventory().addItem(toStak(entityType.getName()));
                return;
            }
        }
    }

    public static ItemStack toStak(String name) {

        if (name == "VillagerGolem") {
            name = "Iron Golem";
        }

        ItemStack stack = new ItemStack(Material.MOB_SPAWNER, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Spawner de: " + ChatColor.AQUA + name);
        stack.setItemMeta(meta);
        return stack;
    }
}

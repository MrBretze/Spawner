package fr.bretzel.spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Spawner
        extends JavaPlugin
        implements Listener {
    JavaPlugin plugin = this;
    FileConfiguration config = getConfig();
    ArrayList<Material> pickaxe = new ArrayList();

    public void onEnable() {
        this.config.options().copyDefaults(true);
        saveConfig();

        Bukkit.getPluginManager().registerEvents(this, this.plugin);

        this.pickaxe.add(Material.WOOD_PICKAXE);
        this.pickaxe.add(Material.STONE_PICKAXE);
        this.pickaxe.add(Material.IRON_PICKAXE);
        this.pickaxe.add(Material.GOLD_PICKAXE);
        this.pickaxe.add(Material.DIAMOND_PICKAXE);

        getCommand("spawner").setExecutor(this);
    }

    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((sender instanceof Player)) {
            Player p = (Player) sender;
            if (args.length >= 1) {
                ItemStack mob;
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length != 3) {
                        p.sendMessage(ChatColor.RED + "Erreur dans la commande : " + ChatColor.GREEN + "/spawner give [player] [entity]");
                        return true;
                    }
                    if (!p.hasPermission("spawner.give")) {
                        p.sendMessage(ChatColor.RED + "Tu dois avoir la permission " + ChatColor.GREEN + "spawner.give" + ChatColor.RED + " pour utiliser cette commande.");
                        return true;
                    }
                    Player p2 = Bukkit.getPlayer(args[1]);
                    if (p2 != null) {
                        EntityType ent = EntityType.fromName(args[2]);
                        if (ent != null) {
                            if (p2.getInventory().firstEmpty() == -1) {
                                p.sendMessage(ChatColor.RED + "Inventaire de " + ChatColor.DARK_RED + p2.getName() + ChatColor.RED + " plein.");
                            } else {
                                mob = new ItemStack(Material.MOB_SPAWNER, 1);

                                ItemMeta im = mob.getItemMeta();

                                im.setDisplayName(ChatColor.AQUA + "Spawner ? " + ent.getName());

                                mob.setItemMeta(im);

                                p2.getInventory().addItem(new ItemStack[]{mob});
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Entit? " + ChatColor.DARK_RED + args[2] + ChatColor.RED + " introuvable.");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Joueur " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " introuvable.");
                    }
                } else if (args[0].equalsIgnoreCase("takeall")) {
                    if (!p.hasPermission("spawner.takeall")) {
                        p.sendMessage(ChatColor.RED + "Tu dois avoir la permission " + ChatColor.GREEN + "spawner.takeall" + ChatColor.RED + " pour utiliser cette commande.");
                        return true;
                    }
                    HashMap<String, List<String>> spawner = new HashMap();
                    List<String> list;
                    for (String key : this.config.getKeys(true)) {
                        String[] k = key.split("[.]");
                        if (k.length == 2) {
                            if (k[0].equals("spawner")) {
                                list = this.config.getStringList(key);
                                if (list.size() > 0) {
                                    spawner.put(k[1], list);
                                }
                            }
                        }
                    }
                    int item = 0;
                    int nbSpawner = 0;
                    for (String world : spawner.keySet()) {
                        World w = Bukkit.getWorld(world);
                        if (w != null) {
                            for (String str : spawner.get(w)) {
                                String[] s = str.split(":");
                                if ((s.length == 4) && (UUID.fromString(s[3]).equals(p.getUniqueId()))) {
                                    Block b1 = w.getBlockAt(Integer.valueOf(s[0]).intValue(), Integer.valueOf(s[1]).intValue(), Integer.valueOf(s[2]).intValue());
                                    if (b1.getType() == Material.MOB_SPAWNER) {
                                        CreatureSpawner b = (CreatureSpawner) b1.getState();

                                        ItemStack mob2 = new ItemStack(Material.MOB_SPAWNER, 1);

                                        ItemMeta im = mob2.getItemMeta();

                                        im.setDisplayName(ChatColor.AQUA + "Spawner ? " + b.getCreatureTypeName());

                                        mob2.setItemMeta(im);
                                        if (p.getInventory().firstEmpty() != -1) {
                                            p.getInventory().addItem(new ItemStack[]{mob2});
                                            item++;

                                            b1.setType(Material.AIR);
                                        }
                                        nbSpawner++;
                                    }
                                }
                            }
                        }
                    }
                    if (item != nbSpawner) {
                        p.sendMessage(ChatColor.AQUA.toString() + item + ChatColor.GREEN + " spawner retir? mais il en reste " + ChatColor.AQUA + (nbSpawner - item) +
                                ChatColor.RED + " qui n'ont pas pu ?tre retir? (plus de place dans l'inventaire).");
                    } else {
                        p.sendMessage(ChatColor.AQUA.toString() + item + ChatColor.GREEN + " spawner retir?.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Erreur dans la commande");
                }
            } else {
                p.sendMessage(ChatColor.RED + "Erreur dans la commande");
            }
        } else {
            CommandSender p = sender;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length != 3) {
                        p.sendMessage(ChatColor.RED + "Erreur dans la commande : " + ChatColor.GREEN + "/spawner give [player] [entity]");
                        return true;
                    }
                    if (!p.hasPermission("spawner.give")) {
                        p.sendMessage(ChatColor.RED + "Tu dois avoir la permission " + ChatColor.GREEN + "spawner.give" + ChatColor.RED + " pour utiliser cette commande.");
                        return true;
                    }
                    Player p2 = Bukkit.getPlayer(args[1]);
                    if (p2 != null) {
                        EntityType ent = EntityType.fromName(args[2]);
                        if (ent != null) {
                            if (p2.getInventory().firstEmpty() == -1) {
                                p.sendMessage(ChatColor.RED + "Inventaire de " + ChatColor.DARK_RED + p2.getName() + ChatColor.RED + " plein.");
                            } else {
                                ItemStack mob = new ItemStack(Material.MOB_SPAWNER, 1);

                                ItemMeta im = mob.getItemMeta();

                                im.setDisplayName(ChatColor.AQUA + "Spawner ? " + ent.getName());

                                mob.setItemMeta(im);

                                p2.getInventory().addItem(new ItemStack[]{mob});
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Entit? " + ChatColor.DARK_RED + args[2] + ChatColor.RED + " introuvable.");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Joueur " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " introuvable.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Erreur dans la commande");
                }
            }
        }
        return true;
    }

    @EventHandler
    void BlockPlace(BlockPlaceEvent e) {
        ItemStack is = e.getItemInHand();
        if (is.hasItemMeta()) {
            String name = is.getItemMeta().getDisplayName();
            if (name.startsWith(ChatColor.AQUA + "Spawner ? ")) {
                CreatureSpawner b = (CreatureSpawner) e.getBlockPlaced().getState();

                b.setCreatureTypeByName(name.split(" ")[2]);

                String path = "spawner." + b.getWorld().getName();


                this.config.set(path, b.getX() + ":" + b.getY() + ":" + b.getZ() + ":" + e.getPlayer().getUniqueId());

                saveConfig();
            }
        }
    }

    @EventHandler
    void BlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MOB_SPAWNER) {
            CreatureSpawner b = (CreatureSpawner) e.getBlock().getState();

            String path = "spawner." + b.getWorld().getName();
            if (!this.config.contains(path)) {
                return;
            }

            String sL = this.config.getString(path);

            Player p = e.getPlayer();

            int size = 0;
            String[] s = sL.split(":");
            if ((s.length == 4) && (s[0].equals(b.getX())) && (s[1].equals(b.getY())) && (s[2].equals(b.getZ()))) {
                if ((UUID.fromString(s[3]).equals(p.getUniqueId())) || (p.hasPermission("spawner.break"))) {
                    if (this.pickaxe.contains(e.getPlayer().getItemInHand().getType())) {
                        ItemStack mob = new ItemStack(Material.MOB_SPAWNER, 1);

                        ItemMeta im = mob.getItemMeta();

                        im.setDisplayName(ChatColor.AQUA + "Spawner ? " + b.getCreatureTypeName());

                        mob.setItemMeta(im);

                        b.getWorld().dropItemNaturally(b.getLocation(), mob);
                    }
                    e.setExpToDrop(0);
                } else {
                    p.sendMessage(ChatColor.DARK_RED + "Ce n'est pas ton spawner !");
                    e.setCancelled(true);
                }
            }
            this.config.set(path, sL);
            saveConfig();
        }
    }
}

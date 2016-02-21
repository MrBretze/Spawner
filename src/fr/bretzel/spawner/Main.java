package fr.bretzel.spawner;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by MrBretzel on 13/08/2015.
 */
public class Main extends JavaPlugin {

    public static JSONObject object;
    private static Map json = new HashMap<>();
    public static File config;
    public static List<Spawner> spawners = new ArrayList<>();
    public static List<Material> pickaxes = new ArrayList<>();
    public static HashMap<String, CreatureType> mobs = new HashMap<>();
    public static List<String> irons = new LinkedList<>();


    @Override
    public void onLoad() {
        config = new File(getDataFolder(), "spawner.json");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        if (!config.exists() || config == null) {
            try {
                config.createNewFile();
                FileWriter writer = new FileWriter(config);
                writer.write("{}");
                writer.flush();
                writer.close();
            } catch (IOException e) {}
        }

        JSONParser parser = new JSONParser();

        try {
            json = (Map) parser.parse(new FileReader(config));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        object = new JSONObject(json);
    }

    @Override
    public void onEnable() {

        pickaxes.add(Material.GOLD_PICKAXE);
        pickaxes.add(Material.IRON_PICKAXE);
        pickaxes.add(Material.DIAMOND_PICKAXE);
        pickaxes.add(Material.STONE_PICKAXE);

        irons.add("irongolem");
        irons.add("golem");
        irons.add("vilagergolem");

        for (CreatureType type : CreatureType.values()) {
            mobs.put(type.getName().toLowerCase(), type);
        }

        for (Object o : object.keySet()) {
            String uuid = (String) o;
            if (isAValidUUID(uuid) && object.containsKey(uuid)) {
                if (json.get(uuid) instanceof JSONArray) {
                    JSONArray array = (JSONArray) json.get(uuid);
                    UUID id = UUID.fromString(uuid);
                    Spawner spawner = new Spawner(id);
                    for (int i = 0; i < array.size(); i++) {
                        String s = (String) array.get(i);
                        spawner.addSpawner(toLocationString(s));
                        spawners.add(spawner);
                    }
                }
            }
        }

        getServer().getPluginManager().registerEvents(new Event(), this);

        getCommand("spawner").setExecutor(new Command());
    }

    @Override
    public void onDisable() {

        object = new JSONObject();

        for (Spawner spawner : spawners) {
            String key = spawner.getOwner().toString();
            JSONArray array = new JSONArray();
            for (Location location : spawner.getLocations()) {
                if (location.getBlock().getState() instanceof CreatureSpawner) {
                    array.add(toStringLocation(location));
                }
            }
            object.put(key, array);
        }

        JsonElement element = new com.google.gson.JsonParser().parse(object.toJSONString());

        String js = new GsonBuilder().setPrettyPrinting().create().toJson(element);

        write(config, js);

        json.clear();
        object.clear();
        spawners.clear();
    }

    public static Spawner getSpawnerByPlayer(Player player) {
        for (Spawner s : spawners) {
            if (!Objects.equals(s.getOwner().toString(), player.getUniqueId().toString())) continue;
            return s;
        }
        Spawner spawner = new Spawner(player);
        spawners.add(spawner);
        return spawner;
    }

    public List<Spawner> getSpawners() {
        return spawners;
    }

    public static String fileToJson(InputStream stream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(new String(line.getBytes(), Charset.forName("UTF-8")).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (builder == null || builder.toString().equals("")) {
            builder.append("{}");
        }

        return builder.toString();
    }

    public static Spawner getSpawnerByCreatureSpawner(CreatureSpawner spawner) {
        for (Spawner sp : spawners) {
            for (Location l : sp.getLocations()) {
                if (l.getWorld() == spawner.getLocation().getWorld() &&
                        l.getBlockX() == spawner.getLocation().getBlockX() &&
                        l.getBlockY() == spawner.getLocation().getBlockY() &&
                        l.getBlockZ() == spawner.getLocation().getBlockZ()) {
                    return sp;
                }
            }
        }
        return null;
    }

    public static boolean isAValidUUID(String uuid) {
        return uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
    }

    public static Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]), Double.valueOf(strings[1]),
                Double.valueOf(strings[2]),
                Double.valueOf(strings[3]),
                Float.valueOf(strings[4]),
                Float.valueOf(strings[5]));
    }

    public static String toStringLocation(Location location) {
        return (location.getWorld().getName() + ";") +
                location.getBlockX() + ";" +
                location.getBlockY() + ";" +
                location.getBlockZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch() + "";
    }

    public void write(File file, String value) {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter output = new BufferedWriter(fw);
            output.write(value);
            output.flush();
            output.close();
        } catch (IOException ioe) {
        }
    }
}

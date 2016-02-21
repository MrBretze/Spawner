package fr.bretzel.spawner;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by MrBretzel on 13/08/2015.
 */

public class Spawner {

    private UUID owner;
    private List<Location> locations = new ArrayList<>();

    public Spawner(Player player) {
        this(player.getUniqueId());
    }

    public Spawner(UUID uuid) {
        setOwner(uuid);
    }

    public void addSpawner(Location location) {
        for (Location l : getLocations()) {
            if (l.getWorld() == location.getWorld() &&
                    l.getBlockX() == location.getBlockX() &&
                    l.getBlockY() == location.getBlockY() &&
                    l.getBlockZ() == location.getBlockZ()) {
                return;
            }
        }
        getLocations().add(location);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public int getSize() {
        return locations.size();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public boolean contains(Location location) {
        for (Location l : getLocations()) {
            if (l.getWorld() == location.getWorld() &&
                    l.getBlockX() == location.getBlockX() &&
                    l.getBlockY() == location.getBlockY() &&
                    l.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public void remove(Location location) {
        for (int i = 0; i < getLocations().size(); i++) {
            Location l = getLocations().get(i);
            if (l.getWorld() == location.getWorld() &&
                    l.getBlockX() == location.getBlockX() &&
                    l.getBlockY() == location.getBlockY() &&
                    l.getBlockZ() == location.getBlockZ()) {
                getLocations().remove(i);
                return;
            }
        }
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}

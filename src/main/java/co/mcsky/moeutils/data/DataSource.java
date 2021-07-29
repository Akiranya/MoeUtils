package co.mcsky.moeutils.data;

import org.bukkit.Location;

import java.util.*;

public class DataSource {

    private final List<Location> targetLocations;
    private final Map<String, List<Location>> targetLocationMap;

    public DataSource() {
        targetLocations = new ArrayList<>();
        targetLocationMap = new HashMap<>();
    }

    private void addToEndEyeTargetMap(Location location) {
        if (targetLocationMap.containsKey(location.getWorld().getName())) {
            targetLocationMap.get(location.getWorld().getName()).add(location);
        } else {
            targetLocationMap.put(location.getWorld().getName(), new ArrayList<>() {{
                add(location);
            }});
        }
    }

    public void addEndEyeTargetLocation(Location location) {
        targetLocations.add(location);
        addToEndEyeTargetMap(location);
    }

    public void addEndEyeTargetLocation(Collection<? extends Location> locations) {
        targetLocations.addAll(locations);
        targetLocations.forEach(this::addToEndEyeTargetMap); // sync to hash map for faster condition check
    }

    public void clearTargetLocations() {
        targetLocationMap.clear();
        targetLocations.clear();
    }

    public List<Location> getEndEyeTargetLocationsByWorld(String world) {
        return targetLocationMap.get(world);
    }

    public boolean containsEndEyeTargetWorld(String world) {
        return targetLocationMap.containsKey(world);
    }

    public List<Location> getEndEyeTargetLocations() {
        return targetLocations;
    }
}

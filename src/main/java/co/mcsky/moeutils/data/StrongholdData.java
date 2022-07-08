package co.mcsky.moeutils.data;

import org.bukkit.Location;

import java.util.*;

public class StrongholdData {

    private final List<Location> strongholdLocationList;
    private final Map<String, List<Location>> strongholdLocationMap;

    public StrongholdData() {
        strongholdLocationList = new ArrayList<>();
        strongholdLocationMap = new HashMap<>();
    }

    private void addToEndEyeTargetMap(Location location) {
        if (strongholdLocationMap.containsKey(location.getWorld().getName())) {
            strongholdLocationMap.get(location.getWorld().getName()).add(location);
        } else {
            strongholdLocationMap.put(location.getWorld().getName(), new ArrayList<>() {{
                add(location);
            }});
        }
    }

    public void addEndEyeTargetLocation(Location location) {
        strongholdLocationList.add(location);
        addToEndEyeTargetMap(location);
    }

    public void addEndEyeTargetLocation(Collection<? extends Location> locations) {
        strongholdLocationList.addAll(locations);
        strongholdLocationList.forEach(this::addToEndEyeTargetMap); // sync to hash map for faster condition check
    }

    public void clearTargetLocations() {
        strongholdLocationMap.clear();
        strongholdLocationList.clear();
    }

    public List<Location> getEndEyeTargetLocationsByWorld(String world) {
        return strongholdLocationMap.get(world);
    }

    public boolean containsEndEyeTargetWorld(String world) {
        return strongholdLocationMap.containsKey(world);
    }

    public List<Location> getEndEyeTargetLocations() {
        return strongholdLocationList;
    }

}

package co.mcsky.moeutils.data;

/**
 * Represents a datasource in memory.
 */
public class Datasource {

    private final StrongholdData strongholdData;

    public Datasource() {
        strongholdData = new StrongholdData();
    }

    public StrongholdData getEndPortals() {
        return strongholdData;
    }

}

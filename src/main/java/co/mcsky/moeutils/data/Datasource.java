package co.mcsky.moeutils.data;

/**
 * Represents a datasource in memory.
 */
public class Datasource {

    private final EndPortalsData endPortalsData;

    public Datasource() {
        endPortalsData = new EndPortalsData();
    }

    public EndPortalsData getEndPortalsData() {
        return endPortalsData;
    }

}

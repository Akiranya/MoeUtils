package co.mcsky.moeutils.external;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class MoePlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "moeutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Nailm";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

}

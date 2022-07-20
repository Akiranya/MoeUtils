package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import com.google.common.collect.ImmutableMap;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.function.Supplier;

public class CommandFireball implements Supplier<CommandAPICommand> {

    private final MewUtils plugin;

    public static final String FIREBALL_META_KEY = "mew_fireball_proj";
    public static final Map<String, Class<? extends Projectile>> types;
    public static final String[] typesArray;

    static {
        final ImmutableMap.Builder<String, Class<? extends Projectile>> builder = ImmutableMap.<String, Class<? extends Projectile>>builder()
                .put("fireball", Fireball.class)
                .put("small", SmallFireball.class)
                .put("large", LargeFireball.class)
                .put("arrow", Arrow.class)
                .put("skull", WitherSkull.class)
                .put("egg", Egg.class)
                .put("snowball", Snowball.class)
                .put("expbottle", ThrownExpBottle.class)
                .put("dragon", DragonFireball.class)
                .put("trident", Trident.class);

        types = builder.build();
        typesArray = types.keySet().toArray(new String[0]);
    }

    public CommandFireball(MewUtils plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("fireball")
                .withArguments(new StringArgument("projectile")
                        .replaceSuggestions(ArgumentSuggestions.strings(typesArray))
                )
                .withArguments(new DoubleArgument("speed"))
                .withArguments(new BooleanArgument("ride"))
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                    String proj = types.containsKey((String) args[0]) ? (String) args[0] : "fireball";
                    double speed = (double) args[1];
                    boolean ride = (boolean) args[2];
                    Player player = (Player) args[3];
                    launch(proj, speed, ride, player);
                });
    }

    private void launch(String proj, double speed, boolean ride, LivingEntity player) {
        speed = Double.max(0, Double.min(speed, 8));
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        final Projectile projectile = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), types.get(proj));
        projectile.setShooter(player);
        projectile.setVelocity(direction);
        projectile.setMetadata(FIREBALL_META_KEY, new FixedMetadataValue(plugin, true));

        if (ride) {
            projectile.addPassenger(player);
        }
    }
}

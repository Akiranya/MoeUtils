package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import com.google.common.collect.ImmutableMap;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.lucko.helper.Schedulers;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CommandFireball implements Supplier<List<CommandAPICommand>> {

    public static final String FIREBALL_META_KEY = "mew_fireball_proj";
    public static final int MAX_SPEED = 8;
    public static final Map<String, Class<? extends Projectile>> types;
    public static final String[] typesArray;
    public static final String[] speedsArray;

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
        typesArray = types.keySet().toArray(String[]::new);
        speedsArray = IntStream.range(0, MAX_SPEED + 1).boxed().map(String::valueOf).toArray(String[]::new);
    }

    @Override
    public List<CommandAPICommand> get() {
        final Argument<String> projArg = new StringArgument("projectile").replaceSuggestions(ArgumentSuggestions.strings(typesArray));
        final Argument<Double> speedArg = new DoubleArgument("speed", 0, MAX_SPEED).replaceSuggestions(ArgumentSuggestions.strings(speedsArray));
        final BooleanArgument rideArg = new BooleanArgument("ride");

        final CommandAPICommand executesConsole = new CommandAPICommand("fireball")
                .withPermission("mew.admin")
                .withArguments(projArg, speedArg, rideArg)
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {
                    String proj = types.containsKey((String) args[0]) ? (String) args[0] : "fireball";
                    double speed = (double) args[1];
                    boolean ride = (boolean) args[2];
                    Player player = (Player) args[3];
                    launch(proj, speed, ride, player);
                });
        final CommandAPICommand executesPlayer = new CommandAPICommand("fireball")
                .withPermission("mew.admin")
                .withArguments(projArg, speedArg, rideArg)
                .executesPlayer((sender, args) -> {
                    String proj = types.containsKey((String) args[0]) ? (String) args[0] : "fireball";
                    double speed = (double) args[1];
                    boolean ride = (boolean) args[2];
                    launch(proj, speed, ride, sender);
                });

        return List.of(executesConsole, executesPlayer);
    }

    private void launch(String proj, double speed, boolean ride, LivingEntity player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        final Projectile projectile = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), types.get(proj));
        projectile.setShooter(player);
        projectile.setVelocity(direction);
        projectile.setMetadata(FIREBALL_META_KEY, new FixedMetadataValue(MewUtils.p, true));

        if (ride) {
            projectile.addPassenger(player);
        }

        Schedulers.sync().runLater(projectile::remove, 100);
    }
}

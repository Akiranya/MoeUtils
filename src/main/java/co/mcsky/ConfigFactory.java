package co.mcsky;

import co.mcsky.config.*;
import co.mcsky.config.reference.EntityValues;
import co.mcsky.config.reference.MaterialValues;
import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class ConfigFactory {

    // All config goes here
    @Getter private BeesConfig beesConfig;
    @Getter private CommonConfig commonConfig;
    @Getter private CreatureDeathLoggerConfig creatureDeathLoggerConfig;
    @Getter private FoundDiamondsConfig foundDiamondsConfig;
    @Getter private MagicTimeConfig magicTimeConfig;
    @Getter private MagicWeatherConfig magicWeatherConfig;
    @Getter private MobArenaProConfig mobArenaProConfig;
    @Getter private SafePortalConfig safePortalConfig;

    public ConfigFactory(MoeUtils moe) {
        try {
            commonConfig = new CommonConfig(moe);
            commonConfig.init();
            beesConfig = new BeesConfig(moe);
            beesConfig.init();
            creatureDeathLoggerConfig = new CreatureDeathLoggerConfig(moe);
            creatureDeathLoggerConfig.init();
            foundDiamondsConfig = new FoundDiamondsConfig(moe);
            foundDiamondsConfig.init();
            magicTimeConfig = new MagicTimeConfig(moe);
            magicTimeConfig.init();
            magicWeatherConfig = new MagicWeatherConfig(moe);
            magicWeatherConfig.init();
            mobArenaProConfig = new MobArenaProConfig(moe);
            mobArenaProConfig.init();
            safePortalConfig = new SafePortalConfig(moe);
            safePortalConfig.init();

            MaterialValues materialValues = new MaterialValues(moe);
            materialValues.init();
            EntityValues entityValues = new EntityValues(moe);
            entityValues.init();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}

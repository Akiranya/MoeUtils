package co.mcsky.moeutils.utilities;

/**
 * Contains chinese localization for all damage causes.
 */
public enum DamageCauseLocalization {

    BLOCK_EXPLOSION("方块爆炸"),
    CONTACT("触碰"),
    CRAMMING("拥挤"),
    CUSTOM("自定义"),
    DRAGON_BREATH("龙息"),
    DROWNING("淹死"),
    DRYOUT("干渴"),
    ENTITY_ATTACK("生物攻击"),
    ENTITY_EXPLOSION("生物爆炸"),
    ENTITY_SWEEP_ATTACK("生物横扫攻击"),
    FALL("跌落"),
    FALLING_BLOCK("被方块压死"),
    FIRE("火烧"),
    FIRE_TICK("火烧"),
    FLY_INTO_WALL("撞墙"),
    HOT_FLOOR("高温地板"),
    LAVA("岩浆"),
    LIGHTNING("闪电"),
    MAGIC("魔法"),
    MELTING("融化"),
    POISON("中毒"),
    PROJECTILE("弹射物"),
    STARVATION("饥饿"),
    SUFFOCATION("窒息"),
    SUICIDE("自杀"),
    THORNS("荆棘"),
    VOID("虚空"),
    WITHER("凋零");

    private final String localization;

    DamageCauseLocalization(String localization) {
        this.localization = localization;
    }

    public String getLocalization() {
        return this.localization;
    }

}

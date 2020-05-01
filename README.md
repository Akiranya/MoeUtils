# MoeUtils

一个基于 Bukkit 的小功能合集插件，专门为 [上古时代服务器](https://mimaru.me) 定制。

# Features

- **FoundOres**
  - 当玩家挖到指定的方块时，通报全服该玩家挖到的种类和对应数量
  - 可配置通报哪些方块种类
  - 可配置哪些世界启用通报
  - 可配置单次最多通报的数量
- **MagicUtils**
  - 修改世界时间和天气的小功能
  - 可配置使用间隔
  - 可配置使用花费
- **MobArena-Addon**
  - 玩家头上会显示当前血量
  - 友军射出去的箭不会被友军（可配置友军的实体类型）阻挡
  - 该功能只会对竞技场内的玩家有效，不会影响之外的玩家
- **DeathLogger**
  - 公示指定实体的死亡信息
  - 如果实体不是被玩家直接击杀，则会通报附近的玩家
  - 可配置附近玩家的搜索半径
  - 可配置提醒哪些实体
- **BetterPortals**
  - 当玩家使用地狱传送门时，如果目的地在世界的边界外，则取消传送并告知玩家
  - 这可以防止玩家传送到地图边界外被系统秒杀，导致玩家无法拾取他们的遗产
  - 可配置是否启用该功能
- **BetterBees**
  - 空手右击蜂箱/蜂巢可以查看内部的蜜蜂数量
  - 把蜂箱/蜂巢拿在手上右击空气也可查看内部的蜜蜂数量
  - 可配置是否需要按住 Shift 键才能使用该功能

# Dependencies

MoeUtils 需要以下前置插件：

- [Vault](https://github.com/MilkBowl/Vault)
- [LangUtils](https://github.com/MascusJeoraly/LanguageUtils)

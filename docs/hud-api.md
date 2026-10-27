# HUD API

以 Minecraft 1.21.1 的 `Gui`、`ChatListener`、`ServerBossEvent`、`BossHealthOverlay`、`PlayerTabOverlay` 和 `ToastComponent` 为依据。RIC 提供显示与同步，模组负责触发条件、自己的翻译和自定义 Toast 注册。所有新增类型均为顶层类型。

## 通用注册与文本

在模组的公共初始化路径注册一次，客户端和服务端使用相同 ID。MITE 的自定义包通道上限为 20 个字符，包含冒号和命名空间；通道 ID 与翻译键相互独立。

```java
public static final HudChannel HUD = HudChannel.register(new ResourceLocation("example", "hud"));

HUD.showActionBar(player, TextComponent.translatable("example.hint.ingredients", 3));
HUD.showSystemMessage(player, TextComponent.translatable("example.message.ready"));
HUD.displayMessage(player, TextComponent.literal("Message"), true);
```

公共 API 位于 `moddedmite.rustedironcore.api.hud`；客户端 API 位于 `moddedmite.rustedironcore.client.api.hud`。文本使用现有的 `api.chat.TextComponent`，保留颜色、样式、拼接、嵌套翻译参数和按键名称。具有命令上下文的文本在服务器为目标玩家解析；普通翻译在客户端按玩家语言显示。

在服务端调用会发送给指定玩家；在客户端调用只影响本地玩家。一次交互选择一端触发，避免在两端各显示一次。各通道共用原版显示位置，后来的提示覆盖同一位置的旧提示；RIC 扩展的自定义 Hint 按锚点与偏移区分显示位置。

`HotbarHint` 保留为精简入口，内部复用 `HudChannel`。同一个通道 ID 只注册一次；选择 `HotbarHint.register` 或 `HudChannel.register`，不要重复注册。

## 快捷栏提示

Action Bar 保留 MITE 与 1.21.1 一致的位置和计时路径：持续 60 tick，最后 20 tick 淡出，可选原版彩色动画；绘制文本时补齐 1.21.1 的阴影和背景。`HudChannel` 默认不抑制重复显示；四参数重载可指定重复间隔，`HotbarHint` 默认间隔 10 tick。`clearActionBar` 清空当前提示。

1.21.1 的 `drawStringWithBackdrop` 同时用于 Action Bar、标题和自定义 Hint。默认背景透明，对应原版“背景仅用于聊天”的默认设置；客户端可用 `Hud.setTextBackgroundOpacity(0.5F)` 设置全部这些提示的背景不透明度（0 到 1）。背景边距为 2 像素，并随文本一起淡出。该设置不改动 MITE 自己的聊天背景。

## 自定义 Hint 位置（RIC 扩展）

`HotbarHint.register(id)` 和 `register(id, repeatDelayTicks)` 继续使用原版快捷栏上方位置。注册时可增加 `HintPosition`，选择预设或设置自己的锚点和偏移：

```java
public static final HotbarHint HINT = HotbarHint.register(
        new ResourceLocation("example", "hint"),
        HintPosition.TOP_CENTER.offset(0, 12));

HINT.showTranslated(player, "example.hint.ingredients", 3);
HINT.show(player, TextComponent.translatable("example.hint.ready"));
HINT.clear(player);
```

也可用 `register(id, position, repeatDelayTicks)` 指定重复间隔。位置会随提示从服务器传输，MOD 不需要另写网络包或渲染类。

| 预设 | 位置 |
| --- | --- |
| `HOTBAR` | 原版快捷栏上方 |
| `TOP_LEFT` / `TOP_CENTER` / `TOP_RIGHT` | 屏幕顶部左侧、居中、右侧 |
| `CENTER_LEFT` / `CENTER` / `CENTER_RIGHT` | 屏幕中部左侧、居中、右侧 |
| `BOTTOM_LEFT` / `BOTTOM_CENTER` / `BOTTOM_RIGHT` | 屏幕底部左侧、居中、右侧 |

九宫格预设距相应屏幕边缘 8 个 GUI 像素。`offset(x, y)` 在预设基础上增加偏移，正 X 向右，正 Y 向下；上述 `TOP_CENTER.offset(0, 12)` 因而在顶部下方 20 像素处。完全自定义可用 `new HintPosition(HintAnchor.TOP_RIGHT, -12, 24)`，将文本右边缘放在屏幕右侧内缩 12 像素、顶部下方 24 像素处。左侧锚点按文本左边缘对齐，右侧按右边缘对齐，中间按文本中心对齐。位置基于缩放后的 GUI 尺寸，窗口和 GUI 缩放改变时会重新计算。

`HOTBAR` 无偏移时仍由 MITE 原生 Action Bar 绘制，保留原版共享槽位行为。其他位置（包括带偏移的 `HOTBAR`）由 RIC 绘制，沿用原版 60 tick 生命周期、最后 20 tick 淡出和文本样式；隐藏 HUD 或幽灵状态时不绘制。

相同锚点和偏移共用一个自定义提示槽位，新提示覆盖旧提示；不同位置可同时显示。自定义槽位的重复间隔按当前通道和文本判断，`clear` 只清除本通道当前占用的槽位，不会清除其他通道刚覆盖的新提示。RIC 不自动挪动提示避开聊天、Boss 血条、标题或 Toast，MOD 应通过预设与偏移选择合适的位置。

已注册 `HudChannel` 的 MOD 可直接调用 `HUD.showHint(player, text, position)` 或带重复间隔的四参数重载，并用 `HUD.clearHint(player, position)` 清空；不要为它重复注册相同 ID 的 `HotbarHint`。

## 标题

```java
HUD.setTitleTimes(player, 10, 70, 20);
HUD.setSubtitle(player, TextComponent.translatable("example.subtitle"));
HUD.setTitle(player, TextComponent.translatable("example.title"));
HUD.clearTitles(player, false);
```

标题使用原版中央位置、大标题 4 倍和副标题 2 倍比例、淡入/停留/淡出。默认时长为 10/70/20 tick。修改时间时传负数会保留该项；更新副标题不重启计时，更新大标题会重启。`clearTitles(player, true)` 同时恢复默认时间。客户端可通过 `Hud.getTitles()` 直接操作这些状态。

## Tab 页眉页脚

```java
HUD.setTabHeaderAndFooter(player,
        TextComponent.translatable("example.tab.header"),
        TextComponent.translatable("example.tab.footer"));
HUD.clearTabList(player);
```

文字支持换行和按窗口宽度自动换行，参与背景宽度和列表竖直位置计算。玩家行继续由 MITE 绘制，保留其等级、计分板、队伍名和延迟信息。显示条件沿用 MITE 的玩家列表规则。客户端入口为 `Hud.getTabList()`。

## Boss 血条

```java
ServerBossBar boss = HUD.createBossBar(
        TextComponent.translatable("example.boss.name"),
        BossBarColor.PURPLE, BossBarOverlay.NOTCHED_10);
boss.addPlayer(serverPlayer);
boss.setProgress(0.75F);
boss.setDarkenScreen(true).setCreateWorldFog(true).setPlayBossMusic(true);
boss.setVisible(false);
boss.removePlayer(serverPlayer);
boss.removeAllPlayers();
```

调用者负责维护玩家订阅，例如实体开始/停止追踪时添加/移除玩家，实体移除时清理订阅。不可变 UUID 标识每条血条；名字、进度、样式和环境属性分别增量同步，隐藏期间保留状态，再显示时发送完整状态。

七种颜色为粉、蓝、红、绿、黄、紫、白；样式为连续、6/10/12/20 段。客户端进度按原版在 100 毫秒内插值，按加入顺序排列，超过屏幕高度三分之一时停止绘制。MITE 原有 Boss 血条仍保留，新血条从其下方开始。雾沿用 MITE 的环境雾分支，避免覆盖失明或流体雾；Boss 音乐与原版一样仅在末地选择。客户端自定义血条可用 `Hud.getBosses().add(new ClientBossBar(...))`。

## Toast 与自定义通知

```java
HUD.showToast(player, new SystemToastData(
        new ResourceLocation("example", "notice"),
        TextComponent.translatable("example.notice.title"),
        TextComponent.translatable("example.notice.message")));
HUD.removeToast(player, new ResourceLocation("example", "notice"));
```

系统通知默认显示 5000 毫秒，可通过完整 `SystemToastData` 构造器指定显示时间和多行模式。相同 token 更新现有系统通知；只有标题时按 1.21.1 在通知内垂直居中，客户端可调用 `SystemToast.forceHide()` 提前隐藏。队列沿用原版的五个 32 像素槽位，支持占用多个连续槽位、600 毫秒平方曲线动画和原版进出音效。通知从屏幕右上方出现，也可在菜单界面绘制；隐藏 HUD 时停止绘制。

客户端可直接添加 `SystemToast`、`AdvancementToast`、`RecipeToast`、`TutorialToast`，或实现 `Toast`。进度样式包含挑战音效和长标题切换，配方样式合并并轮换图标，教程样式支持图标、进度条和手动隐藏。它们提供显示能力，模组继续负责自己的成就、配方解锁和教程逻辑。

```java
Hud.getToasts().addToast(new AdvancementToast(
        new ResourceLocation("example", "achievement"),
        TextComponent.translatable("example.achievement.name"),
        iconStack, AdvancementToastFrame.CHALLENGE));
```

需要从服务器发送自定义 Toast 时，在公共代码声明 `ToastType<T>` 的读取和写入函数，在客户端初始化时注册工厂。自定义数据由模组定义；其中含选择器、计分板或 NBT 等命令上下文文本时，调用者应在发送前使用 `ChatComponents.resolveFor(text, source, player)` 解析。`showToast(player, SystemToastData)` 已自动完成标题和正文的上下文解析，普通翻译仍留到客户端处理。

```java
public static final ToastType<MyNotice> NOTICE = new ToastType<>(
        new ResourceLocation("example", "notice"),
        MyNotice::read, (buffer, notice) -> notice.write(buffer));

// 仅客户端初始化：MyToast 实现 Toast。
Hud.getToasts().registerType(NOTICE, MyToast::new);

// 服务端：复用 HUD 通道，不需要另写网络包。
HUD.showToast(serverPlayer, NOTICE, notice);
```

`Toast.render` 接收 `HudGraphics`、管理器和完全滑入后的已显示毫秒数，返回 `SHOW` 或 `HIDE`。可自定义尺寸、token、文字、纹理、物品图标和显示条件；高度按 32 像素向上取整，最多占用五个槽位。`HudGraphics` 提供结构化文本绘制、换行、任意尺寸贴图 UV、矩形、物品和矩阵接口。`Hud.getToasts().setNotificationDisplayTimeMultiplier(...)` 调整原版通知模板的时长。

## 生命周期与资源

断开世界时清理标题、Boss、Tab、Toast 和自定义 Hint 的运行状态，保留已注册的 Toast 工厂；跨维度不清除服务端仍在使用的显示状态。

原版贴图和音频保持字节不变：使用 1.21.1 的 33 张独立 GUI sprite，位于 `assets/ric/textures/gui/sprites/boss_bar/` 和 `assets/ric/textures/gui/sprites/toast/`，不再使用旧的 `bars.png`、`toasts.png` 图集。MITE 的绘制桥接直接使用各文件的原始尺寸、裁剪坐标和 UV；Boss 进度宽度按 1.21.1 的 `Mth.lerpDiscrete(progress, 0, 182)` 计算，满进度为 182 像素。

音频位于 `assets/ric/sounds/ui/toast/` 和 `assets/ric/sounds/music/game/end/boss.ogg`。它们可通过资源包覆盖。音频从本地 1.21.1 安装引用的 asset index 17 提取并校验 SHA-1、长度，音效复用 RIC 的资源声音播放路径，音乐使用 MITE 的音乐音量和背景音乐源。

参考源码：本地 Minecraft 1.21.1 官方映射反编译源码中的 `Gui`、`GuiGraphics`、`ChatListener`、`Toast`、`ToastComponent`、`SystemToast`、`TutorialToast`、`AdvancementToast`、`RecipeToast`、`BossEvent`、`ServerBossEvent`、`BossHealthOverlay`、`LerpingBossEvent`、`PlayerTabOverlay` 和 `Mth`。已核对消息路由、60 tick 提示计时、10/70/20 标题计时、100 毫秒 Boss 插值、600 毫秒 Toast 动画以及 Tab 页眉页脚布局。NeoForge 的专属事件钩子不作为原版接口移植；玩家行中的 MITE 等级、队伍、计分板与延迟继续由 MITE 绘制。

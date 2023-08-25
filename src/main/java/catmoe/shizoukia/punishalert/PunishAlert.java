package catmoe.shizoukia.punishalert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

@SuppressWarnings("unused")
public final class PunishAlert extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[PunishAlert] Enabled! Version: " + getDescription().getVersion() + " By Shizoukia");
    }

    /*
    关于静态(static) :
    一些常见的例子就是直接引用此插件的instance.

    静态的值将会一直保存在内存中
    用于方便存储从别的类或者需要跨实例访问的内容

    "静态" 实际上不是"静止"或者其它smth之类的东西
    正如上面提到的, 它们所作的就是固定它们在内存里独一无二的位置

    静态对象初始化的赋值必须为null或其它静态对象,
    因为在类加载或者有东西尝试读取的话将无法读取 这还是有些符合逻辑的?

    补充:
    方法也可以为静态的哦 只需要在方法前面(声明封装的public/private后面)加上static就可以为静态了
    超类方法不可为静态 (似乎?)
    在实际运用中尽可能传递插件主类 减少对于静态的滥用 因为静态值永远不会垃圾回收 大量使用静态可能会导致内存使用量剧增
     */
    public static PunishAlert INSTANCE;

    /*
    onLoad和onEnable的不同之处在于
    onLoad是在插件主类方法被实例化时触发的
    onEnable方法是等Bukkit已经启动完毕时加载插件时触发的

    对于大多数东西 onLoad并不能代替onEnable方法
    因为如果在这个阶段进行操作(e.x 注册监听器) 就可能会导致"鸡还是蛋"的问题
     */
    @Override
    public void onLoad() {
        INSTANCE=this;
        super.onLoad();
    }

    // 翻译自:
    // https://github.com/CatMoe/MoeFilter/blob/stray/bungee/src/main/java/catmoe/fallencrystal/moefilter/util/message/v2/MessageUtil.kt#L89-L94
    public static StringBuilder argsBuilder(int startIndex, String[] args) {
        StringBuilder message = new StringBuilder();
        if (args != null) {
            for (int i = startIndex; i < args.length - 1; i++) { message.append(args[i]).append(" "); }
        }
        message.append(args != null ? args[args.length - 1] : message);
        return message;
    }

    // 此处用来获取CONSOLE的CommandSender静态对象 以在方法中调用
    public static final CommandSender CONSOLE = Bukkit.getConsoleSender();
    public static final String unknown = "Unknown command. Type \"/help\" for help.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
        之前不是有笨蛋想知道switch是什么吗 差不多就是这样了, 当然这里没有忽略大小写的概念 所以选择将输入的字符串转换成小写.

        从Java7开始 switch可以case字符串了.
        目前可以case的对象有:
        String, Byte, Short, Int, Float, Double, Enum(自定义枚举类)
        实际上这么写也跟逐步if else没什么区别 但很明显前者可以让代码更加整洁

        可以插入default分支用来捕获case中没有列举出的情况
        switch (reallyInt) {
            case 1:
                sender.sendMessage("这是1");
            case 2:
                sender.sendMessage("这是2");
            default:
                sender.sendMessage("Shizoukia不知道哦");
        }

        但 实际上也不要指望switch能对于这种if else能有多复杂的判断或者拥有多个rule.
         */

        // 检查玩家是否拥有特定权限
        // *: 还算可以的实践 但实际上在plugin.yml中就可以指定插件权限 但也请考虑if (!sender.hasPermission("permission")) { return true } (已修改)
        // 在布尔值前面加感叹号可以反转布尔值 (颠倒黑白? 类似于true变成false false变成true之类的 *魔法*
        if (!sender.hasPermission("punishalert")) {
            sender.sendMessage(colorize(unknown));
            return true;
        }

        // 对于不需要重新赋值的对象 建议使用final关键字来声明对象不可被二次修改.
        final Player targetPlayer;
                    /*
                    关于 try-catch语句:
                    try-catch是用来捕获指定Throwable对象的 (例如Exception, Error, etc.)
                    try { runnable } catch (Class obj) { runnable }
                    例如需要抓住Exception 则为 catch (Exception e) { }
                    e则含有Exception本身带有的信息

                    当然.. 不建议直接catch住所有的Exception 这可能会导致一些潜在的副作用.
                    优先catch已经考虑到的特定问题.

                    为了养成良好的编程语法习惯, 当不需要Exception本身的信息时, 应当命名为 "ignore" 或 "safeIgnore" 等字样
                     */
        try {
            targetPlayer = Bukkit.getPlayer(args[0]);
            // 是的, 我们可以通过 throw + 方法插入Throwable对象来抛出某个异常
            // 虽然无论如何我们抛出的NPE都会被下面的catch块处理
            if (targetPlayer == null) { throw new NullPointerException(); }
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
            sender.sendMessage(colorize("&cPlayer not found or not online."));
            return true;
        }
        switch (command.getName().toLowerCase()) {
            case "punishalert":
                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /punishalert <player>");
                    return true;
                }
                // 检查玩家是否拥有特定权限
                if (!sender.hasPermission("punishalert.sendmessage")) {
                    sender.sendMessage(colorize(unknown));
                    return true;
                }

                Bukkit.getServer().broadcastMessage(
                        colorize("\n&f&l» §c§l一名玩家因违反游戏规则被移出游戏！\n&f&l» §b§l使用/report来举报违反游戏规则的玩家！\n&f&l   ")
                );
                targetPlayer.getWorld().strikeLightningEffect(targetPlayer.getLocation());
                // replace对于需要插入复杂的内容的情况下可以显得格外灵活.
                final String banCommand = "litebans:ban [player] [reason] 7d -s"
                        .replace("[player]", targetPlayer.getName())
                        .replace("[reason]", colorize("&c使用第三方程序进行作弊 &f[A]"));
                Bukkit.getServer().dispatchCommand(CONSOLE, banCommand);

            case "wipealert":
                // 如果想接收之后的reason args的长度不应该是必须等于2 应该是必须大于或等于2 (也就是不能小于1
                if (args.length < 2) {
                    sender.sendMessage(colorize("&cUsage: /wipealert <player> <reason>"));
                    return true;
                }
                // 检查玩家是否拥有特定权限
                if (!sender.hasPermission("punishalert.sendmessage")) {
                    sender.sendMessage(colorize(unknown));
                    return true;
                }
                // 稍微有些糟糕的实践注意: 如果reason需要捕获后面的更多内容 则可能需要使用StringBuilder append之后的内容 (已修改)
                // 只会捕获命令中第三个的arg 之后的就直接忽略掉了吧似乎
                final String reason;
                try {
                    // reason = args[1];
                    // 来自L56的静态方法argsBuilder, 通过String.valueOf(obj) 将类型转换成String.
                    reason = String.valueOf(argsBuilder(1, args));
                    if (reason == null || reason.isEmpty()) { throw new NullPointerException(); }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    sender.sendMessage(colorize("Please select a reason"));
                    return true;
                }

                final String wipeCommand = "wipe [player] [reason]"
                        .replace("[player]", targetPlayer.getName())
                        .replace("[reason]", reason);
                final String wipeWarn = "\n&f&l» &c&l您的账户部分游戏数据已被我们重置\n"
                        + "&f&l» &6&l原因: &f&l[reason]\n".replace("[reason]", reason)
                        + "&f&l» §b§l请遵守我们的游戏规则 不要使用违规增益\n&f&l» "
                        + "详见: &ehttps://www.miaomoe.net/rules \n&f&l   ";
                targetPlayer.sendMessage(colorize(wipeWarn));
                Bukkit.getServer().dispatchCommand(CONSOLE, wipeCommand);
        }
        return true;
    }

    private String colorize(String orig) {return ChatColor.translateAlternateColorCodes('&', orig);}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[PunishAlert] Disabled! Version " + getDescription().getVersion() + " By Shizoukia");
    }
}

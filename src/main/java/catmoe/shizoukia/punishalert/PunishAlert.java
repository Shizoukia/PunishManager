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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "punishalert":
                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /punishalert <player>");
                    return true;
                }
                // 检查玩家是否拥有特定权限
                if (sender.hasPermission("punishalert.sendmessage")) {
                    Bukkit.getServer().broadcastMessage(
                            colorize("\n&f&l» §c§l一名玩家因违反游戏规则被移出游戏！\n&f&l» §b§l使用/report来举报违反游戏规则的玩家！\n&f&l   ")
                    );

                    final Player targetPlayer;
                    try {
                        targetPlayer = Bukkit.getPlayer(args[0]);
                        if (targetPlayer == null) { throw new NullPointerException(); }
                    } catch (IndexOutOfBoundsException | NullPointerException ignore) {
                        sender.sendMessage("Player not found or not online.");
                        return true;
                    }
                    targetPlayer.getWorld().strikeLightningEffect(targetPlayer.getLocation());
                    final String c = "litebans:ban [player] [reason] 7d -s"
                            .replace("[player]", targetPlayer.getName())
                            .replace("[reason]", colorize("&c使用第三方程序进行作弊 &f[A]"));
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    sender.sendMessage("Unknown command type /help for help");
                }

            case "wipealert":
                if (args.length != 2) {
                    sender.sendMessage(colorize("&cUsage: /wipealert <player> <reason>"));
                    return true;
                }
                // 检查玩家是否拥有特定权限
                if (sender.hasPermission("punishalert.sendmessage")) {

                    final Player targetPlayer;
                    try {
                        targetPlayer = Bukkit.getPlayer(args[0]);
                        if (targetPlayer == null) { throw new NullPointerException(); }
                    } catch (IndexOutOfBoundsException | NullPointerException ignore) {
                        sender.sendMessage(colorize("&cPlayer not found or not online."));
                        return true;
                    }
                    final String reason;
                    try {
                        reason = args[1];
                        if (reason == null || reason.isEmpty()) { throw new NullPointerException(); }
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        sender.sendMessage(colorize("Please select a reason"));
                        return true;
                    }
                    final String c = "wipe [player] [reason]"
                            .replace("[player]", targetPlayer.getName())
                            .replace("[reason]", reason);
                    final String wipeWarn = "\n&f&l» &c&l您的账户部分游戏数据已被我们重置\n"
                            + "&f&l» &6&l原因: &f&l[reason]\n".replace("[reason]", reason)
                            + "&f&l» §b§l请遵守我们的游戏规则 不要使用违规增益\n&f&l» "
                            + "详见: &ehttps://www.miaomoe.net/rules \n&f&l   ";
                    targetPlayer.sendMessage(colorize(wipeWarn));
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), c);
                } else {
                    sender.sendMessage("Unknown command type /help for help");
                }
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

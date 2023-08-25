package catmoe.shizoukia.punishalertforspigot;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;

import java.util.SplittableRandom;

public final class PunishAlertForSpigot extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[PunishAlert] Enabled! Version: 1.0 By Shizoukia");

        getCommand("punishalert" + "wipealert").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("punishalert")){
//                Player player = (Player) sender;
                if (args.length != 1) {
                    sender.sendMessage("§cUsage: /punishalert <player>");
                    return true;
                }
                // 检查玩家是否拥有特定权限
                if (sender.hasPermission("punishalert.sendmessage")) {
                    String text = "\n§f§l» §c§l一名玩家因违反游戏规则被移出游戏！\n§f§l» §b§l使用/report来举报违反游戏规则的玩家！\n§f§l   ";
                    Bukkit.getServer().broadcastMessage(text);

                    Player targetPlayer = Bukkit.getPlayer(args[0]);
                    if (targetPlayer == null || !targetPlayer.isOnline()) {
                        sender.sendMessage("Player not found or not online.");
                        return true;
                    }
                    targetPlayer.getWorld().strikeLightningEffect(targetPlayer.getLocation());
                    String playerName = targetPlayer.getName();
                    String BanReason = "§c使用第三方程序进行作弊 §f[A]";
                    String BanCommand = "litebans:ban"+ " " + playerName + " " + BanReason + " " + "7d" + " " + "-s";
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), BanCommand );
                } else {
                    sender.sendMessage("Unknown command type /help for help");
                }
            }

        if (command.getName().equalsIgnoreCase("wipealert")){
//                Player player = (Player) sender;
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /wipealert <player> <reason>");
                return true;
            }
            // 检查玩家是否拥有特定权限
            if (sender.hasPermission("punishalert.sendmessage")) {

                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    sender.sendMessage("Player not found or not online.");
                    return true;
                }
                String wipecommandreason = args[1];
                if (wipecommandreason.isEmpty()) {
                    sender.sendMessage("Please Enter Reason");
                } else {
                    String playerName = targetPlayer.getName();
                    String wipeCommmand = "wipe " + playerName + wipecommandreason;
                    String WipeReason = "\n§f§l» §c§l您的账户部分游戏数据已被我们重置\n"+ "§f§l» §6§l原因: §f§l"+ wipecommandreason + "\n" + "§f§l» §b§l请遵守我们的游戏规则 不要使用违规增益\n§f§l» 详见: §ehttps://www.miaomoe.net/rules \n§f§l   ";
                    targetPlayer.sendMessage(WipeReason);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), wipeCommmand );}
            } else {
                sender.sendMessage("Unknown command type /help for help");
            }
        }
        return false;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[PunishAlert] Disabled! Version" + " ${project.version} " + "By Shizoukia");
    }
}

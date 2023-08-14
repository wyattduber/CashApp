package doubleyoucash.eaplugin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import doubleyoucash.eaplugin.CashApp;

public class BCE implements CommandExecutor {

    private final CashApp ca;
    private final List<String> modList;
    private final List<String> modPlusList;
    private final ConsoleCommandSender console;

    public BCE() {
        ca = CashApp.getPlugin();
        console = Bukkit.getServer().getConsoleSender();
        modList = ca.modList;
        modPlusList = ca.modPlusList;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check args length
        if(args.length != 2) {
            return false;
        }

        //Check to make sure that only console is executing the command
        if (sender instanceof Player player) {
            player.sendMessage("§cCommand must be executed from console!");
            return true;
        }

        /*
         * Dictionary:
         * 0 = Custom Title (Mod+)
         * 1 = Custom Title Replacement (Mod+)
         * 2 = Custom Weapon Name/Lore (Mod+)
         * 3 = Public Warp (Mod/Mod+)
         * 4 = Warp Move/Rename (Mod/Mod+)
         * 5 = Mob Spawner (Mod/Mod+)
         */
        switch (args[0]) {
            case "-1" -> {
                for (String value : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " reminded mall renters of re-rental period.");
                }
                for (String value : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " reminded mall renters of re-rental period.");
                }
            }
            case "0" -> {
                for (String value : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " has purchased a Custom Title.");
                }
            }
            case "1" -> {
                for (String user : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + user + " " + args[1] + " has purchased a Custom Title Replacement.");
                }
            }
            case "2" -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Custom Weapon Name/Lore.");
                }
            }
            case "3" -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
            }
            case "4" -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
            }
            case "5" -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
            }
        }

        sender.sendMessage("Buycraft Event " + args[0] + " Sent!");
        return true;
    }

}

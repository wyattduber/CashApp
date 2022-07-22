package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BCE implements CommandExecutor {

    private String[] modPlusList = { "Dronox", "Cubemaster02", "GreenCreepyGhost", "thenameisKO", "Wcash" };
    private String[] modList = { "Cobethel", "PokedonGCG", "EchoingStar", "Anjulah", "Deizhor", "Scoutblade" };
    private final CashApp ca;
    private final ConsoleCommandSender console;
    private final Permission perms;
    private final List<UUID> staff;

    public BCE() {
        ca = CashApp.getPlugin();
        console = Bukkit.getServer().getConsoleSender();
        perms = ca.perms;
        staff = ca.staffUUID;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check args length
        if(args.length != 2) {
            return false;
        }

        // Check to make sure that only console is executing the command
        /*if (sender instanceof Player player) {
            player.sendMessage("§cCommand must be executed from console!"); Removed for Testing
        }*/

        // Initialize Staff Lists
        /*for (UUID uuid : staff) {
            Player player = ca.getServer().getOfflinePlayer(uuid).getPlayer();
            if (perms.getPrimaryGroup(player).equalsIgnoreCase("Moderator+")) {
                modPlusList.add(player);
            } else if (perms.getPrimaryGroup(player).equalsIgnoreCase("Moderator")) {
                modList.add(player);
            }
        }*/

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
            case "0":
                for (String value : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + value + " " + args[1] + " has purchased a Custom Title.");
                }
                break;
            case "1":
                for (String user : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + user + " " + args[1] + " has purchased a Custom Title Replacement.");
                }
                break;
            case "2":
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Custom Weapon Name/Lore.");
                }
                break;
            case "3":
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
                break;
            case "4":
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                break;
            case "5":
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
                break;
        }

        sender.sendMessage("Buycraft Event Sent!");
        return true;
    }

    private void fillLists() {

    }

}

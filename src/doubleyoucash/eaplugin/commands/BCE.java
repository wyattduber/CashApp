package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Set;

public class BCE implements CommandExecutor {

    private PermissionUser[] modPlusList = new PermissionUser[20];
    private PermissionUser[] modList = new PermissionUser[30];
    private CashApp ca;
    private ConsoleCommandSender console;

    public BCE() {
        ca = CashApp.getPlugin();
        console = Bukkit.getServer().getConsoleSender();
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

        // Mod+ List Initialize
        PermissionGroup modPlus = PermissionsEx.getPermissionManager().getGroup("Moderator+");
        modPlus.getActiveUsers().toArray(modPlusList);

        // Mod List Initialize
        PermissionGroup mod = PermissionsEx.getPermissionManager().getGroup("Moderator");
        mod.getActiveUsers().toArray(modList);

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
                for (PermissionUser value : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + value.getName() + " " + args[1] + " has purchased a Custom Title.");
                }
                break;
            case "1":
                for (PermissionUser user : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + user.getName() + " " + args[1] + " has purchased a Custom Title Replacement.");
                }
                break;
            case "2":
                for (PermissionUser permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Custom Weapon Name/Lore.");
                }
                break;
            case "3":
                for (PermissionUser permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Public Warp.");
                }
                for (PermissionUser permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Public Warp.");
                }
                break;
            case "4":
                for (PermissionUser permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                for (PermissionUser permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                break;
            case "5":
                for (PermissionUser permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Mob Spawner.");
                }
                for (PermissionUser permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "/mail send " + permissionUser.getName() + " " + args[1] + " has purchased a Mob Spawner.");
                }
                break;
        }

        return false;
    }

    private void fillLists() {

    }

}

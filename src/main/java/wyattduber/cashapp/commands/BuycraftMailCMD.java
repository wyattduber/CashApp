package wyattduber.cashapp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.helpers.ChatMessageHelper;

import java.util.List;

public class BuycraftMailCMD implements CommandExecutor {

    private final CashApp ca;
    private final List<String> modList;
    private final List<String> modPlusList;
    private final ConsoleCommandSender console;

    public BuycraftMailCMD() {
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
        if (!(sender instanceof ConsoleCommandSender)) {
            ChatMessageHelper.sendMessage(sender, "§cCommand must be executed from console!");
            return true;
        }

        // Confirm that argument is an integer
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ChatMessageHelper.sendMessage(sender, "§cFirst argument must be an integer!");
            return true;
        }

        switch (BuycraftMailType.fromInt(Integer.parseInt(args[0]))) {
            case STALL_RE_RENTAL_MESSAGE_ALERT -> {
                for (String value : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " reminded mall renters of re-rental period.");
                }
                for (String value : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " reminded mall renters of re-rental period.");
                }
            }
            case CUSTOM_TITLE -> {
                for (String value : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + value + " " + args[1] + " has purchased a Custom Title.");
                }
            }
            case CUSTOM_TITLE_REPLACEMENT -> {
                for (String user : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + user + " " + args[1] + " has purchased a Custom Title Replacement.");
                }
            }
            case CUSTOM_WEAPON_NAME_LORE -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Custom Weapon Name/Lore.");
                }
            }
            case PUBLIC_WARP -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp.");
                }
            }
            case WARP_MOVE_RENAME -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Public Warp Move/Rename.");
                }
            }
            case MOB_SPAWNER -> {
                for (String permissionUser : modPlusList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
                for (String permissionUser : modList) {
                    Bukkit.dispatchCommand(console, "cmi mail send " + permissionUser + " " + args[1] + " has purchased a Mob Spawner.");
                }
            }
        }

        ChatMessageHelper.sendMessage(sender, "Buycraft Event " + BuycraftMailType.fromInt(Integer.parseInt(args[0])) + " Sent!");
        return true;
    }

}

enum BuycraftMailType {
    STALL_RE_RENTAL_MESSAGE_ALERT (-1),
    CUSTOM_TITLE (0),
    CUSTOM_TITLE_REPLACEMENT (1),
    CUSTOM_WEAPON_NAME_LORE(2),
    PUBLIC_WARP(3),
    WARP_MOVE_RENAME(4),
    MOB_SPAWNER(5);

    private final int value;

    BuycraftMailType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BuycraftMailType fromInt(int value) {
        for (BuycraftMailType enumValue : BuycraftMailType.values()) {
            if (enumValue.getValue() == value) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}

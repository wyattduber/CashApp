package wyattduber.cashapp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.enums.StatType;

import java.util.Arrays;

public class StatsCMD implements CommandExecutor {

    private final CashApp ca;
    private final Database db;

    public StatsCMD() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    /**
     * Usage: /stats (statType) (statSubType) [player]
     * @param sender The sender of the command
     * @param command The command that was executed
     * @param label The alias of the command
     * @param args The arguments provided with the command
     * @return true if the command was executed successfully, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ca.sendMessage(sender, "§cOnly players can use this command!");
            return true;
        }

        if (args.length == 0 || args.length > 3) {
            ca.sendMessage(player, "§cUsage: /stats (statType) (statSubType) [player]");
            return true;
        }

        if (args.length == 1) {
            String statType = args[0];
            if (Arrays.stream(StatType.values()).noneMatch(stat -> stat.name().equalsIgnoreCase(statType))) {
                ca.sendMessage(player, "§cInvalid Stat Type!");
                return true;
            }

            double stat = db.getStat(player.getUniqueId(), StatType.valueOf(statType));
            if (stat == -1) ca.sendMessage(player, "§cInvalid Stat Type or You have no stat for this type yet!");
            else ca.sendMessage(player, "§a" + statType + ": " + stat);
        } else if (args.length == 2) {
            String statType = args[0];
            String statSubType = args[1];
            if (Arrays.stream(StatType.values()).noneMatch(stat -> stat.name().equalsIgnoreCase(statType))) {
                ca.sendMessage(player, "§cInvalid Stat Type!");
                return true;
            }

            double stat = db.getStat(player.getUniqueId(), StatType.valueOf(statType), statSubType);

            if (stat == -1) ca.sendMessage(player, "§cInvalid Stat SubType!");
            else ca.sendMessage(player, "§a" + statType + ": " + stat);
            return true;
        }
        return true;
    }
}

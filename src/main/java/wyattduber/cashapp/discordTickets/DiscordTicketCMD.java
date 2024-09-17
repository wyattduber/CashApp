package wyattduber.cashapp.discordTickets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;
import wyattduber.cashapp.helpers.ChatMessageHelper;
import wyattduber.cashapp.helpers.TabCompleterHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiscordTicketCMD implements TabExecutor {

    private final CashApp ca;
    private final Database db;

    public DiscordTicketCMD() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            ChatMessageHelper.sendMessage(sender, "&cYou can only run this command as a player!", false);
            return true;
        } else {
            if (args.length <= 1) return false;

            boolean isAdminOnly = args[0].equalsIgnoreCase("true");
            String desc = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            String ticketName = new Random().nextInt(1000) + "-" + player.getName();
            //TicketHelper.createTicket(ticketName, desc, );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("true");
                tabs.add("false");
                tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[0], tabs);
                yield tabs;
            }
            case 2 -> {
                tabs.add("Description...");
                yield tabs;
            }
            default -> tabs;
        };
    }

}

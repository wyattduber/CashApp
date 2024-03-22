package wyattduber.cashapp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.javacord.JavacordHelper;

public class BuildOfTheMonthCMD implements CommandExecutor {

    private final CashApp ca;
    private final JavacordHelper js;

    public BuildOfTheMonthCMD() {
        ca = CashApp.getPlugin();
        js = ca.js;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        // Argument Check
        if (args.length < 5) {
            return false;
        }

        // Check if the server is connected to Discord
        if (!ca.discordConnected) {
            ca.sendMessage(sender, "&cThe server is not connected to Discord! Contact an admin.");
            return true;
        }

        if (ca.js.botmChannel == null) {
            ca.sendMessage(sender, "&cThe build of the month channel is not properly connected! Contact an admin.");
            return true;
        }

        // Declare variables and assign initial arguments
        String username = args[0];
        String world = args[1];
        String x = args[2];
        String y = args[3];
        String z = args[4];
        StringBuilder message = new StringBuilder();

        // Check if there is a message attached
        // If so, then parse string and check for bad words
        // If not, set it to the default "None"
        if (args.length > 5) {
            for (int i = 5; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            for (String bword : ca.botmBannedWords) {
                if (message.toString().contains(bword) || username.contains(bword) || world.contains(bword) || x.contains(bword) || y.contains(bword) || z.contains(bword)) {
                    ca.sendMessage(sender, "§cKeep your message kid-friendly!");
                    return true;
                }
            }
        } else {
            message.append("None");
        }

        // Check if player is who they say they are when sending the command
        if (sender instanceof Player player) {
            if (!player.getName().equals(username)) {
                ca.sendMessage(player, "§cUsernames do not match!");
                return true;
            }
        }

        // If all the above conditions check out, then send the message to the javacord handler
        js.sendBOTMMessage(username, world, x, y, z, message.toString());
        ca.sendMessage(sender, "BOTM Message Sent!");

        return true;
    }

}

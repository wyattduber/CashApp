package wyattduber.cashapp.helpers;

import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;

public class ChatMessageHelper {

    private static final CashApp ca = CashApp.getPlugin();

    public static void sendMessage(CommandSender sender, String message, boolean includePluginPrefix) {
        // Check if the sender is a player or the console
        if (sender instanceof Player) {
            // Send the full message to players, including hex colors
            sender.sendMessage((includePluginPrefix ? "§f[§aCash§bApp§f] " : "") + CMIChatColor.translate(message));
        } else {
            ca.log(message.replaceAll("§[0-9a-fk-orxX]|§x(§[0-9a-fA-F]){6}", ""));
        }
    }
}

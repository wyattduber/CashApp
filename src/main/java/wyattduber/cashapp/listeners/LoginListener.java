package wyattduber.cashapp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.connectors.Database;

import java.util.Objects;

public class LoginListener implements Listener {

    private final CashApp ca;
    private final Database db;

    public LoginListener() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add Player to the Users Database
        if (!db.isUserInDatabase(player)) {
            db.insertUser(player);
        }

        if (ca.messageDelayTicks > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    /* Send the custom first message if player is new */
                    if (ca.useFirstTimeMessage && !event.getPlayer().hasPlayedBefore()) {
                        sendFirstMessage(event.getPlayer());
                        return;
                    }

                    /* Sends the Messages to Players who have the Permission node to receive them */
                    sendCustomMessages(event.getPlayer());
                }
            }.runTaskLater(ca, ca.messageDelayTicks);
        } else {
            /* Send the custom first message if player is new */
            if (ca.useFirstTimeMessage && !event.getPlayer().hasPlayedBefore()) {
                sendFirstMessage(event.getPlayer());
                return;
            }

            /* Sends the Messages to Players who have the Permission node to receive them */
            sendCustomMessages(event.getPlayer());
        }
    }

    private void sendFirstMessage(Player player) {
        String message = ca.firstTimeMessage;
        message = parsePlaceholders(message, player);
        Objects.requireNonNull(player.getPlayer()).sendMessage(message);
    }

    private void sendCustomMessages(Player player) {
        for (String messageName : ca.messageNames) {
            if (player.hasPermission("ca.message." + messageName)) {
                String message = ca.messages.get("ca.message." + messageName);
                message = parsePlaceholders(message, player);
                player.sendMessage(message);
            }
        }
    }

    private String parsePlaceholders(String message, Player player) {
        message = message.replaceAll("%PLAYER%", player.getName());
        message = message.replaceAll("%ONLINE%", Integer.toString(ca.getServer().getOnlinePlayers().size()));
        message = message.replaceAll("%MAXPLAYERS%",  Integer.toString(ca.getServer().getMaxPlayers()));

        return message;
    }
}

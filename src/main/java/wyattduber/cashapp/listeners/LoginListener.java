package wyattduber.cashapp.listeners;

import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class LoginListener implements Listener {

    private final CashApp ca;
    private final Database db;
    public String[] messageNames;
    public HashMap<String, String> messages;

    public LoginListener() {
        ca = CashApp.getPlugin();
        db = ca.db;
        messages = ca.messages;
        messageNames = ca.messageNames;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

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


        if (db.getSyncReminderStatus(player.getUniqueId()) && db.isSynced(player.getUniqueId())) {
            UUID uuid = player.getUniqueId();
            String discordUsername = getSyncedDiscordUsername(uuid);
            if (!Objects.equals(discordUsername, player.getName())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        /* If the username is not the same as the discord username, then notify the player to update their username */
                        ca.sendMessage(player, ca.syncReminderMsg.replaceAll("%DISCORDUSERNAME%", discordUsername));
                    }
                }.runTaskLater(ca, 2);
            }
        }
    }

    private void sendFirstMessage(Player player) {
        String message = ca.firstTimeMessage;
        message = message.replaceAll("%PLAYER%", player.getName());
        message = message.replaceAll("%ONLINE%", Integer.toString(ca.getServer().getOnlinePlayers().size()));
        message = message.replaceAll("%MAXPLAYERS%",  Integer.toString(ca.getServer().getMaxPlayers()));
        Objects.requireNonNull(player.getPlayer()).sendMessage(message);
    }

    private void sendCustomMessages(Player player) {
        for (String messageName : messageNames) {
            if (player.hasPermission("lm.message." + messageName)) {
                String message = messages.get("lm.message." + messageName);
                message = message.replaceAll("%PLAYER%", player.getName());
                message = message.replaceAll("%ONLINE%", Integer.toString(ca.getServer().getOnlinePlayers().size()));
                message = message.replaceAll("%MAXPLAYERS%",  Integer.toString(ca.getServer().getMaxPlayers()));

                player.sendMessage(message);
            }
        }
    }

    private String getSyncedDiscordUsername(UUID uuid) {
        long discordId = db.getSyncedDiscordID(uuid);
        return ca.js.getUserName(discordId);
    }

}

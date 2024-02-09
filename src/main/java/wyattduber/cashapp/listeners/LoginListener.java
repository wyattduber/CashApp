package wyattduber.cashapp.listeners;

import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

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

        if (db.getSyncReminderStatus(player.getUniqueId()) && db.isSynced(player.getUniqueId())) {
            UUID uuid = player.getUniqueId();
            String discordUsername = getSynchedDiscordUsername(uuid);
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

    private String getSynchedDiscordUsername(UUID uuid) {
        long discordId = db.getSyncedDiscordID(uuid);
        return ca.js.getUserName(discordId);
    }

}

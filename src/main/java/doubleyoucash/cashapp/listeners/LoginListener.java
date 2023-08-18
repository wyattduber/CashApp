package doubleyoucash.cashapp.listeners;

import doubleyoucash.cashapp.CashApp;
import doubleyoucash.cashapp.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
        UUID id = event.getPlayer().getUniqueId();

        /* Check for voting streak status and send message if enabled for that user */
        if (ca.enableVoteStreak) {
            if (db.userExistsInStreaks(id) && db.getStreakStatus(id)) {
                int streak = db.getStreak(id);
                if (streak == 1)
                    event.getPlayer().sendMessage("[§aCash§bApp§f] Current vote streak: " + streak + " day");
                else event.getPlayer().sendMessage("[§aCash§bApp§f] Current vote streak: " + streak + " days");
            }
        }

    }

}

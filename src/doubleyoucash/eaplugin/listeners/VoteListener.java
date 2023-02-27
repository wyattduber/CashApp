package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.bencodez.votingplugin.events.PlayerVoteEvent;

import java.util.UUID;

public class VoteListener implements Listener {

    private final Database db;
    private final CashApp ca;

    public VoteListener() {
        ca = CashApp.getPlugin();
        db = CashApp.getPlugin().db;
    }

    @EventHandler
    public void onVote(PlayerVoteEvent event) {
        ca.log("VOTE DETECTED: " + event.getPlayer() + " voted on " + event.getVoteSite().getDisplayName());
        Player player;
        try {
            player = ca.getServer().getPlayer(event.getPlayer());
        } catch (NullPointerException e) {
            ca.error("Error getting player named + " + event.getPlayer() + "!");
            e.printStackTrace();
            return;
        }
        assert player != null;
        UUID id = player.getUniqueId();

        // Check if the user exists in the streak database
        if (!db.userExistsInStreaks(id)) {
            ca.log("PLAYER NOT IN DATABASE!");
            db.addUserStreakEntry(id);
        }

        // Check if the user lost their streak
        if ((System.currentTimeMillis() - db.getLastVote(id)) > 86400000) {
            ca.log(player.getName() + " lost their streak of " + db.getStreak(id) + "!");
            db.resetStreak(id);
        }

        //Count the votes
        db.insertVote(id, event.getVoteSite().getDisplayName());
        int votes = db.getTotalVotes(id);
        db.updateLastVote(id);

        switch (votes) {
            case 5:
                sm(player, "You have voted 5 days in a row! Receiving rewards...");
                giveRewards(5);
            case 10:
                sm(player, "You have voted 10 days in a row! Receiving rewards...");
                giveRewards(10);
            case 15:
                sm(player, "You have voted 15 days in a row! Receiving rewards...");
                giveRewards(15);
            case 20:
                sm(player, "You have voted 20 days in a row! Receiving rewards...");
                giveRewards(20);
            case 25:
                sm(player, "You have voted 25 days in a row! Receiving rewards...");
                giveRewards(25);
            case 30:
                sm(player, "You have voted 30 days in a row! Receiving rewards...");
                giveRewards(30);
            default:
                if (votes > 30 ) sm(player, "You are " + (5 % (votes % 5)) + " days away from your next reward tier!");
        }

        if (votes > 30)  {
            sm(player, "You have voted more than 30 days in a row! Receiving rewards...");
            giveRewards(31);
        }

    }

    private void sm(Player player, String message) {
        player.sendMessage("§f[§aCash§bApp§f] " + message);
    }

    private void giveRewards(int votes) {

    }

}

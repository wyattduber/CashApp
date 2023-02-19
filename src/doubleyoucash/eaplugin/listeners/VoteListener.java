package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.database.Database;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.bencodez.votingplugin.events.PlayerVoteEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

public class VoteListener implements Listener {

    private final CashApp ca;
    private final Database db;

    public VoteListener() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @EventHandler
    public void onVote(PlayerVoteEvent event) {
        Player player = event.getVotingPluginUser().getPlayer();
        UUID id = player.getUniqueId();

        // Check if the user exists in the streak database
        if (!db.userExistsInStreaks(id)) {
            db.addUserStreakEntry(id);
        }

        // Check if the user lost their streak
        if ((System.currentTimeMillis() - db.getLastVote(id)) > 86400000) {
            db.resetStreak(id);
        }

        //Count the vote
        db.insertVote(id, event.getVoteNumber(), event.getVoteSite().getDisplayName());
        int votes = db.getVotes(id);

        switch (votes) {
            case 5:
                sm(player, "You have voted 5 days in a row! Receiving rewards...");
            case 10:
                sm(player, "You have voted 10 days in a row! Receiving rewards...");
            case 15:
                sm(player, "You have voted 15 days in a row! Receiving rewards...");
            case 20:
                sm(player, "You have voted 20 days in a row! Receiving rewards...");
            case 25:
                sm(player, "You have voted 25 days in a row! Receiving rewards...");
            case 30:
                sm(player, "You have voted 30 days in a row! Receiving rewards...");
            default:
                if (votes > 30 ) sm(player, "You are " + (5 % (votes % 5)) + " days away from your next reward tier!");
        }

        if (votes > 30)  {
            sm(player, "You have voted more than 30 days in a row! Receiving rewards...");

        }

    }

    private void sm(Player player, String message) {
        player.sendMessage("§f[§aCash§bApp§f] " + message);
    }



}

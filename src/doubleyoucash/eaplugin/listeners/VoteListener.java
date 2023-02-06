package doubleyoucash.eaplugin.listeners;

import doubleyoucash.eaplugin.CashApp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.bencodez.votingplugin.events.PlayerVoteEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class VoteListener implements Listener {

    private final CashApp ca;

    public VoteListener() {
        this.ca = CashApp.getPlugin();
    }

    @EventHandler
    public void onVote(PlayerVoteEvent event) throws FileNotFoundException {
        Player player = event.getVotingPluginUser().getPlayer();

        File file = ca.voteFiles.get(player.getUniqueId());
        int numLines = 0;

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) numLines++;
        Date date = new Date();

        try (FileWriter fw = new FileWriter(file)) {
            if (!(numLines > 30)) {
                fw.append(player.getName()).append(" (").append(String.valueOf(player.getUniqueId())).append(") voted on ").append(String.valueOf(date.getTime()));
            }
        } catch (IOException e) {
            ca.error("Nag the developer about this error! This should not happen!");
            e.printStackTrace();
        }

        switch (numLines) {
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
                if (numLines > 30 ) sm(player, "You are " + (5 % (numLines % 5)) + " days away from your next reward tier!");
        }

        if (numLines > 30) sm(player, "You have voted more than 30 days in a row! Receiving rewards...");

    }

    private void sm(Player player, String message) {
        player.sendMessage("§f[§aCash§bApp§f] " + message);
    }

}

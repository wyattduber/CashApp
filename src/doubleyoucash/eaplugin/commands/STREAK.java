package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.database.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class STREAK implements CommandExecutor {

    private final CashApp ca;
    private final Database db;

    public STREAK() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String text, @NotNull String[] args) {
        if (args.length < 1) return false;

        switch (args[0].toLowerCase()) {
            case "togglelogin" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.togglelogin")) toggleLogin(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.togglelogin.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        toggleLoginOther(player);
                    } catch (Exception e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "totalvotes" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes")) totalVotes(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.totalvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        totalVotesOther(player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "getvotes" -> {
                if (args.length == 2) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getvotes")) getVotes(player, Integer.parseInt(args[1]));
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 3) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.getvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[2]);
                        assert player != null;
                        getVotes(sender, player, Integer.parseInt(args[1]));
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "getstreak" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getstreak")) getStreak(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getstreak.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        getStreakOther(sender, player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "lastvote" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes")) getLastVote(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        getLastVoteOther(sender, player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
        }
        return true;
    }

    private void toggleLogin(Player player) {
        if (db.toggleShowStreak(player.getUniqueId())) player.sendMessage("Streak Reminder at Login Enabled!");
        else player.sendMessage("Streak Reminder at Login Disabled!");
    }

    private void toggleLoginOther(Player player) {
        if (db.toggleShowStreak(player.getUniqueId())) player.sendMessage("Streak Reminder at Login Enabled for " + player.getName() + "!");
        else player.sendMessage("Streak Reminder at Login Disabled for" + player.getName() + "!");
    }

    private void totalVotes(Player player) {
        player.sendMessage("Total Votes: " + db.getTotalVotes(player.getUniqueId()));
    }

    private void totalVotesOther(Player player) {
        player.sendMessage("Total Votes for user " + player.getName() + ": " + db.getTotalVotes(player.getUniqueId()));
    }

    private void getVotes(Player player, int num) {
        ArrayList<String> list = db.getVotes(player.getUniqueId(), num);
        for (String s : list) {
            player.sendMessage(s);
        }
    }

    private void getVotes(CommandSender sender, Player player, int num) {
        ArrayList<String> list = db.getVotes(player.getUniqueId(), num);
        for (String s : list) {
            sender.sendMessage(s);
        }
    }

    public void getStreak(Player player) {
        int streak = db.getStreak(player.getUniqueId());
        if (streak == 1) player.sendMessage("Current Vote Streak: " + db.getStreak(player.getUniqueId()) + " day");
        else player.sendMessage("Current Vote Streak: " + db.getStreak(player.getUniqueId()) + " days");
    }

    public void getStreakOther(CommandSender sender, Player player) {
        int streak = db.getStreak(player.getUniqueId());
        if (streak == 1) sender.sendMessage("Current Vote Streak for " + player.getName() + ": " + db.getStreak(player.getUniqueId()) + " day");
        else sender.sendMessage("Current Vote Streak for " + player.getName() + ": " + db.getStreak(player.getUniqueId()) + " days");
    }

    public void getLastVote(Player player) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
        Date result = new Date(db.getLastVote(player.getUniqueId()));
        player.sendMessage("Time of Last Vote: " + simple.format(result));
    }

    public void getLastVoteOther(CommandSender sender, Player player) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
        Date result = new Date(db.getLastVote(player.getUniqueId()));
        sender.sendMessage("Time of Last Vote for " + player.getName() + ": " + simple.format(result));
    }

    private void wrongArgAmount(CommandSender sender) { sender.sendMessage("§cIncorrect amount of arguments!"); }

}

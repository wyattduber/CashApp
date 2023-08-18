package doubleyoucash.cashapp.commands;

import doubleyoucash.cashapp.CashApp;
import doubleyoucash.cashapp.JavacordStart;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BOTM implements CommandExecutor {

    private final JavacordStart js;

    // This is a string of words that
    String bannedWords = "anal ass asshole ballsack bastard bitch biatch blowjob boner boob boobs bullshit buttplug clitoris cock crackwhore cunt cum cyka dick dildo dipshit doggystyle douche dyke earrape fag faggot fuck fucker jerk jizz knobend knobhead knobjockey marijuana meth minge motherfucker muff penis þorn þornography prick pussy queer rape retard retarded scrotum slut sperm spunk shit shite shitty sussy tit tosser turd twat penis vagina vibrator wank wanker whore omfg wtf uwu onision owo 0w0 pussy pussys pussies";

    public BOTM() {
        CashApp ca = CashApp.getPlugin();
        js = ca.js;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length < 5) {
            return false;
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

            String[] bwords = bannedWords.split(" ");
            for (String bword : bwords) {
                if (message.toString().contains(bword) || username.contains(bword) || world.contains(bword) || x.contains(bword) || y.contains(bword) || z.contains(bword)) {
                    sender.sendMessage("§cKeep your message kid-friendly!");
                    return true;
                }
            }
        } else {
            message.append("None");
        }

        // Check if player is who they say they are when sending the command
        if (sender instanceof Player player) {
            if (!player.getName().equals(username)) {
                player.sendMessage("§cUsernames do not match!");
                return true;
            }
        }

        // If all the above conditions check out, then send the message to the javacord handler
        js.sendBOTMMessage(username, world, x, y, z, message.toString());
        sender.sendMessage("BOTM Message Sent!");

        return true;
    }

}

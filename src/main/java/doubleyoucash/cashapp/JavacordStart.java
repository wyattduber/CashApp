package doubleyoucash.cashapp;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

public class JavacordStart {

    public DiscordApi api;
    public Server discordServer;
    public TextChannel botmChannel;

    private final CashApp ca = CashApp.getPlugin();

    public JavacordStart() {
        parseConfig();
        if (api.getTextChannelById(569228321175371776L).isPresent())
            botmChannel = api.getTextChannelById(569228321175371776L).get();
    }

    public void disableAPI() {
        try {
            if (api != null) {
                api.disconnect();
            }
            api = null;
        } catch (Exception e) {
            ca.error("Error Disconnecting from API! Contact the developer.");
        }
    }

    public void reload() {
        disableAPI();
        parseConfig();
    }

    private void parseConfig() {
        if (ca.botToken == null) {
            return;
        }

        try {
            api = new DiscordApiBuilder().setToken(ca.botToken).setAllIntents().login().join();
            ca.log("Connected to " + api.getYourself().getName() + " Bot!");
        } catch (NullPointerException e) {
            ca.warn("Could not connect to API! Please enter a valid Bot Token in config.yml and reload the plugin.");
            ca.warn("If the bot-token is valid, please file an issue on our GitHub.");
        }

        try {
            if (api.getServerById(ca.serverID).isPresent())
                discordServer = api.getServerById(ca.serverID).get();
            ca.log("Connected to " + discordServer.getName() + " Discord Server!");
        } catch (Exception e) {
            ca.warn("Server not Found! Please enter a valid Server ID in config.yml and reload the plugin.");
        }

    }

    public User checkUserExists(String username) {
        try {
            User user = api.getCachedUsersByNameIgnoreCase(username).iterator().next();
            discordServer.requestMember(user).get().getId();
            return user;
        } catch (NullPointerException | InterruptedException | ExecutionException | NoSuchElementException e) {
            return null;
        } 
    }

    public void sendBOTMMessage(String username, String world, String x, String y, String z, String message) {
        String messageToSend = "Name: " + username + "\n" +
                               "World: " + world + "\n" +
                               "Co-Ords: " + x + " " + y + " " + z + "\n" +
                               "Other: " + message;
        botmChannel.sendMessage(messageToSend);
    }

    public int sendCode(User user) {
        int code = (int) (Math.random() * 1000000);
        user.openPrivateChannel().thenAccept(channel -> channel.sendMessage("Your code is: " + code)).join();
        return code;
    }

    public void syncUsername(User user, String username) {
        discordServer.updateNickname(user, username, "Username Sync").exceptionally(ExceptionLogger.get());
    }

    public void unsyncUsername(User user) {
        discordServer.updateNickname(user, "", "Username Unsync").exceptionally(ExceptionLogger.get());
    }

}

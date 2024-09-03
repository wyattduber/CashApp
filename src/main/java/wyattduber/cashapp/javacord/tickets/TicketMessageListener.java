package wyattduber.cashapp.javacord.tickets;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.javacord.Javacord;

public class TicketMessageListener implements MessageCreateListener {

    private final CashApp ca = CashApp.getPlugin();
    private final Javacord js = ca.js;
    private final Database db = ca.db;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser()) {
            return;
        }
        if (event.getMessageAuthor().isYourself()) {
            return;
        }
        if (!js.openTickets.contains(event.getChannel().getId())) {
            return;
        }

        String message = event.getMessageContent();
        TextChannel channel = event.getChannel();
        long channelId = channel.getId();
        if (!message.startsWith("-")) {
            return;
        }
        if (message.equalsIgnoreCase("-ticket")) {
            String[] args = message.split(" ");

            if (args.length == 2) {
                switch (args[1]) {
                    case "close" -> {
                        event.getChannel().sendMessage("Closing ticket...");
                        db.closeTicket(channelId);
                    }
                    case "help" -> {
                        sendMessage("Ticket Help:\n" +
                                "-ticket close: Closes the ticket.", event.getChannel());
                    }
                    case "adminOnly" -> {
                        boolean isAdminOnly = db.isTicketAdminOnly(channelId);
                        db.setAdminOnly(channelId, !isAdminOnly);
                        sendMessage("Ticket is " + (isAdminOnly ? "not " : "now") + " admin only.", event.getChannel());
                    }
                    default -> sendMessage("Invalid ticket command! Use -ticket help for help.", event.getChannel());

                }
            }
        }
    }

    private void sendMessage(String message, TextChannel channel) {
        channel.sendMessage(message);
    }
}

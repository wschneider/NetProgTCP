package chat;

public class MessageParser {

    public MessageParser() {
        // TODO Auto-generated constructor stub
    }

    public static Message parse(String messageText) {
        // Takes String representation of a chat server message, returns a
        // Message object of the type associated with that message. If the
        // message is invalid for any reason, it returns a generic Message
        // object with the isValid flag set to false, which should be handled
        // with an invalid message error to the sender.
        
        if (messageText.startsWith("ME IS")) {
            String[] messageParts = messageText.split(" ");
            if (messageParts.length == 3) {
                // SUCCESSFUL ME IS MESSAGE
                return new MeIsMessage(messageText, messageParts[2]);
            }
            else {
                // ERROR CONDITION
                return new Message(messageText, false);
            }
        }
        else if (messageText.startsWith("SEND")) {
            // Message lines: Header with from/to users, message size, and
            // message length.
            String[] messageLines = messageText.split("\n", 3);
            // First line parts: "SEND", from-user, to-user.
            String[] firstLineParts = messageText.split(" ");
            if (messageLines.length == 3 && firstLineParts.length == 3) {
                try {
                    int messageLength = Integer.parseInt(messageLines[1]);
                    if (messageLength == messageLines[2].length()) {
                        // SUCCESSFUL SEND MESSAGE
                        return new SendMessage(messageText, firstLineParts[1], firstLineParts[2], messageLines[3]);
                    }
                    // ERROR CONDITION
                    return new Message(messageText, false);
                }
                catch (NumberFormatException exception) {
                    // ERROR CONDITION
                    return new Message(messageText, false);
                }
            }
            // ERROR CONDITION
            return new Message(messageText, false);
        }
        if (messageText.startsWith("BROADCAST")) {
            // Message lines: Header with from-user, message size, and
            // message length.
            String[] messageLines = messageText.split("\n", 3);
            // First line parts: "BROADCAST", from-user.
            String[] firstLineParts = messageText.split(" ");
            if (messageLines.length == 3 && firstLineParts.length == 2) {
                try {
                    int messageLength = Integer.parseInt(messageLines[1]);
                    if (messageLength == messageLines[2].length()) {
                        // SUCCESSFUL BROADCAST MESSAGE
                        return new BroadcastMessage(messageText, firstLineParts[1], messageLines[3]);
                    }
                    // ERROR CONDITION
                    return new Message(messageText, false);
                }
                catch (NumberFormatException exception) {
                    // ERROR CONDITION
                    return new Message(messageText, false);
                }
            }
            // ERROR CONDITION
            return new Message(messageText, false);
        }
        if (messageText.startsWith("WHO HERE")) {
            String[] messageParts = messageText.split(" ");
            if (messageParts.length == 3) {
                // SUCCESSFUL WHO HERE MESSAGE
                return new WhoHereMessage(messageText, messageParts[2]);
            }
            else {
                // ERROR CONDITION
                return new Message(messageText, false);
            }
        }
        if (messageText.startsWith("LOG OUT")) {
            String[] messageParts = messageText.split(" ");
            if (messageParts.length == 3) {
                // SUCCESSFUL LOG OUT MESSAGE
                return new LogOutMessage(messageText, messageParts[2]);
            }
            else {
                // ERROR CONDITION
                return new Message(messageText, false);
            }
        }
        // ERROR CONDITION
        return new Message(messageText, false);
    }
}

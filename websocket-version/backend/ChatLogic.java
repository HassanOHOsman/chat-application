import java.util.ArrayList;
import java.util.List;

public class ChatLogic {

      List<Message> messages = new ArrayList<>();

      public void addMessage(String user, String content) {

         Message newMessage = new Message(user, content, System.currentTimeMillis());
         messages.add(newMessage);

      }

      public List<Message> getAllMessages() {
         return new ArrayList<>(messages);
      }

      public List<Message> newMessages(long timestamp) {
         List<Message> recentMessages = new ArrayList<>();
         for (Message message: messages) {
            if (message.getTimestamp() > timestamp) {
               recentMessages.add(message);
            }
         }
         return recentMessages;
      }


      public Message likeMessage(String messageId) {
         for (Message message : messages) {
            if (message.getId().equals(messageId)) {
               message.like();
               return message;
            }
         }
         return null;
      }

      public Message dislikeMessage(String messageId) {
         for (Message message : messages) {
            if (message.getId().equals(messageId)) {
               message.dislike();
               return message;
            }
         }
         return null;
      }



   }
   

    

   public class ChatLogic {

      List<Message> messages = new ArrayList<>();

      public void addMessage(String user, String content) {

         Message newMessage = new Message(user, content, System.currentTimeMillis());
         messages.add(newMessage);

      }

   }
   

    

package message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServerMessageReader implements Runnable{
    private ServerMessageSender sender;
    private String clientId;
    private String username;

    @Override
    public void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String msg = in.readLine();
                sender.addMessage(new Message("server-generated-message-id-"+ new Random().nextInt(), msg, clientId, username));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

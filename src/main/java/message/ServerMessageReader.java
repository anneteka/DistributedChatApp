package message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
                sender.addMessage(new Message("generated-message-id", msg, clientId, username));
                // todo acknowledgement here
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

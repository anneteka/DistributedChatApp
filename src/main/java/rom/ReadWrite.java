package rom;

import java.util.Scanner;

public class ReadWrite {

    Receive receive = null;
    Transmit transmit = null;

    public ReadWrite() {
        this.receive = new Receive();
        this.transmit = new Transmit();
    }

    public class Receive {

        public void print(String message)
        {
            System.out.println(message);
        }
    }

    public class Transmit {

        Scanner scan = null;

        public Transmit() {
            scan =  new Scanner(System.in);
        }

        public MessageInfo read(Integer messageId)
        {
            Integer id = messageId;
            String msg = scan.nextLine();
            return new MessageInfo(id, msg);
        }
    }
}

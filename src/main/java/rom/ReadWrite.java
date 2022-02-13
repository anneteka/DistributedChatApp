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

        public void print(MessageInfo message)
        {
            System.out.println(message.getMessage());
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
            System.out.println("Read from console : " + id + " "+ msg);
            return new MessageInfo(id, msg);
        }
    }
}

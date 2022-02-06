package node.examples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.ObjectOutputStream;
import java.net.Socket;

@Getter
@Setter
@AllArgsConstructor
public class MetaData implements java.io.Serializable {
    private String[] process;
    private transient Socket socket;
    private transient ObjectOutputStream writer;
    private boolean open;
}
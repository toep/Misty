package examples;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.Table;
import com.misty.engine.graphics.UI.TextField;
import com.misty.engine.graphics.font.Font;
import com.misty.engine.network.*;
import com.misty.engine.network.Server.ClientSocket;

import java.awt.*;

public class ChatSystemTest extends Game implements ServerListener, ClientListener {


    Table chatTable;
    TextField textField;
    Server server;
    Client client;
    String name;

    private final static byte PACKET_ID_CUSTOM_PACKET = 0;

    public static void main(String[] args) {
        new ChatSystemTest("examples.ChatSystemTest", 400, 400).start();
    }

    public ChatSystemTest(String name, int width, int height) {
        super(name, width, height, 2);
    }

    @Override
    public void draw(Renderer g) {
    }

    @Override
    public void update() {
    }

    @Override
    public void setup() {
        setClearColor(new Color(0xffaeaeae));

        textField = new TextField("");
        textField.setPosition(0, getHeight() - textField.getHeight());
        textField.setWidth(getWidth());

        chatTable = new Table(0, 0);
        chatTable.setFixedHeight(getHeight() - textField.getHeight());
        chatTable.setWidth(400);

        chatTable.addOnClickListener(() -> {
            textField.setFocus(true);
        });

        add(chatTable);
        add(textField);
        getRenderer().setFont(Font.c64);
        textField.addReturnListener(() -> {
            print(">" + this.name + ": " + textField.getText());
            addCommand(textField.getText());
            textField.clear();
        });



        setFrameRate(15);
    }

    private void addCommand(String text) {
        if (text.startsWith("connect") && text.length() > 8) {
            String[] content = text.split(" ");
            if(content.length != 3) {
                print("connect requires 2 arguments, an ip:port and a username");
                return;
            }
            int sepIndex = content[1].indexOf(":");
            if (sepIndex != -1) {
                String[] parts = content[1].split(":");
                print("Connecting to " + parts);
                client = new Client(parts[0], Integer.valueOf(parts[1]), content[2]);
                client.addListener(this);
                client.connect();
                this.name = content[2];
            } else {
                print("Wrong format.. make sure to include port (ip:port)");
            }
        } else if (text.startsWith("host")) {
            String prt = text.substring(5);
            print("Hosting server on port " + prt);
            int port = Integer.valueOf(prt);
            server = new Server(port);
            server.addListener(this);
            server.start();
            this.name = "Server";
        } else {
            if (server != null) {
                Packet p = new Packet(PACKET_ID_CUSTOM_PACKET, Packet.sizeNeededForString(text));
                p.putString(text);
                server.sendToAll(p);
            } else if (client != null) {
                Packet p = new Packet(PACKET_ID_CUSTOM_PACKET, Packet.sizeNeededForString(text));
                p.putString(text);
                client.sendData(p);
            }
        }
    }

    private void print(String string) {
        chatTable.add(new Label(string));
    }

    @Override
    public void disconnectedFromServer() {
    }

    @Override
    public void receiveDataFromServer(Packet p) {
        if (p.id == PACKET_ID_CUSTOM_PACKET) {
            //a regular message
            p.toPayload();
            print(">"+p.getString());
        }
    }

    @Override
    public void receiveDataFromClient(ClientSocket s, Packet p) {
        if (p.id == PACKET_ID_CUSTOM_PACKET) {
            //a regular message
            p.toPayload();
            print(">" + s.name+ ": " +p.getString());
            server.sendToAllExcept(s, p);
        }
    }

    @Override
    public void clientHasConnected(ClientSocket cs) {
        print(cs.name + " has connected");
    }

    @Override
    public void clientDisconnected(ClientSocket clientSocket) {
    }

    @Override
    public void onConnection(boolean b, String string) {
        if (!b) {
            print(string);
        }
    }

}

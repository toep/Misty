import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.Table;
import com.misty.engine.graphics.UI.TextField;
import com.misty.engine.network.Client;
import com.misty.engine.network.ClientListener;
import com.misty.engine.network.Packet;
import com.misty.engine.network.Server;
import com.misty.engine.network.Server.ClientSocket;
import com.misty.engine.network.ServerListener;

public class ChatSystemTest extends Game implements ServerListener, ClientListener {

	
	Table chatTable;
	TextField textField;
	Server server;
	Client client;
	
	public static void main(String[] args) {
		new ChatSystemTest("ChatSystemTest", 400, 400).start();
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
		textField.setPosition(0, getHeight()-textField.getHeight());
		textField.setWidth(getWidth());
		
		chatTable = new Table(0, 0);
		chatTable.setFixedHeight((int) (getHeight()-textField.getHeight()));
		chatTable.setWidth(400);
		
		add(chatTable);
		add(textField);
		
		textField.addReturnListener(() -> {
			print(">" + textField.getText());
			addCommand(textField.getText());
			textField.setText("");
		});
	}
	private void addCommand(String text) {
		if(text.startsWith("connect") && text.length() > 8) {
			String ip = text.substring(8);
			int sepIndex = ip.indexOf(":");
			if(sepIndex != -1) {
				String[] parts = ip.split(":");
				print("Connecting to " + ip);
				client = new Client(parts[0], Integer.valueOf(parts[1]));
				client.addListener(this);
				client.connect();
			}else {
				print("Wrong format.. make sure to include port (ip:port)");
			}
		}
		else if(text.startsWith("host")) {
			String prt = text.substring(5);
			print("Hosting server on port " + prt);
			int port = Integer.valueOf(prt);
			server = new Server(port);
			server.addListener(this);
			server.start();
		}
		else {
			if(server != null) {
				Packet p = new Packet(4, text.length()+2);
				p.putString(text);
				server.sendToAll(p);
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
	public void receiveDataToClient(Packet p) {
		if(p.id == 4) {
			//a regular message
			p.toPayload();
			print(p.getString());
		}
	}
	@Override
	public void receiveDataToServer(ClientSocket s, Packet p) {
	}
	@Override
	public void clientHasConnected(ClientSocket cs) {
	}
	@Override
	public void clientDisconnected(ClientSocket clientSocket) {
	}
	@Override
	public void serverUpdate() {
	}
	@Override
	public void onConnection(boolean b, String string) {
		if(!b) {
			print(string);
		}
	}

}

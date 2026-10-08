import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ServerStock extends JFrame implements Runnable {
    private JTextArea textArea;
    private DatagramSocket socket;
    private final int PORT = 9876;
    private String[] stocks = {"ACB", "VNM", "MSN"};
    private Random random = new Random();
    private List<ClientInfo> clientList = new ArrayList<>();

    public ServerStock() {
        setTitle("Stock Price Server");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        try {
            socket = new DatagramSocket(PORT);
        } catch (Exception e) {
            textArea.append("Error initializing server: " + e.getMessage() + "\n");
        }

        new Thread(this).start();
    }

    public static void main(String[] args) {
        new ServerStock().setVisible(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                boolean clientExists = false;
                for (ClientInfo client : clientList) {
                    if (client.getAddress().equals(clientAddress) && client.getPort() == clientPort) {
                        clientExists = true;
                        break;
                    }
                }
                if (!clientExists) {
                    clientList.add(new ClientInfo(clientAddress, clientPort));
                    textArea.append("New client added: " + clientAddress + ":" + clientPort + "\n");
                }

                StringBuilder stockPrices = new StringBuilder();
                for (String stock : stocks) {
                    double price = 100 + random.nextDouble() * 50; 
                    stockPrices.append(stock).append(":").append(String.format("%.2f", price)).append(",");
                }

                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String message = "Time:" + timeStamp + "," + stockPrices.toString();
                byte[] sendBuffer = message.getBytes();

                for (ClientInfo client : clientList) {
                    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, client.getAddress(), client.getPort());
                    socket.send(sendPacket);
                }

                textArea.append("Sent to all clients: " + message + "\n");

                Thread.sleep(1000);
            } catch (Exception e) {
                textArea.append("Error: " + e.getMessage() + "\n");
            }
        }
    }

    class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}

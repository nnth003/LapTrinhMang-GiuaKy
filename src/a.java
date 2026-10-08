//import java.net.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Random;
//import javax.swing.*;
//
//public class ServerStock extends JFrame implements Runnable {
//    private JTextArea textArea;
//    private DatagramSocket socket;
//    private final int PORT = 9876;
//    private String[] stocks = {"ACB", "VNM", "MSN"};
//    private Random random = new Random();
//
//    public ServerStock() {
//        setTitle("Stock Price Server");
//        setSize(400, 300);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        
//        textArea = new JTextArea();
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        add(scrollPane);
//
//        try {
//            socket = new DatagramSocket(PORT);
//        } catch (Exception e) {
//            textArea.append("Error initializing server: " + e.getMessage() + "\n");
//        }
//
//        new Thread(this).start();
//    }
//
//    public static void main(String[] args) {
//        new ServerStock().setVisible(true);
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                byte[] receiveBuffer = new byte[1024];
//                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
//                socket.receive(receivePacket);
//
//                StringBuilder stockPrices = new StringBuilder();
//                for (String stock : stocks) {
//                    double price = 100 + random.nextDouble() * 50; 
//                    stockPrices.append(stock).append(":").append(String.format("%.2f", price)).append(",");
//                }
//
//                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
//                String message = "Time:" + timeStamp + "," + stockPrices.toString();
//
//                byte[] sendBuffer = message.getBytes();
//                InetAddress clientAddress = receivePacket.getAddress();
//                int clientPort = receivePacket.getPort();
//                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
//                socket.send(sendPacket);
//
//                textArea.append("Sent to client: " + message + "\n");
//
//                Thread.sleep(1000); 
//            } catch (Exception e) {
//                textArea.append("Error: " + e.getMessage() + "\n");
//            }
//        }
//    }
//}

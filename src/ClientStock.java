import java.awt.BorderLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

public class ClientStock extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private DatagramSocket socket;
    private final int SERVER_PORT = 9876;
    private final String SERVER_IP = "localhost";
    private String[] columnNames = {"Time", "ACB", "VNM", "MSN"};
    private JScrollPane scrollPane;

    public ClientStock() {
        setTitle("Stock Price Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        try {
            socket = new DatagramSocket();
            new Thread(() -> {
                while (true) {
                    requestStockPrices();
                    try {
                        Thread.sleep(1000); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestStockPrices() {
        try {
            byte[] sendBuffer = "REQUEST".getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
            String[] data = message.split(",");

            String time = data[0].split(":")[1] + ":" + data[0].split(":")[2] + ":" + data[0].split(":")[3];  
            String acb = data[1].split(":")[1]; 
            String vnm = data[2].split(":")[1]; 
            String msn = data[3].split(":")[1]; 

            tableModel.addRow(new Object[]{time, acb, vnm, msn});
            
//            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
//            verticalScrollBar.setValue(verticalScrollBar.getMaximum());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClientStock().setVisible(true);
    }
}

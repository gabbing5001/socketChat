import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatServer extends JFrame implements ActionListener {
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private JTextField serverMsg = null; // ����¥�� �ؽ�Ʈâ
	private Receiver clientMsg = null;
	private ServerSocket listener = null;
	private Socket socket = null;
	private JScrollPane scroll = null;

	public ChatServer() {
		setTitle("ä�ü���");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // â ������ ���α׷� �����ϵ���
																// ����

		serverMsg = new JTextField();
		// �����޼����� �����ʸ� �޾ƺ��ϴ�. �����ϴ� Ŭ���� �ȸ���� �ްڽ�����.
		serverMsg.addActionListener(this);

		Container c = getContentPane();

		// ��ġ ������ ����
		c.setLayout(new BorderLayout());
		c.add(serverMsg, BorderLayout.NORTH);

		clientMsg = new Receiver();
		scroll = new JScrollPane(clientMsg);
		c.add(scroll, BorderLayout.CENTER);

		setSize(400, 200);
		setVisible(true);

		setup();

		Thread th = new Thread(clientMsg);
		th.start();

	}

	public void actionPerformed(ActionEvent e) {
		JTextField t = (JTextField) e.getSource(); // �̺�Ʈ �߻���Ų ��� ������ �ٿ�ĳ��������
													// �˾Ƴ�
		String msg = t.getText(); // �޼��� �о�
		t.setText(""); // �а� ���� ����
		clientMsg.append(msg + "\n"); // Ŭ���̾�Ʈ���� �� �� ����� ������
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());

		try {
			out.write(msg + "\n");
			out.flush(); // ������.
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			handleError(e1.getMessage());
		}
	}

	private void setup() {
		try {
			listener = new ServerSocket(9999);
			socket = listener.accept();
			clientMsg.append("Ŭ���̾�Ʈ�κ��� ����!!\n");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // ������� �ϸ� ��Ʈ��ũ ���� �ޱ� ����
		} catch (IOException e) {
			// TODO Auto-generated catch block
			handleError(e.getMessage());
		}

	}

	private void handleError(String msg) {
		System.out.println(msg);
		System.exit(1); // �ü������ �������� ������ ���� �����ϵ��� ���
	}

	class Receiver extends JTextArea implements Runnable {

		public void run() {
			while (true) { // ������ ���⸦ ��� ��ٸ��� �־�� ��
				try {
					String msg = in.readLine(); // �о�ϴ�.
					this.append(msg + "\n"); // ���ƿ°� ���ҵ�
					scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
				} catch (IOException e) {
					handleError(e.getMessage()); // ��밡 �������
				}
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}

}

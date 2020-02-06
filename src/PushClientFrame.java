import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//ù��° �ؽ�Ʈ�� ip �Է�
//�ι�° ip�� 
public class PushClientFrame extends JFrame {
	private Socket socket = null;
	private Receiver text = new Receiver();
	private JButton startBtn = new JButton("����");
	private JTextField serverMsg = null;
	private JTextField ipField = null;
	private JTextField portField = null;

	BufferedReader in = null;// Ŭ���̾�Ʈ�� �ޱ⸸ ������ out�� ���� �ʴ´�.
	private BufferedWriter out = null;

	public PushClientFrame() {
		super("Ŭ���̾�Ʈ");// ������ Ÿ��Ʋ

		// ������ ���� ��ư(X)�� Ŭ���ϸ� ���α׷� ����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		c.add(startBtn);
		ipField = new JTextField(10);
		portField = new JTextField(10);

		

		c.add(ipField);
		c.add(portField);

		startBtn.addActionListener(new MyActionListener()); // ��ŸƮ��ư ������ �׶� �۵�

		serverMsg = new JTextField(30);
		// �����޼����� �����ʸ� �޾ƺ��ϴ�. �����ϴ� Ŭ���� �ȸ���� �ްڽ�����.
		serverMsg.addActionListener(new MyActionListener2());

		c.add(serverMsg);

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(300, 125));
		
		c.add(scroll, BorderLayout.SOUTH);
		setSize(400, 400);
		setVisible(true);

	}

	class MyActionListener implements ActionListener { // ���� �׼Ǹ�����
		public void actionPerformed(ActionEvent e) {
			try {
				setupConnection(); // ��Ʈ��ũ ���� �Լ� ����
				// �����尴ü�� ����� ������. �����Ͱ� �����ϱ⸦ ��ٷ�
				new Thread(text).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	class MyActionListener2 implements ActionListener { // ���� �׼Ǹ�����
		public void actionPerformed(ActionEvent e) {
			JTextField t = (JTextField) e.getSource(); // �̺�Ʈ �߻���Ų ��� ������
														// �ٿ�ĳ�������� �˾Ƴ�
			String msg = t.getText(); // �޼��� �о�
			t.setText(""); // �а� ���� ����
			text.append(msg + "\n"); // Ŭ���̾�Ʈ���� �� �� ����� ������
			try {
				out.write(msg + "\n");
				out.flush(); // ������.
				new Thread(text).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private class Receiver extends JTextArea implements Runnable {
		@Override
		public void run() {
			String inputMessage = null;
			while (true) {
				try {
					inputMessage = in.readLine();

					this.append(inputMessage); // �ؽ�Ʈarea�� �޼����� ��´�.
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Ŭ���̾�Ʈ���� �� ���� ���ڿ� ����
			}
		}
	}

	private void setupConnection() throws IOException {// ��Ʈ��ũ �����Լ�
		/*
		 * socket = new Socket("localhost", 9999); // Ŭ���̾�Ʈ ���� ����
		 * System.out.println("�����");
		 */
		String ipAdd = ipField.getText().trim();
		String port = portField.getText().trim();
		if (ipAdd.length() == 0 || port.length() == 0)
			return;

		socket = new Socket(ipAdd, Integer.parseInt(port)); // �ٸ���� ��Ʈ�� ������
															// �Է��Ѱ� �޾Ƽ� �����غ���
		setTitle("���� �Ϸ�...");

		in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8")); // Ŭ���̾�Ʈ�κ�����
																					// �Է½�Ʈ��
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")); // �������
	}

	private void handleError(String string) {
		if (socket != null)
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		System.out.println(string);
		System.exit(1);
	}

	public static void main(String[] args) {
		new PushClientFrame();
	}

}

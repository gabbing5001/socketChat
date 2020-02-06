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
	private JTextField serverMsg = null; // 한줄짜리 텍스트창
	private Receiver clientMsg = null;
	private ServerSocket listener = null;
	private Socket socket = null;
	private JScrollPane scroll = null;

	public ChatServer() {
		setTitle("채팅서버");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫을때 프로그램 종료하도록
																// 설정

		serverMsg = new JTextField();
		// 서버메세지에 리스너를 달아봅니다. 간단하니 클래스 안만들고 달겠습ㄴ다.
		serverMsg.addActionListener(this);

		Container c = getContentPane();

		// 배치 관리자 설정
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
		JTextField t = (JTextField) e.getSource(); // 이벤트 발생시킨 사람 누구야 다운캐스팅으로
													// 알아냄
		String msg = t.getText(); // 메세지 읽어
		t.setText(""); // 읽고 나면 지워
		clientMsg.append(msg + "\n"); // 클라이언트에서 한 줄 띄워서 보여줘
		scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());

		try {
			out.write(msg + "\n");
			out.flush(); // 보낸다.
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			handleError(e1.getMessage());
		}
	}

	private void setup() {
		try {
			listener = new ServerSocket(9999);
			socket = listener.accept();
			clientMsg.append("클라이언트로부터 접속!!\n");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 여기까지 하면 네트워크 연결 받기 가능
		} catch (IOException e) {
			// TODO Auto-generated catch block
			handleError(e.getMessage());
		}

	}

	private void handleError(String msg) {
		System.out.println(msg);
		System.exit(1); // 운영체제에게 정상적인 과정을 거쳐 종료하도록 명령
	}

	class Receiver extends JTextArea implements Runnable {

		public void run() {
			while (true) { // 소켓이 오기를 계속 기다리고 있어야 함
				try {
					String msg = in.readLine(); // 읽어봅니다.
					this.append(msg + "\n"); // 날아온걸 어팬드
					scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
				} catch (IOException e) {
					handleError(e.getMessage()); // 상대가 끈어버림
				}
			}
		}
	}

	public static void main(String[] args) {
		new ChatServer();
	}

}

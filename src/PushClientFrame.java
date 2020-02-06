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

//첫번째 텍스트에 ip 입력
//두번째 ip에 
public class PushClientFrame extends JFrame {
	private Socket socket = null;
	private Receiver text = new Receiver();
	private JButton startBtn = new JButton("시작");
	private JTextField serverMsg = null;
	private JTextField ipField = null;
	private JTextField portField = null;

	BufferedReader in = null;// 클라이언트는 받기만 함으로 out은 하지 않는다.
	private BufferedWriter out = null;

	public PushClientFrame() {
		super("클라이언트");// 프레임 타이틀

		// 프레임 종료 버튼(X)을 클릭하면 프로그램 종료
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		c.add(startBtn);
		ipField = new JTextField(10);
		portField = new JTextField(10);

		

		c.add(ipField);
		c.add(portField);

		startBtn.addActionListener(new MyActionListener()); // 스타트버튼 누르면 그때 작동

		serverMsg = new JTextField(30);
		// 서버메세지에 리스너를 달아봅니다. 간단하니 클래스 안만들고 달겠습ㄴ다.
		serverMsg.addActionListener(new MyActionListener2());

		c.add(serverMsg);

		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(300, 125));
		
		c.add(scroll, BorderLayout.SOUTH);
		setSize(400, 400);
		setVisible(true);

	}

	class MyActionListener implements ActionListener { // 여기 액션리스너
		public void actionPerformed(ActionEvent e) {
			try {
				setupConnection(); // 네트워크 연결 함수 시작
				// 스레드객체를 만들고 돌린다. 데이터가 도착하기를 기다려
				new Thread(text).start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	class MyActionListener2 implements ActionListener { // 여기 액션리스너
		public void actionPerformed(ActionEvent e) {
			JTextField t = (JTextField) e.getSource(); // 이벤트 발생시킨 사람 누구야
														// 다운캐스팅으로 알아냄
			String msg = t.getText(); // 메세지 읽어
			t.setText(""); // 읽고 나면 지워
			text.append(msg + "\n"); // 클라이언트에서 한 줄 띄워서 보여줘
			try {
				out.write(msg + "\n");
				out.flush(); // 보낸다.
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

					this.append(inputMessage); // 텍스트area에 메세지를 찍는다.
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 클라이언트에서 한 행의 문자열 읽음
			}
		}
	}

	private void setupConnection() throws IOException {// 네트워크 연결함수
		/*
		 * socket = new Socket("localhost", 9999); // 클라이언트 소켓 생성
		 * System.out.println("연결됨");
		 */
		String ipAdd = ipField.getText().trim();
		String port = portField.getText().trim();
		if (ipAdd.length() == 0 || port.length() == 0)
			return;

		socket = new Socket(ipAdd, Integer.parseInt(port)); // 다른사람 포트로 들어가려고
															// 입력한거 받아서 연결해보자
		setTitle("접속 완료...");

		in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8")); // 클라이언트로부터의
																					// 입력스트림
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")); // 여기까지
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

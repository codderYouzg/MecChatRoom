package com.mec.chat_room.client.view.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mec.chat_room.client.model.UserInfo;
import com.mec.chat_room.view.ChatRoomView;
import com.mec.csframework.action.ActionBeanFactory;
import com.mec.csframework.action.Actioner;
import com.mec.csframework.action.Mapping;
import com.mec.csframework.core.Client;
import com.mec.csframework.core.ClientActionAdapter;
import com.mec.util.ArgumentMaker;
import com.mec.util.FrameIsNullException;
import com.mec.util.IMecView;
import com.mec.util.ViewTool;

@Actioner
public class LoginView implements IMecView {
	private Client client;
	
	private JFrame jfrmLogin;
	private JLabel jlblRegistry;
	private JButton jbtnLogin;
	private JTextField jtxtUserName;
	private JPasswordField jpswPassword;

	public LoginView() {
	}
	
	public LoginView(Client client) {
		this.client = client;
		this.client.setClientAction(new LoginAction());
	}

	private void offline() {
		client.offline();
	}
	
	@Override
	public void dealEvent() {
		jfrmLogin.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				offline();
			}
		});
		
		jpswPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jpswPassword.setText("");
			}
		});
		
		jtxtUserName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtxtUserName.selectAll();
			}
		});
		
		jtxtUserName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jpswPassword.requestFocus();
			}
		});
		
		jpswPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbtnLogin.requestFocus();
			}
		});
		
		jbtnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dealUserLogin();
			}
		});
		
		jbtnLogin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				dealUserLogin();
			}
		});
	}
	
	private void dealUserLogin() {
		String id = jtxtUserName.getText();
		String password = new String(jpswPassword.getPassword());
		password = String.valueOf(password.hashCode());

		ActionBeanFactory.setObject("afterLogin", this);
		client.sendRequest("userLogin", "afterLogin", new ArgumentMaker()
				.add("id", id)
				.add("password", password)
				.toString());
	}

	@Mapping("afterLogin")
	public void afterLogin(UserInfo user) {
		if (user.getId().equals("ERROR")) {
			ViewTool.showWarnning(jfrmLogin, "�˺Ż��������");
			jpswPassword.setText("");
			jtxtUserName.selectAll();
			jtxtUserName.requestFocus();
			return;
		}
		user.setNetId(client.getId());
		client.sendMessageToServer("�û�[" + user + "]���������ң�");
		ChatRoomView chatRoomView = new ChatRoomView(client, user);
		chatRoomView.initView();
		try {
			chatRoomView.showView();
			exitView();
		} catch (FrameIsNullException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		jfrmLogin = new JFrame("�û���¼");
		jfrmLogin.setLayout(new BorderLayout());
		jfrmLogin.setSize(300, 200);
		jfrmLogin.setLocationRelativeTo(null);
		jfrmLogin.setResizable(false);
		jfrmLogin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JLabel jlblTopic = new JLabel("�û���¼", 0);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		jfrmLogin.add(jlblTopic, BorderLayout.NORTH);
		
		JPanel jpnlContxt = new JPanel();
		jpnlContxt.setLayout(null);
		jfrmLogin.add(jpnlContxt, BorderLayout.CENTER);
		
		int normalSize = normalFont.getSize();
		int left = 8*PADDING;
		int top = 5*PADDING;
		int lblWidth = 2 * normalSize;
		int lblHeight = normalSize + 2;
		JLabel jlblUserName = new JLabel("�˺�");
		jlblUserName.setFont(normalFont);
		jlblUserName.setBounds(left, top, lblWidth, lblHeight);
		jpnlContxt.add(jlblUserName);
		
		jtxtUserName = new JTextField();
		jtxtUserName.setFont(normalFont);
		jtxtUserName.setBounds(left + lblWidth + PADDING, top, 11*normalSize, lblHeight+4);
		jpnlContxt.add(jtxtUserName);
		
		top += lblHeight + 3*PADDING;
		JLabel jlblPassword = new JLabel("����");
		jlblPassword.setFont(normalFont);
		jlblPassword.setBounds(left, top, lblWidth, lblHeight);
		jpnlContxt.add(jlblPassword);
		
		jpswPassword = new JPasswordField();
		jpswPassword.setFont(normalFont);
		jpswPassword.setBounds(left + lblWidth + PADDING, top, 11*normalSize, lblHeight + 4);
		jpnlContxt.add(jpswPassword);
		
		JPanel jpnlButton = new JPanel();
		jpnlButton.setLayout(new FlowLayout());
		jfrmLogin.add(jpnlButton, BorderLayout.SOUTH);
		
		jlblRegistry = new JLabel("ע��");
		jlblRegistry.setFont(new Font("΢���ź�", Font.BOLD, 16));
		jlblRegistry.setForeground(new Color(5,98,44));
		jlblRegistry.setCursor(handCursor);
		jpnlButton.add(jlblRegistry);
		
		JLabel jlblBlank = new JLabel("      ");
		jlblBlank.setFont(normalFont);
		jpnlButton.add(jlblBlank);
		
		jbtnLogin = new JButton("��¼");
		jbtnLogin.setFont(smallFont);
		jpnlButton.add(jbtnLogin);
	}

	@Override
	public void reinit() {}

	@Override
	public JFrame getFrame() {
		return jfrmLogin;
	}

	class LoginAction extends ClientActionAdapter {
		
		public LoginAction() {
		}

		@Override
		public boolean confirmOffline() {
			int choice = ViewTool.getChoice(jfrmLogin, "�Ƿ�����?", JOptionPane.YES_NO_OPTION);
			
			return choice == JOptionPane.YES_OPTION;
		}

		@Override
		public void afterOffline() {
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void serverAbnormalDrop() {
			ViewTool.showError(jfrmLogin, "�������쳣崻�������ֹͣ��");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void serverForcedown() {
			ViewTool.showMessage(jfrmLogin, "������ǿ��崻�������ֹͣ��");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

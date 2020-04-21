package com.mec.chat_room.client.view.connect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.mec.chat_room.client.view.login.LoginView;
import com.mec.csframework.core.Client;
import com.mec.csframework.core.ClientActionAdapter;
import com.mec.csframework.core.ClientActionNotSetException;
import com.mec.util.FrameIsNullException;
import com.mec.util.IMecView;
import com.mec.util.ViewTool;

public class ConnectToServerView implements IMecView {
	private JFrame jfrmConnectView;
	private JLabel jlblMessage;	
	private int count;
	
	private Client client;
	
	public ConnectToServerView() {
		this.count = 1;
	}

	public void initClient() throws UnknownHostException {
		this.client = new Client();
		this.client.initClient("/client.cfg.properties");
		this.client.setClientAction(new ConnectAction());
	}
	
	@Override
	public void init() {
		jfrmConnectView = new JFrame("���ӷ�����");
		jfrmConnectView.setLayout(new BorderLayout());
		jfrmConnectView.setMinimumSize(new Dimension(300, 150));
		jfrmConnectView.setLocationRelativeTo(null);
		jfrmConnectView.setResizable(false);
		jfrmConnectView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JLabel jlblTopic = new JLabel("���ӷ�����", 0);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		jfrmConnectView.add(jlblTopic, BorderLayout.NORTH);
		
		jlblMessage = new JLabel("", 0);
		jlblMessage.setFont(normalFont);
		jfrmConnectView.add(jlblMessage);
	}

	public void startConnect() {
		jlblMessage.setText("���ڽ��е�" + count++ + "�����ӡ���");
		try {
			while (!client.connectToServer()) {
				int choice = ViewTool.getChoice(jfrmConnectView, "���ӷ�����ʧ�ܣ��Ƿ������������?", 
						JOptionPane.YES_NO_OPTION);
				if (choice != JOptionPane.YES_OPTION) {
					exitView();
					return;
				}
				jlblMessage.setText("���ڽ��е�" + count++ + "�����ӡ���");
			}
		} catch (ClientActionNotSetException e) {
			e.printStackTrace();
		} catch (FrameIsNullException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void reinit() {}

	@Override
	public void dealEvent() {
		jfrmConnectView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					exitView();
				} catch (FrameIsNullException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	public JFrame getFrame() {
		return jfrmConnectView;
	}

	class ConnectAction extends ClientActionAdapter {
		
		public ConnectAction() {
		}

		@Override
		public void afterConnectToServer() {
			// ���ӳɹ�����ʼ����¼������
			LoginView loginView = new LoginView(client);
			loginView.initView();
			try {
				loginView.showView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void serverAbnormalDrop() {
			ViewTool.showMessage(jfrmConnectView, "�������ܾ����ӣ����Ժ��ԡ���");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void serverOutOfRoom() {
			ViewTool.showMessage(jfrmConnectView, "���������������Ժ������ӡ���");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}

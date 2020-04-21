package com.mec.chat_room.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.Gson;
import com.mec.chat_room.client.model.UserInfo;
import com.mec.chat_room.client.user.ChatMessage;
import com.mec.chat_room.client.user.EAppCommand;
import com.mec.chat_room.client.user.FriendPool;
import com.mec.csframework.core.Client;
import com.mec.csframework.core.ClientActionAdapter;
import com.mec.util.ArgumentMaker;
import com.mec.util.FrameIsNullException;
import com.mec.util.IMecView;
import com.mec.util.ViewTool;

public class ChatRoomView implements IMecView {
	private Client client;
	
	private JFrame jfrmChatRoomView;
	private JTextArea jtatSystemMessage;
	private JTextArea jtatChat;
	private DefaultListModel<UserInfo> dlmFriendList;
	private JList<UserInfo> jlstFriendList;
	private JLabel jlblCurrentFriend;
	private JTextField jtxtSpeakContext;
	private JButton jbtnSend;
	
	private UserInfo me;
	private UserInfo all;
	
	private FriendPool friendPool;
	private Gson gson;
	
	private static final String allId = "00000000";

	public ChatRoomView(Client client,  UserInfo me) {
		this.gson = ArgumentMaker.gson;
		this.friendPool = new FriendPool();
		
		this.all = new UserInfo();
		this.all.setId(allId);
		this.all.setNick("������");
		
		this.client = client;
		this.client.setClientAction(new ChatRoomAction());
		this.me = me;
	}

	@Override
	public void reinit() {
		dlmFriendList.addElement(all);
		jlstFriendList.setSelectedIndex(0);
		jlblCurrentFriend.setText(all.getNick());
		// �����������Һ��ѷ��͡������ˡ�����Ϣ
		Gson gson = ArgumentMaker.gson;
		
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setCommand(EAppCommand.I_AM_COMMING);
		chatMessage.setMessage(gson.toJson(me));
		client.toOther(gson.toJson(chatMessage));
	}

	private void showSystemMessage(String message) {
		jtatSystemMessage.append(message + "\n");
		jtatSystemMessage.setCaretPosition(jtatSystemMessage.getText().length());
	}
	
	private void showChatMessage(String message) {
		jtatChat.append(message + "\n");
		jtatChat.setCaretPosition(jtatChat.getText().length());
	}

	@Override
	public void dealEvent() {
		jfrmChatRoomView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.offline();
			}
		});
		
		jlstFriendList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					UserInfo curFriend = jlstFriendList.getSelectedValue();
					if (curFriend == null) {
						jlstFriendList.setSelectedIndex(0);
						curFriend = jlstFriendList.getSelectedValue();
					}
					jlblCurrentFriend.setText(curFriend.toString());
				}
			}
		});
		
		jbtnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String speakMessage = jtxtSpeakContext.getText();
				jtxtSpeakContext.setText("");
				if (speakMessage.length() <= 0) {
					ViewTool.showError(jfrmChatRoomView, "���ܷ��Ϳ���Ϣ��");
					return;
				}
				
				UserInfo curFriend = jlstFriendList.getSelectedValue();
				String orgNick = jlblCurrentFriend.getText();
				if (!curFriend.getNick().equals(orgNick)) {
					ViewTool.showError(jfrmChatRoomView, "����[" + orgNick + "]�Ѿ����������ң�");
					return;
				}
				
				ChatMessage chatMessage = new ChatMessage();
				String message = new ArgumentMaker()
						.add("friend", me)
						.add("message", speakMessage)
						.toString();
				if (curFriend.getId().equals(allId)) {
					chatMessage.setCommand(EAppCommand.TO_OTHER);
					chatMessage.setMessage(message);
					client.toOther(gson.toJson(chatMessage));
					
					showChatMessage("��Դ�Һ���:" + speakMessage);
				} else {
					chatMessage.setCommand(EAppCommand.TO_ONE);
					chatMessage.setMessage(message);
					client.toOne(curFriend.getNetId(), gson.toJson(chatMessage));
					
					showChatMessage("�����ĵض�[" + curFriend + "]˵��:" + speakMessage);
				}
			}
		});
	}

	@Override
	public JFrame getFrame() {
		return jfrmChatRoomView;
	}

	class ChatRoomAction extends ClientActionAdapter {
		
		public ChatRoomAction() {
		}

		@Override
		public boolean confirmOffline() {
			int choice = ViewTool.getChoice(jfrmChatRoomView, "�Ƿ����ߣ�", JOptionPane.YES_NO_OPTION);
			
			return choice == JOptionPane.YES_OPTION;
		}

		@Override
		public void beforeOffline() {
			client.sendMessageToServer("�û�[" + me + "]�뿪������");
			// ���������ѷ��͡��������ߡ�����Ϣ
			ChatMessage leaveMessage = new ChatMessage();
			leaveMessage.setCommand(EAppCommand.I_AM_GONE);
			leaveMessage.setMessage(gson.toJson(me));
			
			client.toOther(gson.toJson(leaveMessage));
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
			ViewTool.showError(jfrmChatRoomView, "�������쳣崻�������ֹͣ��");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void dealToOne(String sourceId, String message) {
			ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
			EAppCommand command = chatMessage.getCommand();
			String parameter = chatMessage.getMessage();
			
			switch (command) {
			case I_AM_HERE:
				UserInfo friend = gson.fromJson(parameter, UserInfo.class);
				friendPool.addFriend(friend);
				dlmFriendList.addElement(friend);
				break;
			case TO_ONE:
				ArgumentMaker argument = new ArgumentMaker(parameter);
				UserInfo speaker = (UserInfo) argument.getValue("friend", UserInfo.class);
				String speakContext = (String) argument.getValue("message", String.class);
				
				showChatMessage("[" + speaker + "]�������ĵ�˵:" + speakContext);
				break;
			default:
				break;
			}
		}

		@Override
		public void dealToOther(String sourceId, String message) {
			ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
			EAppCommand command = chatMessage.getCommand();
			String parameter = chatMessage.getMessage();
			
			switch (command) {
			case I_AM_COMMING:
				UserInfo newFriend = gson.fromJson(parameter, UserInfo.class);
				friendPool.addFriend(newFriend);
				dlmFriendList.addElement(newFriend);
				showSystemMessage("����[" + newFriend + "]����������");
				
				ChatMessage echoMessage = new ChatMessage();
				echoMessage.setCommand(EAppCommand.I_AM_HERE);
				echoMessage.setMessage(gson.toJson(me));
				client.toOne(sourceId, gson.toJson(echoMessage));
				break;
			case I_AM_GONE:
				UserInfo leaveFriend = gson.fromJson(parameter, UserInfo.class);
				friendPool.removeFriend(leaveFriend);
				showSystemMessage("����[" + leaveFriend + "]�뿪������");
				
				UserInfo curFriend = jlstFriendList.getSelectedValue();
				dlmFriendList.removeElement(leaveFriend);
				if (curFriend.equals(leaveFriend)) {
					jlstFriendList.setSelectedIndex(0);
					jlblCurrentFriend.setText(all.toString());
					ViewTool.showWarnning(jfrmChatRoomView, "����[" + leaveFriend + "]�Ѿ��뿪");
				}
				break;
			case TO_OTHER:
				ArgumentMaker argument = new ArgumentMaker(parameter);
				UserInfo speaker = (UserInfo) argument.getValue("friend", UserInfo.class);
				String speakContext = (String) argument.getValue("message", String.class);
				
				showChatMessage("[" + speaker + "]�Դ�Һ���:" + speakContext);
				break;
			default:
				break;
			}
		}
		
		@Override
		public void serverForcedown() {
			ViewTool.showMessage(jfrmChatRoomView, "������ǿ��崻�������ֹͣ��");
			try {
				exitView();
			} catch (FrameIsNullException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public void init() {
		jfrmChatRoomView = new JFrame("΢����2019���＾������");
		jfrmChatRoomView.setDefaultCloseOperation(
				JFrame.DO_NOTHING_ON_CLOSE);
		jfrmChatRoomView.setSize(800, 600);
		jfrmChatRoomView.setLocationRelativeTo(null);
		
		Container container = jfrmChatRoomView.getContentPane();
		container.setLayout(new BorderLayout());
		
		JLabel jlblTopic = new JLabel("΢����������", JLabel.CENTER);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		container.add(jlblTopic, BorderLayout.NORTH);
		
		jtatSystemMessage = new JTextArea(0, 15);
		jtatSystemMessage.setFont(normalFont);
		JScrollPane jscpSystemMessage = 
				new JScrollPane(jtatSystemMessage);
		jscpSystemMessage.setPreferredSize(new Dimension(200, 400));
		TitledBorder ttdbSystemMessage = new TitledBorder(" ϵͳ��Ϣ ");
		ttdbSystemMessage.setTitleFont(normalFont);
		ttdbSystemMessage.setTitlePosition(TitledBorder.ABOVE_TOP);
		ttdbSystemMessage.setTitleJustification(TitledBorder.CENTER);
		jscpSystemMessage.setBorder(ttdbSystemMessage);
		container.add(jscpSystemMessage, BorderLayout.EAST);
		
		jtatChat = new JTextArea();
		jtatChat.setFont(normalFont);
		JScrollPane jscpChat = new JScrollPane(jtatChat);
		TitledBorder ttdbChat = new TitledBorder(" ������Ϣ ");
		ttdbChat.setTitleFont(normalFont);
		ttdbChat.setTitleColor(Color.RED);
		ttdbChat.setTitlePosition(TitledBorder.ABOVE_TOP);
		ttdbChat.setTitleJustification(TitledBorder.CENTER);
		jscpChat.setBorder(ttdbChat);
		container.add(jscpChat, BorderLayout.CENTER);
		
		JPanel jpnlFriends = new JPanel(new BorderLayout());
		container.add(jpnlFriends, BorderLayout.WEST);
		
		JPanel jpnlWelcome = new JPanel(new GridLayout(0, 1));
		jpnlFriends.add(jpnlWelcome, BorderLayout.NORTH);
		
		JLabel jlblWelcome = new JLabel(" ��ӭ ", JLabel.CENTER);
		jlblWelcome.setFont(normalFont);
		jlblWelcome.setForeground(Color.RED);
		jpnlWelcome.add(jlblWelcome);
		
		JLabel jlblUserSelf = new JLabel(me.getNick(), JLabel.CENTER);
		jlblUserSelf.setFont(normalFont);
		jlblUserSelf.setForeground(topicColor);
		jpnlWelcome.add(jlblUserSelf);
		
		dlmFriendList = new DefaultListModel<>();
		jlstFriendList = new JList<>(dlmFriendList);
		jlstFriendList.setFont(normalFont);
		JScrollPane jscpFriendList = 
				new JScrollPane(jlstFriendList);
		jscpFriendList.setPreferredSize(new Dimension(200, 300));
		TitledBorder ttdbFriendList = 
				new TitledBorder(" �����б� ");
		ttdbFriendList.setTitleFont(normalFont);
		ttdbFriendList.setTitlePosition(TitledBorder.TOP);
		ttdbFriendList.setTitleJustification(
				TitledBorder.CENTER);
		jscpFriendList.setBorder(ttdbFriendList);
		jpnlFriends.add(jscpFriendList, BorderLayout.CENTER);
		
		JPanel jpnlCurFriend = 
				new JPanel(new GridLayout(0, 1));
		jpnlFriends.add(jpnlCurFriend, BorderLayout.SOUTH);
		
		JLabel jlblCurrentFriendCaption = 
				new JLabel("��ǰ����", 0);
		jlblCurrentFriendCaption.setFont(normalFont);
		jpnlCurFriend.add(jlblCurrentFriendCaption);
		
		jlblCurrentFriend = new JLabel("", JLabel.CENTER);
		jlblCurrentFriend.setFont(normalFont);
		jlblCurrentFriend.setForeground(Color.RED);
		jpnlCurFriend.add(jlblCurrentFriend);
		
		JPanel jpnlFooter = new JPanel();
		container.add(jpnlFooter, BorderLayout.SOUTH);
		
		JLabel jlblYousay = new JLabel("��˵ ");
		jlblYousay.setFont(normalFont);
		jpnlFooter.add(jlblYousay);
		
		jtxtSpeakContext = new JTextField(40);
		jtxtSpeakContext.setFont(normalFont);
		jpnlFooter.add(jtxtSpeakContext);
		
		jbtnSend = new JButton("����");
		jbtnSend.setFont(smallFont);
		jpnlFooter.add(jbtnSend);
	}

}

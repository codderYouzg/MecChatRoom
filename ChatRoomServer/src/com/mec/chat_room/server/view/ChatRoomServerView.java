package com.mec.chat_room.server.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.mec.csframework.core.IListener;
import com.mec.csframework.core.Server;
import com.mec.util.FrameIsNullException;
import com.mec.util.IMecView;
import com.mec.util.PropertiesParser;
import com.mec.util.ViewTool;

public class ChatRoomServerView implements IMecView, IListener {
	private Server server;

	private JFrame jfrmServerView;
	private JTextField jtxtCommand;
	private JTextArea jtatMessage;

	public ChatRoomServerView() {
		this.server = new Server();
		this.server.addListener(this);
	}

	public ChatRoomServerView initServer() {
		this.server.initServer("/server.cfg.properties");
		initView();
		return this;
	}

	@Override
	public void init() {
		jfrmServerView = new JFrame("��ת��������");
		jfrmServerView.setLayout(new BorderLayout());
		jfrmServerView.setMinimumSize(new Dimension(800, 600));
		jfrmServerView.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jfrmServerView.setLocationRelativeTo(null);
		jfrmServerView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JLabel jlblTopic = new JLabel("��ת��������", JLabel.CENTER);
		jlblTopic.setFont(topicFont);
		jlblTopic.setForeground(topicColor);
		jfrmServerView.add(jlblTopic, BorderLayout.NORTH);
		
		JPanel jpnlFooter = new JPanel(new FlowLayout());
		jfrmServerView.add(jpnlFooter, BorderLayout.SOUTH);
		
		JLabel jlblCommand = new JLabel("����:");
		jlblCommand.setFont(normalFont);
		jpnlFooter.add(jlblCommand);
		
		jtxtCommand = new JTextField(40);
		jtxtCommand.setFont(normalFont);
		jpnlFooter.add(jtxtCommand);
		
		JPanel jpnlLeftBlank = new JPanel();
		JLabel jlblLeftBlank = new JLabel(" ");
		jlblLeftBlank.setFont(normalFont);
		jpnlLeftBlank.add(jlblLeftBlank);
		jfrmServerView.add(jpnlLeftBlank, BorderLayout.WEST);
		
		JPanel jpnlRightBlank = new JPanel();
		JLabel jlblRightBlank = new JLabel(" ");
		jlblRightBlank.setFont(normalFont);
		jpnlRightBlank.add(jlblRightBlank);
		jfrmServerView.add(jpnlRightBlank, BorderLayout.EAST);
		
		jtatMessage = new JTextArea();
		jtatMessage.setFont(normalFont);
		jtatMessage.setEditable(false);
		jtatMessage.setFocusable(false);
		JScrollPane jscpMessage = new JScrollPane(jtatMessage);
		TitledBorder ttbdMessage = new TitledBorder("ϵͳ��Ϣ");
		ttbdMessage.setTitleFont(normalFont);
		ttbdMessage.setTitleColor(Color.red);
		ttbdMessage.setTitlePosition(TitledBorder.ABOVE_TOP);
		ttbdMessage.setTitleJustification(TitledBorder.CENTER);
		jscpMessage.setBorder(ttbdMessage);
		
		jfrmServerView.add(jscpMessage, BorderLayout.CENTER);
	}

	@Override
	public void reinit() {
	}

	private void closeServer() {
		if (server.isStartup()) {
			ViewTool.showWarnning(jfrmServerView, "��������δ崻���");
			return;
		}
		try {
			exitView();
		} catch (FrameIsNullException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dealEvent() {
		jfrmServerView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeServer();
			}

			@Override
			public void windowOpened(WindowEvent e) {
				PropertiesParser property = new PropertiesParser();
				String strAutoStartup = property.value("autoStartup");
				if (strAutoStartup.equalsIgnoreCase("true")) {
					try {
						server.startup();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		});
		
		jtxtCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = jtxtCommand.getText().trim();
				if (command.length() <= 0) {
					return;
				}
				dealCommand(command);
				jtxtCommand.setText("");
			}
		});
	}
	
	private void dealCommand(String command) {
		if (command.equalsIgnoreCase("startup")
				|| command.equalsIgnoreCase("st")) {
			try {
				server.startup();
			} catch (IOException e) {
				ViewTool.showError(jfrmServerView, e.getMessage());
			}
		} else if (command.equalsIgnoreCase("shutdown")
				|| command.equalsIgnoreCase("sd")) {
			server.shutdown();
		} else if (command.equalsIgnoreCase("exit")
				|| command.equalsIgnoreCase("x")) {
			closeServer();
		} else if (command.equalsIgnoreCase("forcedown")
				|| command.equalsIgnoreCase("fd")) {
			server.forcedown();
		}
	}

	@Override
	public JFrame getFrame() {
		return jfrmServerView;
	}
	
	@Override
	public void processMessage(String message) {
		jtatMessage.append(message);
		jtatMessage.append("\n");
		jtatMessage.setCaretPosition(jtatMessage.getText().length());
	}

}

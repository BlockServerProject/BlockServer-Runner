package org.blockserver.runner.awt;

import org.blockserver.Server;
import org.blockserver.ServerBuilder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends Frame{
	private Frame child = null;
	private Panel leftPanel, rightPanel;
	private Button startStopButton;
	private Server server;
	public MainWindow(){
		super("BlockServer Runner");
		WindowAdapter adapter;
		addWindowListener(adapter = new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
			@Override
			public void windowGainedFocus(WindowEvent e){
				if(child != null){
					child.toFront();
				}
			}
		});
		addWindowFocusListener(adapter);
		setLayout(new GridLayout(1, 2));
		add(leftPanel = new Panel());
		startStopButton = new Button("Start Server");
		startStopButton.addActionListener((ActionEvent e) -> {
			startStopButton.setEnabled(false);
			startServer();
		});
		leftPanel.add(startStopButton);
		add(rightPanel = new Panel());
		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
		toFront();
		validate();
	}
	private void startServer(){
		server = new ServerBuilder()
				// configure
				.build();
		// server.setAPI(...);
		server.start();
	}
}

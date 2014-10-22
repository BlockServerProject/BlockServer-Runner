package org.blockserver.runner.awt.stage;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.blockserver.Server;
import org.blockserver.runner.awt.lib.LifeStage;
import org.blockserver.runner.awt.utils.Console;
import org.blockserver.runner.awt.utils.ServerProperties;

public class MainStage extends LifeStage implements ActionListener{
	public final static int SERVER_STOPPED = 0, SERVER_RUNNING = 1, SERVER_STOPPING = 2;
	private Panel leftPanel, rightPanel, controls, inputPanel;
	private Button startButton;
	private JTextPane consoleOutput;
	private JScrollPane consoleOutputScroller;
	private TextField consoleInput;

	private Server serverInstance = null;
	private int state = SERVER_STOPPED;
	private Console console = null;
	private ServerProperties properties = new ServerProperties();

	public MainStage(){
		super("BlockServer-Runner");
		start(new GridLayout(1, 2));
	}

	@Override
	protected void prepareGui(){
		addComp(leftPanel = new Panel(new GridLayout(3, 1)));
		leftPanel.add(controls = new Panel(new FlowLayout(FlowLayout.LEFT)));
		controls.add(startButton = new Button("Start Server"));
		startButton.addActionListener(this);
		// TODO add edit properties button
		addComp(rightPanel = new Panel(new GridLayout(1, 1)));
		reload();
	}

	@Override
	public void actionPerformed(ActionEvent evt){
		switch(state){
			case SERVER_STOPPED:
				setupGuiOnServerStart();
				serverInstance = properties.buildServer(this);
				serverInstance.setWrapperStopCallback(new Runnable(){
					@Override
					public void run(){
						onServerStopped();
					}
				});
				serverInstance.run();
				setupGuiOnPostServerStart();
				state = SERVER_RUNNING;
				break;
			case SERVER_RUNNING:
				startButton.setLabel("Stopping...");
				startButton.setEnabled(false);
				state = SERVER_STOPPING;
				reload();
				new Thread(new Runnable(){
					@Override
					public void run(){
						try{
							serverInstance.stop();
						}
						catch(Exception e1){
							e1.printStackTrace();
						}
					}
				}).start();
				startButton.setLabel("Start");
				startButton.setEnabled(true);
				reload();
				// TODO clear & reset GUI
				break;
		}
	}
	private void setupGuiOnServerStart(){
		startButton.setLabel("Starting...");
		startButton.setEnabled(false);
		rightPanel.add(consoleOutputScroller = new JScrollPane(consoleOutput = new JTextPane()));
		leftPanel.add(inputPanel = new Panel(new GridLayout(2, 1)));
		inputPanel.add(new Label("Input command:"));
		inputPanel.add(consoleInput = new TextField());
		console = new Console(consoleOutput, consoleInput, consoleOutputScroller);
		reload();
	}
	private void setupGuiOnPostServerStart(){
		startButton.setLabel("Stop");
		startButton.setEnabled(true);
		reload();
	}
	public void onServerStopped(){
		state = SERVER_STOPPED;
		startButton.setLabel("Start");
		startButton.setEnabled(true);
		serverInstance = null;
		reload();
	}

	@Override
	protected void onClose(){
		if(state == SERVER_RUNNING){
			new Thread(new Runnable(){
				@Override
				public void run(){
					try{
						serverInstance.stop();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public Console getConsole(){
		return console;
	}
}

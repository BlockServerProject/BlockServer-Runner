package org.blockserver.runner.awt.stage;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextPane;

import org.blockserver.Server;
import org.blockserver.runner.awt.lib.LifeStage;
import org.blockserver.runner.awt.utils.Console;
import org.blockserver.runner.awt.utils.ServerProperties;

public class MainStage extends LifeStage implements ActionListener{
	public final static int SERVER_STOPPED = 0, SERVER_RUNNING = 1, SERVER_STOPPING = 2;
	private Panel controls, serverViews, textInput;
	private Button startButton;
	private JTextPane consoleOutput;
	private TextField consoleInput;

	private Server serverInstance = null;
	private int state = SERVER_STOPPED;
	private Console console = null;
	private ServerProperties properties = new ServerProperties();

	public MainStage(){
		super("BlockServer-Runner");
		start(new GridLayout(3, 1));
	}

	@Override
	protected void prepareGui(){
		addComp(controls = new Panel(new FlowLayout(FlowLayout.CENTER)));
		controls.add(startButton = new Button("Start Server"));
		startButton.addActionListener(this);
		// TODO add edit properties button
		addComp(serverViews = new Panel(new GridLayout(1, 1)));
		addComp(textInput = new Panel(new GridLayout(1, 2)));
		reload();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		switch(state){
			case SERVER_STOPPED:
				setupGuiOnServerStart();
				serverInstance = properties.buildServer(this);
				serverInstance.run();
				setupGuiOnPostServerStart();
				state = SERVER_RUNNING;
				break;
			case SERVER_RUNNING:
				startButton.setLabel("Stopping...");
				startButton.setEnabled(false);
				try{
					serverInstance.stop();
				}
				catch(Exception e1){
					e1.printStackTrace();
				}
				state = SERVER_STOPPING;
				try{
					Thread.sleep(500);
				}
				catch(InterruptedException e1){
					e1.printStackTrace();
				}
				startButton.setLabel("Start");
				startButton.setEnabled(true);
				// TODO clear & reset GUI
				break;
		}
	}
	private void setupGuiOnServerStart(){
		startButton.setLabel("Starting...");
		startButton.setEnabled(false);
		serverViews.add(consoleOutput = new JTextPane());
		textInput.add(consoleInput = new TextField());
		console = new Console(consoleOutput, consoleInput);
		reload();
	}
	private void setupGuiOnPostServerStart(){
		startButton.setLabel("Stop");
		reload();
	}

	@Override
	protected void onClose(){
		if(state == SERVER_RUNNING){
			try{
				serverInstance.stop();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public Console getConsole(){
		return console;
	}
}

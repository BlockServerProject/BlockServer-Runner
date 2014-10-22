package org.blockserver.runner.awt.utils;

import java.awt.Color;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.blockserver.ConsoleCommandSource;
import org.blockserver.utility.ServerLogger;

public class Console implements ConsoleCommandSource, ServerLogger{
	private List<String> cmdsToDispatch;
	private JTextPane pane;
	private TextField cmdInput;
	private Style time, trace, fatal, warning, error, info, debug, cmdEcho;

	public Console(JTextPane pane, TextField cmdInput){
		this.pane = pane;
		this.cmdInput = cmdInput;
		time = pane.addStyle("time", null);
		StyleConstants.setForeground(time, Color.BLUE);
		trace = pane.addStyle("trace", null);
		StyleConstants.setForeground(trace, Color.DARK_GRAY);
		fatal = pane.addStyle("fatal", null);
		StyleConstants.setForeground(fatal, Color.RED);
		warning = pane.addStyle("warning", null);
		StyleConstants.setForeground(warning, Color.YELLOW);
		error = pane.addStyle("error", null);
		StyleConstants.setForeground(error, Color.RED);
		StyleConstants.setBold(error, true);
		info = pane.addStyle("info", null);
		StyleConstants.setForeground(info, Color.BLACK);
		debug = pane.addStyle("debug", null);
		StyleConstants.setForeground(debug, Color.DARK_GRAY);
		StyleConstants.setItalic(debug, true);
		cmdEcho = pane.addStyle("cmdEcho", null);
		StyleConstants.setItalic(cmdEcho, true);
		StyleConstants.setForeground(cmdEcho, Color.CYAN);
		cmdInput.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e){
//				if(e.isShiftDown()){
//					return;
//				}
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					submitText();
					e.consume();
				}
			}
		});
	}
	public void submitText(){
		String text = cmdInput.getText();
		cmdInput.setText("");
		cmdsToDispatch.add(text);
	}
	protected void log(String message, Style style){
		String timeStr = (new SimpleDateFormat("[HH:mm:ss] ")).format(new Date());
		try{
			pane.getDocument().insertString(pane.getDocument().getLength(), timeStr, time);
			pane.getDocument().insertString(pane.getDocument().getLength(), message.concat(System.lineSeparator()), style);
		}
		catch(BadLocationException e){
			e.printStackTrace();
		}
	}
	@Override
	public void trace(String format, Object... message){
		log(String.format(format, message), trace);
	}
	@Override
	public void fatal(String format, Object... message){
		log(String.format(format, message), fatal);
	}

	@Override
	public void warning(String format, Object... message){
		log(String.format(format, message), warning);
	}

	@Override
	public void error(String format, Object... message){
		log(String.format(format, message), error);
	}

	@Override
	public void info(String format, Object... message){
		log(String.format(format, message), info);
	}

	@Override
	public void debug(String format, Object... message){
		log(String.format(format, message), debug);
	}
	public void cmdEcho(String cmd){
		log("> " + cmd, cmdEcho);
	}

	@Override
	public String readLine() throws IOException{
		try{
			synchronized(cmdsToDispatch){
				return cmdsToDispatch.remove(0);
			}
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	public void onCommand(String cmd) throws ConcurrentModificationException{
		cmdsToDispatch.add(cmd);
	}
}

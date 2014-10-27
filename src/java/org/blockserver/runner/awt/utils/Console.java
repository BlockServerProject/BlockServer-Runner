package org.blockserver.runner.awt.utils;

import java.awt.Color;
import java.awt.TextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.blockserver.ConsoleCommandSource;
import org.blockserver.utility.ServerLogger;

public class Console implements ConsoleCommandSource, ServerLogger{
	private List<String> cmdsToDispatch = new ArrayList<String>();
	private JScrollPane scroll;
	private JTextPane pane;
	private TextField cmdInput;
	private Style time, trace, fatal, warning, error, info, debug, cmdEcho;

	public Console(JTextPane pane, TextField cmdInput, JScrollPane scroller){
		this.pane = pane;
		this.cmdInput = cmdInput;
		cmdInput.setEnabled(true);
		this.scroll = scroller;
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
			private List<String> textHist = new ArrayList<String>();
			private String tmpLast = null;
			private int pointer = 0;
			@Override
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					e.consume();
					textHist.add(Console.this.cmdInput.getText());
					submitText();
					pointer = textHist.size();
				}
				if(e.getKeyCode() == KeyEvent.VK_UP){
					e.consume();
					if(pointer == 0){
						return;
					}
					if(pointer == textHist.size()){
						tmpLast = Console.this.cmdInput.getText();
					}
					Console.this.cmdInput.setText(textHist.get(--pointer));
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN){
					e.consume();
					if(pointer == textHist.size() - 1){
						Console.this.cmdInput.setText(tmpLast);
						return;
					}
					if(pointer == textHist.size()){
						return;
					}
					Console.this.cmdInput.setText(textHist.get(pointer++));
				}
			}
		});
	}
	public void submitText(){
		String text = cmdInput.getText();
		cmdInput.setText("");
		synchronized(cmdsToDispatch){
			cmdsToDispatch.add(text);
		}
	}
	protected synchronized void log(String message, Style style){
		String timeStr = (new SimpleDateFormat("[HH:mm:ss] ")).format(new Date());
		try{
			pane.getDocument().insertString(pane.getDocument().getLength(), timeStr, time);
			boolean first = true;
			for(String line: message.split(System.lineSeparator())){
				if(first){
					pane.getDocument().insertString(pane.getDocument().getLength(),
							line.concat(System.lineSeparator()), style);
					first = false;
				}
				else{
					boolean orig = StyleConstants.isItalic(style);
					StyleConstants.setItalic(style, !orig);
					pane.getDocument().insertString(pane.getDocument().getLength(),
							line.concat(System.lineSeparator()), style);
					StyleConstants.setItalic(style, orig);
				}
			}
			JScrollBar bar = scroll.getVerticalScrollBar();
			bar.setValue(bar.getMaximum());
		}
		catch(BadLocationException e){
			e.printStackTrace();
		}
	}
	@Override
	public void trace(String format, Object... message){
		log("[TRACE] " + String.format(format, message), trace);
	}
	@Override
	public void fatal(String format, Object... message){
		log("[FATAL] " + String.format(format, message), fatal);
	}

	@Override
	public void warning(String format, Object... message){
		log("[WARNING] " + String.format(format, message), warning);
	}

	@Override
	public void error(String format, Object... message){
		log("[ERROR] " + String.format(format, message), error);
	}

	@Override
	public void info(String format, Object... message){
		log("[INFO] " + String.format(format, message), info);
	}

	@Override
	public void debug(String format, Object... message){
		log("[DEBUG] " + String.format(format, message), debug);
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
		synchronized(cmdsToDispatch){
			cmdsToDispatch.add(cmd);
		}
	}
	@Override
	public void close(){
		cmdInput.setEnabled(false);
	}
}

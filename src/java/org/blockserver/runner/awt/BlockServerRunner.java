package org.blockserver.runner.awt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BlockServerRunner{
	public static AwtRunner instance;
	public static void main(String[] args){
		try{
			instance = new AwtRunner();
		}
		catch(Exception e){
			e.printStackTrace();
			int i = 2;
			File dumps = new File("dumps");
			dumps.mkdirs();
			String filename = "Error-Dump-" + (new SimpleDateFormat("dd-MM-yy_HH:mm:ss")).format(new Date());
			File file = new File(dumps, filename);
			while(file.exists()){
				file = new File(dumps, String.format("%s (%d)", filename, i++));
			}
			try{
				e.printStackTrace(new PrintStream(file));
			}
			catch(FileNotFoundException e1){
				e1.printStackTrace();
			}
			System.exit(1);
		}
	}
}

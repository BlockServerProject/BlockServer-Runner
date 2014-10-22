package org.blockserver.runner.awt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.pocketbukkit.BlockServerInterface;
import net.pocketbukkit.ExtensibleEntityTypeManager;

import org.blockserver.BlockServer;
import org.blockserver.Server;
import org.blockserver.level.generator.FlatGenerator;
import org.blockserver.player.BSFPlayerDatabase;
import org.blockserver.runner.awt.stage.MainStage;
import org.blockserver.utility.ConfigAgent;
import org.blockserver.utility.MinecraftVersion;

public class ServerProperties{
	public final static String NORMAL_CONFIG_COMMENTS = "Normal settings file",
			ADVANCED_CONFIG_COMMENTS = "Advanced settings file: ONLY EDIT IF YOU KNOW WHAT YOU ARE DOING!";
	private Properties config, advConfig;
	public ServerProperties(){
		config = new Properties();
		advConfig = new Properties();
		try{
			if(!BlockServer.CONFIG_FILE.isFile()){
				ConfigAgent.saveConfig(ConfigAgent.generateConfig(),
						BlockServer.CONFIG_FILE, NORMAL_CONFIG_COMMENTS);
			}
			if(!BlockServer.ADVANCED_CONFIG_FILE.isFile()){
				ConfigAgent.saveConfig(ConfigAgent.getAdvancedConfig(),
						BlockServer.ADVANCED_CONFIG_FILE, ADVANCED_CONFIG_COMMENTS);
			}
			config.load(new FileInputStream(BlockServer.CONFIG_FILE));
			advConfig.load(new FileInputStream(BlockServer.ADVANCED_CONFIG_FILE));
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	public Properties getConfig(){
		return config;
	}
	public Properties getAdvancedConfig(){
		return advConfig;
	}
	public void saveAll(){
		saveConfig();
		saveAdvancedConfig();
	}
	public void saveConfig(){
		try{
			config.store(new FileOutputStream(BlockServer.CONFIG_FILE), NORMAL_CONFIG_COMMENTS);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public void saveAdvancedConfig(){
		try{
			advConfig.store(new FileOutputStream(BlockServer.ADVANCED_CONFIG_FILE), ADVANCED_CONFIG_COMMENTS);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public Server buildServer(MainStage main){
		try{
			return new Server(config.getProperty("name"), advConfig.getProperty("ip"),
					ConfigAgent.readShort(config, "port"),
					ConfigAgent.readInt(config, "max-players"),
					MinecraftVersion.V095, config.getProperty("motd"),
					config.getProperty("default-level"), FlatGenerator.class,
					new AwtWrapperChatManager(), new BSFPlayerDatabase(),
					new ExtensibleEntityTypeManager(), new BlockServerInterface(),
					ConfigAgent.readFile(new File("."), advConfig, "levels-include-path"),
					ConfigAgent.readFile(new File("."), advConfig, "players-include-path"),
					main.getConsole(), main.getConsole());
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}

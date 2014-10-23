package org.blockserver.runner.awt.stage;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JFileChooser;

import org.blockserver.runner.awt.lib.LifeStage;
import org.blockserver.runner.awt.utils.ServerProperties;
import org.blockserver.utility.Gettable;
import org.blockserver.utility.Validate;

public class ServerPropertiesEditStage extends LifeStage{
	private Panel configPanel, advConfigPanel;
	private TextField levelsDirField = null;
	private ServerProperties properties;
	public ServerPropertiesEditStage(LifeStage parent, ServerProperties properties){
		super("Edit Server Properties", parent);
		this.properties = properties;
		parent.grow(this, new GridLayout(1, 2));
	}
	@Override
	protected void prepareGui(){
		addComp(configPanel = new Panel(new GridLayout(properties.getConfig().size(), 1)));
		addComp(advConfigPanel = new Panel(new GridLayout(
				properties.getAdvancedConfig().size(), 1)));
		populateAdvConfigPanel(advConfigPanel, properties.getAdvancedConfig());
		populateConfigPanel(configPanel, properties.getConfig());
	}
	protected void populateConfigPanel(Panel pn, Properties pr){
		// server port
		Label portLabel = new Label("Server Port");
		TextField portField = new TextField(pr.getProperty("port"));
		Button savePort = getSaveButton("port", portField, false, new Validate<String>(){
			@Override
			public boolean validate(String s){
				Short.parseShort(s);
				return true;
			}
		});
		addRowToPanel(pn, portLabel, portField, savePort);
		// server name
		Label nameLabel = new Label("Server Name");
		TextField nameField = new TextField(pr.getProperty("name"));
		Button saveName = getSaveButton("name", nameField, false, null);
		addRowToPanel(pn, nameLabel, nameField, saveName);
		// default level
		Label defaultLevelLabel = new Label("Default Level Name");
		TextField defaultLevelField = new TextField(pr.getProperty("default-level"));
		Button pickDefaultLevelButton = getBrowseButton("Browse", defaultLevelField,
				new FieldFileGettable(levelsDirField, new Gettable<File>(){
					@Override
					public File get(){
						return new File(".", properties.getAdvancedConfig().getProperty("levels-include-path"));
					}
				}, new Validate<File>(){
					@Override
					public boolean validate(File f){
						return !f.isFile();
					}
				}), JFileChooser.DIRECTORIES_ONLY);
		Button saveDefaultLevel = getSaveButton("default-level", defaultLevelField, false,
				new Validate<String>(){
			@Override
			public boolean validate(String str){
				return !(new File(str)).isFile();
			}
		});
		addRowToPanel(pn, defaultLevelLabel, defaultLevelField, pickDefaultLevelButton, saveDefaultLevel);
		// max players
		Label maxPlayersLabel = new Label("Max Players");
		TextField maxPlayersField = new TextField(pr.getProperty("max-players"));
		Button saveMaxPlayers = getSaveButton("max-players", maxPlayersField, false,
				new Validate<String>(){
			@Override
			public boolean validate(String str){
				return Integer.parseInt(str) > 0;
			}
		});
		addRowToPanel(pn, maxPlayersLabel, maxPlayersField, saveMaxPlayers);
		// motd
		Label motdLabel = new Label("Message Of The Day (Sent when the player joins)");
		TextField motdField = new TextField(pr.getProperty("motd"));
		Button saveMotd = getSaveButton("motd", motdField, false, null);
		addRowToPanel(pn, motdLabel, motdField, saveMotd);
	}
	protected class FieldFileGettable implements Gettable<File>{
		private TextField field;
		private Gettable<File> backup;
		private Validate<File> v;
		public FieldFileGettable(TextField field, Gettable<File> backup, Validate<File> v){
			this.field = field;
			this.backup = backup;
			this.v = v;
		}
		public File get(){
			File file = new File(field.getText());
			try{
				if(!v.validate(file)){
					file = backup.get();
				}
			}
			catch(Exception e){
				file = backup.get();
			}
			return file;
		}
	}
	protected void populateAdvConfigPanel(Panel p, Properties pr){
		Label levelsLabel = new Label("Folder to save levels");
		levelsDirField = new TextField(pr.getProperty("levels-include-path"));
		Button pickLevelsButton = getBrowseButton("Browse", levelsDirField,
				new Gettable<File>(){
			@Override
			public File get(){
				return new File(".");
			}
		}, JFileChooser.DIRECTORIES_ONLY);
		Button saveLevels = getSaveButton("levels-include-path", levelsDirField, true,
				new Validate<String>(){
			@Override
			public boolean validate(String s){
				return !(new File(s)).isFile();
			}
		});
		addRowToPanel(p, levelsLabel, levelsDirField, pickLevelsButton, saveLevels);
		// TODO more
	}
	protected void addRowToPanel(Panel pn, Component... comps){
		Panel row = new Panel(new GridLayout(1, comps.length));
		for(Component comp: comps){
			row.add(comp);
		}
		pn.add(row);
	}
	protected Button getSaveButton(String key, TextField src, boolean isAdv, Validate<String> v){
		Button b = new Button("Save");
		b.addActionListener(new SaveButtonActionListener(key, src, isAdv, v));
		return b;
	}
	protected class SaveButtonActionListener implements ActionListener{
		private String key;
		private TextField src;
		private boolean isAdv;
		private Validate<String> validate;
		public SaveButtonActionListener(String key, TextField src, boolean isAdv, Validate<String> validate){
			this.key = key;
			this.src = src;
			this.isAdv = isAdv;
			this.validate = validate;
		}
		@Override
		public void actionPerformed(ActionEvent event){
			String value = src.getText();
			if(validate != null){
				try{
					if(!validate.validate(value)){
						Color orig = src.getBackground();
						src.setBackground(Color.RED);
						Thread.sleep(100);
						src.setBackground(orig);
						return;
					}
				}
				catch(Exception e){
					return;
				}
			}
			if(isAdv){
				properties.getAdvancedConfig().setProperty(key, value);
				properties.saveAdvancedConfig();
			}
			else{
				properties.getConfig().setProperty(key, value);
				properties.saveConfig();
			}
		}
	}
	protected Button getBrowseButton(String label, TextField field, File startAt, int flags){
		return getBrowseButton(label, field, new ValueGettable<File>(startAt), flags);
	}
	protected Button getBrowseButton(String label, TextField field, Gettable<File> startAt, int flags){
		Button button = new Button(label);
		button.addActionListener(new BrowseFileActionListener(field, startAt, flags));
		return button;
	}
	public static class ValueGettable<V> implements Gettable<V>{
		private V v;
		public ValueGettable(V v){
			this.v = v;
		}
		@Override
		public V get(){
			return v;
		}
	}
	protected class BrowseFileActionListener implements ActionListener{
		private TextField field;
		private Gettable<File> startAt;
		private int flags;
		private JFileChooser fc = new JFileChooser();
		public BrowseFileActionListener(TextField field, Gettable<File> startAt, int flags){
			this.field = field;
			this.startAt = startAt;
			this.flags = flags;
		}
		@Override
		public void actionPerformed(ActionEvent event){
			File dir = startAt.get();
			dir.mkdirs();
			fc.setCurrentDirectory(dir);
			fc.setFileSelectionMode(flags);
			if(fc.showDialog(getFrame(), "Select") == JFileChooser.APPROVE_OPTION){
				File result = fc.getSelectedFile();
				String rel = dir.toURI().relativize(result.toURI()).getPath();
				field.setText(rel);
			}
			else{
				field.requestFocusInWindow();
			}
		}
	}
}

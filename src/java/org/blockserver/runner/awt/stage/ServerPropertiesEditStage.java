package org.blockserver.runner.awt.stage;

import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import org.blockserver.runner.awt.lib.LifeStage;
import org.blockserver.runner.awt.utils.ServerProperties;

public class ServerPropertiesEditStage extends LifeStage{
	private Panel configPanel, advConfigPanel;
	private ServerProperties properties;
	public ServerPropertiesEditStage(LifeStage parent, ServerProperties properties){
		super("Edit Server Properties", parent);
		this.properties = properties;
		parent.grow(this, new GridLayout(1, 2));
	}
	@Override
	protected void prepareGui(){
		addComp(configPanel = new Panel(new GridLayout(properties.getConfig().size(), 2)));
		addComp(advConfigPanel = new Panel(new GridLayout(
				properties.getAdvancedConfig().size(), 3)));
		for(Map.Entry<Object, Object> entry: properties.getConfig().entrySet()){
			final String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			configPanel.add(new Label(key));
			final TextField valueField = new TextField(value);
			configPanel.add(valueField);
			Button button = new Button("Save");
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					properties.getConfig().setProperty(key, valueField.getText());
					properties.saveConfig();
				}
			});
			configPanel.add(button);
		}
		for(Map.Entry<Object, Object> entry: properties.getAdvancedConfig().entrySet()){
			final String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			advConfigPanel.add(new Label(key));
			final TextField valueField = new TextField(value);
			advConfigPanel.add(valueField);
			Button button = new Button("Save");
			button.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0){
					properties.getAdvancedConfig().setProperty(key, valueField.getText());
					properties.saveConfig();
				}
			});
			advConfigPanel.add(button);
		}
	}
}

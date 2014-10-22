package org.blockserver.runner.awt.lib;

import java.awt.Component;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class LifeStage extends WindowAdapter{
	private String name;
	private Frame frame = null;
	private LayoutManager frameLayout = null;
	private LifeStage child = null;
	private LifeStage parent;
	private boolean closingWindow = false;
	public LifeStage(String name){
		this(name, null);
	}
	public LifeStage(String name, LifeStage parent){
		this.name = name;
		this.parent = parent;
	}
	public void start(LayoutManager layout){
		start(layout, Frame.MAXIMIZED_BOTH);
	}
	public void start(LayoutManager layout, int extendState){
		frame = new Frame(name);
		frameLayout = layout;
		frame.setLayout(layout);
		frame.addWindowListener(this);
		prepareGui();
		frame.setVisible(true);
		frame.setExtendedState(extendState);
		frame.toFront();
	}
	public void grow(LifeStage child, LayoutManager layout){
		grow(child, layout, Frame.MAXIMIZED_BOTH);
	}
	public void grow(LifeStage child, LayoutManager layout, int extendState){
		if(this.child == null){
			this.child = child;
			child.start(layout, extendState);
			child.awaken();
		}
		else{
			throw new IllegalStateException("Already has a child "
					+ "("+ this.child.getClass().getSimpleName() + ")");
		}
	}
	public void rmChild(LifeStage child){
		if(child != this.child){
			throw new IllegalArgumentException("Not the correct child!");
		}
		this.child = null;
		awaken();
	}
	public void awaken(){
		if(child != null){
			child.awaken();
		}
		else{
			frame.toFront();
		}
	}
	public void finish(){
		onClose();
		frame.dispose();
		onPostClose();
		if(parent != null){
			parent.rmChild(this);
		}
		else{
			System.exit(0);
		}
	}
	public Frame getFrame(){
		return frame;
	}
	public LayoutManager getFrameLayout(){
		return frameLayout;
	}

	@Override
	public void windowClosing(WindowEvent e){
		if(closingWindow){
			return;
		}
		closingWindow = true;
		finish();
	}

	protected abstract void prepareGui();
	protected void onClose(){}
	protected void onPostClose(){}
	protected void reload(){
		frame.validate();
	}
	protected void addComp(Component comp){
		frame.add(comp);
	}
}

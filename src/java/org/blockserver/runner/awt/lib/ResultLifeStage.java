package org.blockserver.runner.awt.lib;

public abstract class ResultLifeStage extends LifeStage{
	private int resultCode;
	public ResultLifeStage(String name, LifeStage parent, int resultCode){
		super(name, parent);
		this.resultCode = resultCode;
	}
	public void finishWithResult(Object result){
		finish();
		parent.rmChild(this);
		parent.onResult(resultCode, result);
	}
}

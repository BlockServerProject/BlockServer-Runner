package org.blockserver.runner.awt;

import org.blockserver.runner.awt.lib.LifeStage;
import org.blockserver.runner.awt.stage.MainStage;

public class AwtRunner{
	private LifeStage baseStage;
	public AwtRunner(){
		baseStage = new MainStage();
	}
	public LifeStage getBaseStage(){
		return baseStage;
	}
}

package com.game.draco.app.goblin;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.game.draco.GameContext;

public class GoblinStartTimingTask implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 如果哥布林活动已开启，刷新哥布林
		if (GameContext.getGoblinApp().isOpen()) {
			GameContext.getGoblinApp().refreshGoblin();
		}
	}

}

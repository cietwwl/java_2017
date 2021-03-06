package sacred.alliance.magic.domain;

import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.util.DateUtil;

public @Data class RoleArena {
	public final static String ROLE_ID = "roleId" ;
	public final static int DEFAULT_RATE = 50 ;
	private String roleId ;
	private int win1v1 ;
	private int fail1v1 ;
	private int join1v1 ;
	private int cycleWin1v1 ;
	private int cycleFail1v1 ;
	private int cycleJoin1v1 ;
	private int cycle1v1Score ;
	//大师赛累计积分
	private int topScore ;
	//大师赛计分时间
	private Date topDate = new Date();
	
	private Date cycleDate = new Date();
	private boolean old = false ;
	
	public void check(){
		Date now = new Date();
		if(null == cycleDate){
			this.cycleDate = now ;
			return ;
		}
		//判断是否同一天
		if(DateUtil.sameDay(now, this.cycleDate)){
			return ;
		}
		this.cycleFail1v1 = 0 ;
		this.cycleWin1v1 = 0 ;
		this.cycleJoin1v1 = 0 ;
		this.cycle1v1Score = 0 ;
		this.cycleDate = now ;
	}
	
	public int getTopScore(){
		GameContext.getArenaTopApp().resetArenaTopScore(this);
		return this.topScore ;
	}
	
	public int getScore(ArenaType type){
		if(null == type){
			return 0;
		}
		this.check();
		switch(type){
		case _1V1:
			return this.cycle1v1Score ;
		}
		return 0 ;
	}
	

	public void incrScore(ArenaType type,int score){
		if(null == type){
			return ;
		}
		this.check();
		switch(type){
		case _1V1:
			this.cycle1v1Score += score ;
			//判断大师赛积分是否过期
			//1.未过期，分数直接相加
			//2.过期,先将分数清除
			//TODO:大师赛支持
			//GameContext.getArenaTopApp().resetArenaTopScore(this);
			this.topScore += score ;
			break ;
		}
	}
	
	public void incrJoin(ArenaType type){
		if(null == type){
			return ;
		}
		this.check();
		switch(type){
		case _1V1:
			this.join1v1 ++ ;
			this.cycleJoin1v1 ++ ;
			break ;
		}
	}
	
	public void incrWin(ArenaType type){
		if(null == type){
			return ;
		}
		this.check();
		switch(type){
		case _1V1:
			this.win1v1++;
			this.cycleWin1v1 ++ ;
			break ;
		}
	}
	
	public void incrFail(ArenaType type){
		if(null == type){
			return ;
		}
		this.check();
		switch(type){
		case _1V1:
			this.fail1v1++;
			this.cycleFail1v1++;
			break ;
		}
		
	}
	
	public int getCycleWinTime(ArenaType type){
		this.check();
		switch(type){
		case _1V1:
			return this.cycleWin1v1 ;
		}
		return 0 ;
	}
	
	public int getCycleFailTime(ArenaType type){
		this.check();
		switch(type){
		case _1V1:
			return this.cycleFail1v1 ;
		}
		return 0 ;
	}
	

	
	public boolean haveRecord(){
		if(this.old){
			return true ;
		}
		if(0 == this.fail1v1 &&
				0 == this.cycleFail1v1 &&
				0 == this.cycleWin1v1 &&
				0 == this.win1v1 &&
				0 == this.join1v1 &&
				0 == this.cycleJoin1v1 &&
				0 == this.topScore){
			return false ;
		}
		return true ;
	}
}

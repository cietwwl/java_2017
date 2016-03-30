package sacred.alliance.magic.app.rank;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.message.item.RankLayoutItem;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class RankLayout {

	private byte type ;
	private String title1 ;
	private byte len1 ;
	private String title2 ;
	private byte len2 ;
	private String title3 ;
	private byte len3 ;
	private String title4 ;
	private byte len4 ;
	private String title5 ;
	private byte len5 ;
	
	private List<RankLayoutItem> layoutItemList = new ArrayList<RankLayoutItem>();
	
	public void init(){
		this.init(title1, len1);
		this.init(title2, len2);
		this.init(title3, len3);
		this.init(title4, len4);
		this.init(title5, len5);
	}
	
	private void init(String title,byte len){
		if(Util.isEmpty(title) || len <=0){
			Log4jManager.CHECK.error("rank layout config error.type=" + type);
			Log4jManager.checkFail() ;
			return ;
		}
		RankLayoutItem item = new RankLayoutItem();
		item.setTitle(title);
		item.setLen(len);
		this.layoutItemList.add(item);
	}
}

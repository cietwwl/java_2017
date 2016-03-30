package sacred.alliance.magic.app.active.compass;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActivePanelDetailBaseItem;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveCompassAppImpl implements ActiveCompassApp{
	
	@Override
	public void checkReset(RoleInstance role, Active active) {
		//上古法阵是不限次数的抽奖，不需要重置
	}

	@Override
	public C2301_ActivePanelDetailRespMessage getActiveDetail(RoleInstance role, Active active) {
		ActivePanelDetailBaseItem detailItem = new ActivePanelDetailBaseItem();
		detailItem.setType(ActiveType.Compass.getType());
		//公用赋值
		GameContext.getActiveApp().buildActivePanelDetailBaseItem(detailItem, active);
		C2301_ActivePanelDetailRespMessage resp = new C2301_ActivePanelDetailRespMessage();
		resp.setDetailItem(detailItem);
		return resp;
	}

	@Override
	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			return ActiveStatus.NotOpen;
		}
		return ActiveStatus.CanAccept;
	}

	@Override
	public boolean isOutDate(Active active) {
		return active.isOutDate();
	}
	
}

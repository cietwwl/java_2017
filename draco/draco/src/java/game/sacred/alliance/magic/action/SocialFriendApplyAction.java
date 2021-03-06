package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1201_SocialFriendApplyReqMessage;
import com.game.draco.message.response.C1201_SocialFriendRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialFriendApplyAction extends BaseAction<C1201_SocialFriendApplyReqMessage> {

	@Override
	public Message execute(ActionContext context, C1201_SocialFriendApplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		int targetRoleId = reqMsg.getRoleId();
		String targetRoleName = reqMsg.getRoleName();
		RoleInstance targetRole = null;
		C1201_SocialFriendRespMessage resp = new C1201_SocialFriendRespMessage();
		resp.setStatus((byte) 0);
		if(-1 == targetRoleId){
			if(targetRoleName.equals(role.getRoleName())){
				resp.setInfo(Status.Social_Friend_Add_Self.getTips());
				return resp;
			}
			targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleName(targetRoleName);
		}else{
			if(role.getIntRoleId() == targetRoleId){
				resp.setInfo(Status.Social_Friend_Add_Self.getTips());
				return resp;
			}
			targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		}
		if(null == targetRole){
			resp.setInfo(Status.Social_TargRole_Offline.getTips());
			return resp;
		}
		Result result = GameContext.getSocialApp().friendApply(role, targetRole);
		resp.setStatus(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
	
}

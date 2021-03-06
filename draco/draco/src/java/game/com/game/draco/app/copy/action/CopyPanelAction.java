package com.game.draco.app.copy.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0256_CopyPanelReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyPanelAction extends BaseAction<C0256_CopyPanelReqMessage>{

	@Override
	public Message execute(ActionContext context, C0256_CopyPanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		short copyId = req.getCopyId();
		if(copyId <= 0){
			copyId = -1;
		}
		return GameContext.getCopyLogicApp().getCopyPanelRespMessage(role, copyId);
	}
	
}

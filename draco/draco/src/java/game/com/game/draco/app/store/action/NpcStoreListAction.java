package com.game.draco.app.store.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.store.domain.NpcStore;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.NpcStoreItem;
import com.game.draco.message.request.C1601_NpcStoreListReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1601_NpcStoreListRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class NpcStoreListAction extends BaseAction<C1601_NpcStoreListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1601_NpcStoreListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (role == null) {
			return null;
		}
		String param = req.getParam();
		if (Util.isEmpty(param)) {
			return new C0002_ErrorRespMessage(req.getCommandId(),this.getText(TextId.ERROR_INPUT));
		}
		String[] params = param.split(Cat.comma);
		String npcTemplateId = params[0];
		int showType = Integer.valueOf(params[1]);

		List<NpcStore> storeList = GameContext.getNpcStoreApp()
				.getNpcStoreList(npcTemplateId, showType);
		if (null == storeList || storeList.size() == 0) {
			return new C0002_ErrorRespMessage(req.getCommandId(), Status.NpcStore_No_Have_Goods_Sell.getTips());
		}
		C1601_NpcStoreListRespMessage resp = new C1601_NpcStoreListRespMessage();
		resp.setNpcTemplateId(npcTemplateId);
		resp.setType((byte) showType);

		List<NpcStoreItem> itemList = new ArrayList<NpcStoreItem>();
		for (NpcStore store : storeList) {
			try {
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(store.getGoodsId());
				if(null == goodsBase){
					continue ;
				}
				NpcStoreItem item = new NpcStoreItem();
				GoodsLiteNamedItem goodsItem = goodsBase.getGoodsLiteNamedItem() ;
				
				if(!store.isTemplateBindType()){
					goodsItem.setBindType(store.getBindType());
				}else{
					goodsItem.setBindType(goodsBase.getBindingType().getType());
				}
				int stackNum = Math.min(goodsBase.getOverlapCount(),
						store.getDefaultBuyNum());
				goodsItem.setNum((short)stackNum);
				
				item.setGoodsInfo(goodsItem);
				item.setConsumeList(store.getConsumeList());
				itemList.add(item);
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		resp.setStoreList(itemList);
		return resp;
	}
}

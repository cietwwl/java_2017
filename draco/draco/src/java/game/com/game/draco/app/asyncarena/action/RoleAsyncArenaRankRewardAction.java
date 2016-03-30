package com.game.draco.app.asyncarena.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncRankReward;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.asyncarena.vo.RoleAsyncRankRewardResult;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C2628_AsyncArenaRankRewardReqMessage;
import com.game.draco.message.response.C2628_AsyncArenaRankRewardRespMessage;

public class RoleAsyncArenaRankRewardAction extends BaseAction<C2628_AsyncArenaRankRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2628_AsyncArenaRankRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}

		RoleAsyncRankRewardResult item = GameContext.getRoleAsyncArenaApp().rewardRank(role);
		C2628_AsyncArenaRankRewardRespMessage respMsg = new C2628_AsyncArenaRankRewardRespMessage();
		
		//获得角色排名
		int rank = GameContext.getAsyncPvpApp().getRoleAsyncArenaRanking(role.getRoleId());
		AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(role);
		
		if(asyncArenaRole.getIsReward() == 0){
			if(asyncArenaRole.getHistoryRanking() != 0){
				rank = asyncArenaRole.getHistoryRanking();
			}
		}
		respMsg.setHisroryRanking(rank);
		
		AsyncRankReward rankReward = GameContext.getRoleAsyncArenaApp().getAsyncRankReward(rank);
		
		if(rankReward != null){
			respMsg.setExp(rankReward.getExp());
			respMsg.setZp(rankReward.getZp());
			respMsg.setGoldMoney(rankReward.getGoldMoney());
			respMsg.setPrestige(rankReward.getHonor());
			String [] goodsId = rankReward.getGoodsId().split(",");
			String [] goodsNum = rankReward.getGoodsNum().split(",");
			String [] goodsBind = rankReward.getBindType().split(",");
			if(goodsId != null && goodsId.length > 0){
				GoodsLiteNamedItem goodsLiteItem = null;
				List<GoodsLiteItem> goodsLiteItemList = new ArrayList<GoodsLiteItem>();
				int i=0;
				for(;i<goodsId.length;i++){
					goodsLiteItem = new GoodsLiteNamedItem();
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(Integer.parseInt(goodsId[i]));
					goodsLiteItem.setGoodsId(goodsBase.getId());
					goodsLiteItem.setGoodsName(goodsBase.getName());
					goodsLiteItem.setBindType(Byte.parseByte(goodsBind[i]));
					goodsLiteItem.setNum(Short.parseShort(goodsNum[i]));
					goodsLiteItem.setQualityType(goodsBase.getQualityType());
					goodsLiteItem.setGoodsLevel((byte)goodsBase.getLevel());
					goodsLiteItemList.add(goodsLiteItem);
				}
				respMsg.setGoodsItem(goodsLiteItemList);
			}
		}
		
		respMsg.setTime(item.getTime());
		respMsg.setMsg(item.getMsg());
		return respMsg;
	}

}

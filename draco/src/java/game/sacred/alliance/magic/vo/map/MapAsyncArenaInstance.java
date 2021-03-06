package sacred.alliance.magic.vo.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.attri.calct.FormulaCalct;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncMap;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.npc.NpcInstanceFactroy;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.DeathNotifySelfItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.google.common.collect.Lists;

/**
 * 异步竞技场
 * 
 * @author zhouhaobing
 * 
 */
public class MapAsyncArenaInstance extends MapInstance {

	private final LoopCount mapLoop = new LoopCount(1000);// 每1秒判断一次地图状态
	protected RoleInstance role;
	private AsyncPvpBattleInfo battleInfo = null;
	private AtomicBoolean roleEnter = new AtomicBoolean(false);
	private List<RoleHero> fightHeros = Lists.newArrayList();// 对手的英雄
	private long sendRewardTime = System.currentTimeMillis();
	private final static int KICK_ROLE_WHEN_OVER_TIME = 5 * 1000;
	private int pkIndex = 0;
	private MapState mapState = MapState.init;
	private NpcInstance targetNpc;
	protected AsyncPvpRoleAttr targetRoleAttr;
	private int x;
	private int y;

	@Override
	protected void enter(AbstractRole role) {
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		super.enter(role);
		RoleInstance r = (RoleInstance) role;
		this.role = r;
		if (roleEnter.compareAndSet(false, true)) {
			AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(r);
			asyncArenaRole.setChallengeNum((byte) (asyncArenaRole.getChallengeNum() - 1));
			GameContext.getRoleAsyncArenaApp().saveOrUpdRoleAsyncArena(r, asyncArenaRole);

			battleInfo = GameContext.getAsyncPvpApp().getAsyncPvpBattleInfo(role.getRoleId());
			if (battleInfo != null) {
				fightHeros = GameContext.getHeroApp().getRoleSwitchableHeroList(battleInfo.getTargetRoleId());
				targetRoleAttr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(battleInfo.getTargetRoleId());
				mapState = MapState.ready;
			}
			// 活跃度
			GameContext.getDailyPlayApp().incrCompleteTimes(r, 1, DailyPlayType.async_arena, "");
		}
	}

	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		return ForceRelation.enemy;
	}

	@Override
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, NpcInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		return ForceRelation.enemy;
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}

	@Override
	public void broadcastScreenMap(AbstractRole role, Message message, int expireTime) {
		super.broadcastMap(role, message, expireTime);
	}

	private void dispose_target_hero() {
		NpcInstance npcInstance = null;
		try {
			if (pkIndex >= fightHeros.size()) {
				mapState = MapState.game_over;
				return;
			}
			RoleHero pkHero = this.fightHeros.get(this.pkIndex);
			int onHeroId = this.targetRoleAttr.getHeroId();
			Point targetPoint = getTargetPoint();
			if (null == pkHero || null == targetPoint) {
				logger.error(this.getClass().getName() + "英雄为空");
				return;
			}

			x = targetPoint.getX();
			y = targetPoint.getY();

			if (targetNpc != null) {
				x = targetNpc.getMapX();
				y = targetNpc.getMapY();
			}

			RoleHero oldHero = null;
			for (RoleHero rh : this.fightHeros) {
				if (null != rh && rh.getHeroId() == onHeroId) {
					oldHero = rh;
					break;
				}
			}

			if (oldHero == null) {
				logger.error(this.getClass().getName() + "oldHero英雄为空");
			}

			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			AttriBuffer oldBuffer = GameContext.getHeroApp().getHeroAttriBuffer(oldHero);
			if (oldBuffer != null) {
				buffer.append(oldBuffer.reverse());
			}
			AttriBuffer pkBuffer = GameContext.getHeroApp().getHeroAttriBuffer(pkHero);
			buffer.append(pkBuffer);
			for (AttriItem ai : buffer.getMap().values()) {
				if (null == ai) {
					continue;
				}
				byte type = ai.getAttriTypeValue();
				int oldVal = this.targetRoleAttr.getAttriValue(type);
				float val = ai.getValue() + oldVal;
				int value = val >= 0 ? (int) val : 0;
				this.targetRoleAttr.setAttriValue(type, value);
			}

			GoodsHero gh = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, pkHero.getHeroId());

			this.targetRoleAttr.setHeroId(pkHero.getHeroId());
			// 先修改对手的英雄外形资源
			this.targetRoleAttr.setClothesResId(gh.getResId());
			this.targetRoleAttr.setGearId(gh.getGearId());
			this.targetRoleAttr.setHeroHeadId(gh.getHeadId());
			this.targetRoleAttr.setSeriesId(gh.getSeriesId());
			// 清除技能
			this.targetRoleAttr.getSkillInfoMap().clear();

			GameContext.getHeroApp().initSkill(pkHero);
			GameContext.getHeroApp().preToStore(pkHero);
			// 增加新英雄的技能ID
			this.targetRoleAttr.getSkillInfoMap().putAll(Util.parseShortIntMap(pkHero.getSkills()));

			npcInstance = NpcInstanceFactroy.createAsyncPvpNpcInstance(targetRoleAttr, targetPoint.getMapid(), x, y);
			npcInstance.setSpeed(FormulaCalct.DEFAULT_SPEED_VALUE);
			npcInstance.setMapInstance(this);
			npcInstance.setNpcBornDataIndex(-1);
			this.addAbstractRole(npcInstance);
			targetNpc = npcInstance;
			// 通知
			for (RoleInstance ri : this.getRoleList()) {
				C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
				RoleBodyItem item = Converter.getAsyncPvpRoleBodyItem(npcInstance.getRoleId(), targetRoleAttr, (short) x, (short) y);
				item.setHeroHeadId(gh.getHeadId());
				message.setItem(item);
				GameContext.getMessageCenter().send("", ri.getUserId(), message);
			}
			mapState = MapState.pk;

		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".dispose_target_hero error:", e);
		}
	}

	public MapAsyncArenaInstance(Map map) {
		super(map);
	}

	public enum MapState {
		init, // 初始化状态
		ready, // 准备阶段
		pk, // 准备阶段
		game_over, // gameover状态：发结算面板、奖励、记录结束时间
		kick_role, // 踢人状态：超过踢人保护时间后将角色从地图移除
		wait_destory, // 地图销毁阶段：销毁地图
		;
	}

	@Override
	public void updateSub() {
		try {
			super.updateSub();
			if (this.mapLoop.isReachCycle()) {

				if (mapState == MapState.ready) {
					dispose_target_hero();
					this.clearBaffle();
				}
				if (mapState == MapState.pk) {
					checkNpc();
				}
				if (mapState == MapState.game_over) {
					sendRewardTime = System.currentTimeMillis();
					this.mapState = MapState.kick_role;
					return;
				}
				if (mapState == MapState.kick_role) {
					if (System.currentTimeMillis() - sendRewardTime > KICK_ROLE_WHEN_OVER_TIME) {
						// 奖励发送完毕后10s钟后开始t人
						this.clearRole();
						this.mapState = MapState.wait_destory;
					}
				}
				if (mapState == MapState.wait_destory) {
					this.destroy();
				}
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + "clearRole error : ", e);
		}
	}

	private void checkNpc() {
		if (pkIndex + 1 < fightHeros.size()) {
			if (targetNpc.getCurHP() <= targetNpc.getMaxHP() * 0.2) {
				npcDeath(targetNpc);
			}
		}
	}

	@Override
	public void npcDeath(NpcInstance npc) {
		npc.setCurHP(0);
		super.npcDeath(npc);
		pkIndex++;
		dispose_target_hero();
	}

	private void clearRole() {
		// 踢人
		try {
			if (null != this.getRoleList()) {
				for (RoleInstance role : this.getRoleList()) {
					this.kickRole(role);
				}
			}
		} catch (Exception ex) {

		}
	}

	private void flagOver(ChallengeResultType result) {
		try {
			challengeOver(role, this.battleInfo, result);
			mapState = MapState.game_over;
		} catch (RuntimeException e) {
			logger.error("MapLadderInstance.flagOver error: ", e);
		}
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (victim.getRoleType() != RoleType.PLAYER) {
			return;
		}
		flagOver(ChallengeResultType.Lose);
	}

	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim) {
		if (pkIndex >= fightHeros.size()) {
			flagOver(ChallengeResultType.Win);
		}
	}

	@Override
	protected String createInstanceId() {
		instanceId = "async_arena_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	@Override
	public void exitMap(AbstractRole role) {
		try {
			super.exitMap(role);
			if (role.getRoleType() != RoleType.PLAYER) {
				return;
			}
			RoleInstance roleInstance = (RoleInstance) role;
			Point targetPoint = roleInstance.getCopyBeforePoint();
			if (null != targetPoint) {
				role.setMapId(targetPoint.getMapid());
				role.setMapX(targetPoint.getX());
				role.setMapY(targetPoint.getY());
			}
			// 满血满蓝
			this.perfectBody(role);
		} catch (Exception e) {
			logger.error("MapAsyncArenaInstance exitMap error",e);
		} finally {
			this.destroy();
		}
		
	}

	protected Point getTargetPoint() {
		AsyncMap config = GameContext.getAsyncArenaApp().getAsyncMap();
		if (null == config) {
			return null;
		}
		return new Point(config.getMapId(), config.getTargetMapX(), config.getTargetMapY());
	}

	protected void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType result) {
		GameContext.getRoleAsyncArenaApp().challengeOver(role, battleInfo, result);
	}

	@Override
	public boolean canDestroy() {
		if (this.getRoleCount() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	@Override
	protected void deathLog(AbstractRole victim) {
	}

	@Override
	public void useGoods(int goodsId) {
	}

	@Override
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role) {
		// 没有复活方式
		return null;
	}
}

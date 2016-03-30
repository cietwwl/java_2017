package sacred.alliance.magic.app.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.MenuHintItem;
import com.game.draco.message.item.MenuItem;

public class MenuAppImpl implements MenuApp{
	private final Logger logger = LoggerFactory.getLogger(MenuAppImpl.class);
	private List<MenuConfig> menuConfigList = null ;
	private Map<Short,MenuConfig> menuConfigMap = new HashMap<Short,MenuConfig>();
	private Map<Short,HintConfig> hintConfigMap = new HashMap<Short,HintConfig>();

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		String fileName = XlsSheetNameType.menu_config.getXlsName();
		String sheetName = XlsSheetNameType.menu_config.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		List<MenuConfig> configList = XlsPojoUtil.sheetToList(sourceFile, sheetName, MenuConfig.class);
		/*Collections.sort(configList, new Comparator<MenuConfig>(){
			@Override
			public int compare(MenuConfig o1, MenuConfig o2) {
				if(o1.getPriority() >= o2.getPriority()){
					return 1 ;
				}
				return 0;
			}
		}) ;*/
		this.menuConfigMap.clear();
		for(MenuConfig config : configList){
			if(null == config){
				continue;
			}
			this.register(config);
		}
		this.menuConfigList = configList ;
		
		//加载功能菜单开启提示配置
		this.loadHintConfig();
	}
	
	private void loadHintConfig(){
		String fileName = XlsSheetNameType.menu_hit.getXlsName();
		String sheetName = XlsSheetNameType.menu_hit.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			Set<Byte> levelSet = new HashSet<Byte>();
			List<HintConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, HintConfig.class);
			for(HintConfig config : list){
				if(null == config){
					continue;
				}
				short id = config.getId();
				if(id < 0){
					Log4jManager.CHECK.error(info + "id=" + id + " is error!");
					Log4jManager.checkFail();
					continue;
				}
				if(this.hintConfigMap.containsKey(id)){
					Log4jManager.CHECK.error(info + "id=" + id + " is repeated!");
					Log4jManager.checkFail();
					continue;
				}
				byte level = config.getLevel();
				if(level <= 0){
					Log4jManager.CHECK.error(info + "level=" + level + " is error!");
					Log4jManager.checkFail();
					continue;
				}
				if(levelSet.contains(level)){
					Log4jManager.CHECK.error(info + "level=" + level + " is repeated!");
					Log4jManager.checkFail();
					continue;
				}
				if(config.isOpenHint() && Util.isEmpty(config.getHintInfo())){
					Log4jManager.CHECK.error(info + "id=" + id + " hintType=1 but no have hintInfo!");
					Log4jManager.checkFail();
					continue;
				}
				this.hintConfigMap.put(id, config);
				levelSet.add(level);
			}
		} catch (RuntimeException e) {
			Log4jManager.CHECK.error(info);
			Log4jManager.checkFail();
		}
	}

	private void register(MenuConfig config){
		try {
			if(null == config){
				return;
			}
			MenuIdType menuType = MenuIdType.get(config.getMenuId());
			if(null == menuType){
				Log4jManager.CHECK.error("create menufunc null,the menuType=" + config.getMenuType());
				Log4jManager.checkFail();
				return ;
			}
			if(this.menuConfigMap.containsKey(menuType)){
				Log4jManager.CHECK.error("too many same menuType,the menuType=" + config.getMenuType());
				Log4jManager.checkFail();
			}
			//判断活动是否存在
			int activeId = config.getActiveId() ;
			if(activeId > 0){
				Active active = GameContext.getActiveApp().getActive((short)activeId);
				if(null == active){
					Log4jManager.CHECK.error("menu config error,acitve not exist,acitveId=" + activeId);
					Log4jManager.checkFail();
				}
			}
			this.menuConfigMap.put((short)menuType.getType(),config );
			//进行初始化
			config.init();
			MenuFunc func = config.getMenuFunc();
			func.start();//启动功能
			MenuBefore before = config.getMenuBefore();
			if(null != before){
				before.start(); //启动提前x分钟提示
			}
		} catch (Exception e) {
			this.logger.error("menuApp.register by type error, menuType=" + config.getMenuType(), e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public List<MenuItem> getMenuList(RoleInstance role) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		if(Util.isEmpty(this.menuConfigList)){
			return list ;
		}
		for(MenuConfig config : this.menuConfigList){
			MenuFunc func = config.getMenuFunc() ;
			if(null == func){
				continue ;
			}
			MenuItem item = func.getMenuItem(role);
			if(null == item){
				continue ;
			}
			
			list.add(item);
		}
		return list;
	}
	
	private MenuFunc getMenuFunc(MenuIdType menuType){
		if(null == menuType){
			return null ;

		}
		MenuConfig config = this.menuConfigMap.get((short)menuType.getType());
		if(null == config){
			return null ;
		}
		return config.getMenuFunc() ;
	}

	@Override
	public void refresh(MenuIdType menuType) {
		if(null == menuType){
			return ;
		}
		MenuFunc func = this.getMenuFunc(menuType);
		if(null == func){
			return;
		}
		for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
			try {
				if(null == role){
					continue;
				}
				//助手的刷新
				func.refresh(role);
			} catch (Exception e) {
				this.logger.error("menuApp.refresh by type error, MenuType=" + menuType, e);
			}
		}
	}

	@Override
	public void refresh(RoleInstance role, MenuIdType menuType) {
		if(null == role || null == menuType){
			return ;
		}
		try {
			MenuFunc func = this.getMenuFunc(menuType);
			if(null == func){
				return;
			}
			//刷新
			func.refresh(role);
		} catch (Exception e) {
			this.logger.error("menuApp.refresh by role and type error: ", e);
		}
	}

	@Override
	public void refreshByUpgrade(RoleInstance role) {
		if(null == role || Util.isEmpty(this.menuConfigList)){
			return ;
		}
		for(MenuConfig config : this.menuConfigList){
			if(null == config){
				continue;
			}
			try {
				config.getMenuFunc().refreshByUpgrade(role);
			} catch (Exception e) {
				this.logger.error("menuApp.refreshByUpgrade error: roleId=" + role.getRoleId(), e);
			}
		}
	}

	@Override
	public MenuConfig getMenuConfig(MenuIdType menuType) {
		if(null == menuType){
			return null ;
		}
		return this.menuConfigMap.get((short)menuType.getType());
	}

	@Override
	public MenuConfig getMenuConfigById(short menuId) {
		return this.menuConfigMap.get(menuId);
	}
	
	@Override
	public List<MenuHintItem> getHintList(RoleInstance role) {
		List<MenuHintItem> hintList = new ArrayList<MenuHintItem>();
		for(HintConfig config : this.hintConfigMap.values()){
			if(null == config){
				continue;
			}
			byte level = config.getLevel();
			//当前等级已经开启了，就不再下发
			if(role.getLevel() >= level){
				continue;
			}
			MenuHintItem item = new MenuHintItem();
			item.setId(config.getId());
			item.setLevel(level);
			item.setHintType(config.getHintType());
			item.setHintInfo(config.getHintInfo());
			hintList.add(item);
		}
		return hintList;
	}

	@Override
	public boolean isOpenFun(RoleInstance role,MenuIdType menuType) {
		for(MenuConfig config : this.menuConfigMap.values()){
			if(null == config){
				continue;
			}
			if(config.getMenuId() == menuType.getType() && role.getLevel() >= config.getRoleLevel()){
				return true;
			}
		}
		return false;
	}
	
	

}

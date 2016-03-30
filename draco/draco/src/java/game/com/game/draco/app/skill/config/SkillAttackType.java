package com.game.draco.app.skill.config;

public enum SkillAttackType {
	
	Default((byte)0,"默认"),
	NormalAttack((byte)1,"普通攻击"),
	Dash((byte)2,"冲锋"),
	Telesport((byte)3,"闪现"),//
	Relive((byte)4,"复活"),
	recover((byte)5,"恢复"),
	
	;
	
	private final byte type;
	private final String name ;
	
	public static SkillAttackType get(byte type){
		for (SkillAttackType item : SkillAttackType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return Default ;
	}
	
	SkillAttackType(byte type,String name){
		this.type = type;
		this.name = name ;
	}
	public byte getType(){
		return type;
	}

	public String getName() {
		return name;
	}
	
	
}

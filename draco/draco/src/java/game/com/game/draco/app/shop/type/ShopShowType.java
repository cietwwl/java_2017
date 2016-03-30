package com.game.draco.app.shop.type;

public enum ShopShowType {
	
	Hot((byte)0,true,"热卖"),
	Derive((byte)1,true,"强化"),
	Assist((byte)2,true,"养成"),
	Fashion((byte)3,true,"时装"),
	BindMoney((byte)4,false,"免费"),
	
	;
	
	private final byte type;
	private final boolean goldMoney;//是否金条购买
	private final String name;
	
	ShopShowType(byte type, boolean goldMoney, String name){
		this.type = type;
		this.goldMoney = goldMoney;
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public static ShopShowType get(byte type){
		for(ShopShowType item : ShopShowType.values()){
			if(item.getType() == type){
				return item;
			}
		}
		return null;
	}

	public boolean isGoldMoney() {
		return goldMoney;
	}
	
}

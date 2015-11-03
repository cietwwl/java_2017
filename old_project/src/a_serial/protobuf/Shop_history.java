package a_serial.protobuf;

import java.io.IOException;
import java.util.List;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;

public class Shop_history{
	@Protobuf(fieldType = FieldType.STRING, order = 1)
	private String shopId;
	private byte[] data;
	
	@Protobuf(fieldType = FieldType.OBJECT, order = 2)
	private List<GoodsRecord> goodsRecordList;

	public Shop_history(){
		super();
	}
	
	public byte[] getData(){
		return data;
	}
	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public List<GoodsRecord> getGoodsRecordList() {
		return goodsRecordList;
	}

	public void setGoodsRecordList(List<GoodsRecord> goodsRecordList) {
		this.goodsRecordList = goodsRecordList;
	}

	public static List<GoodsRecord> parseData(byte[] targetData)
			throws IOException {
		Codec<Shop_history> codec = ProtobufProxy.create(Shop_history.class);
		Shop_history shop = codec.decode(targetData) ;
		return shop.goodsRecordList;
	}

	private byte[] buildData() {
		try {
			Codec<Shop_history> codec = ProtobufProxy.create(Shop_history.class);
			return codec.encode(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void preToData() {
		this.data = buildData();
	}

	public Shop_history postFromData() {
		try {
			goodsRecordList = parseData(this.data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
}

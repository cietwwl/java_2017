package com.game.draco.component.ssdb;

import java.util.List;

/**
 * SSDB Java client SDK.
 * Example:
 * <pre>
 * SSDB ssdb = new SSDB("127.0.0.1", 8888);
 * ssdb.set("a", "123");
 * byte[] val = ssdb.get("a");
 * </pre>
 */
public class SSDB{
	
	private final String UTF8 = "UTF-8" ;
	private Link link;

	public SSDB(String host, int port) throws Exception{
		this(host, port, 0);
	}

	public SSDB(String host, int port, int timeout_ms) throws Exception{
		link = new Link(host, port, timeout_ms);
	}
	
	public void close(){
		link.close();
	}
	
	public boolean isAvailable(){
		return (null != link && link.isAvailable()) ;
	}
	
	/* kv */

	public void set(byte[] key, byte[] val) throws Exception{
		Response resp = link.request("set", key, val);
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void set(String key, byte[] val) throws Exception{
		set(key.getBytes(UTF8), val);
	}
	
	public void set(String key, String val) throws Exception{
		set(key, val.getBytes(UTF8));
	}

	public void del(byte[] key) throws Exception{
		Response resp = link.request("del", key);
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void del(String key) throws Exception{
		del(key.getBytes(UTF8));
	}

	/***
	 * 
	 * @param key
	 * @return null if not found
	 * @throws Exception
	 */
	public byte[] get(byte[] key) throws Exception{
		Response resp = link.request("get", key);
		if(resp.not_found()){
			return null;
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		if(resp.ok()){
			return resp.raw.get(1);
		}
		resp.exception();
		return null;
	}
	
	/***
	 * 
	 * @param key
	 * @return null if not found
	 * @throws Exception
	 */
	public byte[] get(String key) throws Exception{
		if(null == key){
			return null ;
		}
		return get(key.getBytes(UTF8));
	}
	
	public String getString(String key)	throws Exception{
		byte[] b = this.get(key) ;
		if(null == b){
			return null ;
		}
		return new String(b,UTF8) ;
	}

	private Response _scan(String cmd, String key_start, String key_end, int limit) throws Exception{
		if(key_start == null){
			key_start = "";
		}
		if(key_end == null){
			key_end = "";
		}
		Response resp = link.request(cmd, key_start, key_end, (new Integer(limit)).toString());
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public Response scan(String key_start, String key_end, int limit) throws Exception{
		return _scan("scan", key_start, key_end, limit);
	}
	
	public Response rscan(String key_start, String key_end, int limit) throws Exception{
		return _scan("rscan", key_start, key_end, limit);
	}
	
	public long incr(String key, long by) throws Exception{
		Response resp = link.request("incr", key, (new Long(by)).toString());
		if(!resp.ok()){
			resp.exception();
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		long ret = 0;
		ret = Long.parseLong(new String(resp.raw.get(1)));
		return ret;
	}
	
	/* hashmap */

	public void hset(String name, byte[] key, byte[] val) throws Exception{
		Response resp = link.request("hset", name.getBytes(UTF8), key, val);
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void hset(String name, String key, byte[] val) throws Exception{
		this.hset(name, key.getBytes(UTF8), val);
	}
	
	public void hset(String name, String key, String val) throws Exception{
		this.hset(name, key, val.getBytes(UTF8));
	}
	
	public void hdel(String name, byte[] key) throws Exception{
		Response resp = link.request("hdel", name.getBytes(UTF8), key);
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void hdel(String name, String key) throws Exception{
		this.hdel(name, key.getBytes(UTF8));
	}

	/**
	 * 
	 * @param name
	 * @param key
	 * @return null if not found
	 * @throws Exception
	 */
	public byte[] hget(String name, byte[] key) throws Exception{
		Response resp = link.request("hget", name.getBytes(UTF8), key);
		if(resp.not_found()){
			return null;
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		if(resp.ok()){
			return resp.raw.get(1);
		}
		resp.exception();
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @param key
	 * @return null if not found
	 * @throws Exception
	 */
	public byte[] hget(String name, String key) throws Exception{
		return hget(name, key.getBytes(UTF8));
	}
	
	public String hgetString(String name, String key) throws Exception{
		return new String(this.hget(name, key),UTF8);
	}
	
	public Response hgetAll(String name) throws Exception {
		return link.request("hget", name.getBytes(UTF8));
	}

	private Response _hscan(String cmd, String name, String key_start, String key_end, int limit) throws Exception{
		if(key_start == null){
			key_start = "";
		}
		if(key_end == null){
			key_end = "";
		}
		Response resp = link.request(cmd, name, key_start, key_end, (new Integer(limit)).toString());
		if(!resp.ok()){
			resp.exception();
		}
		for(int i=1; i<resp.raw.size(); i+=2){
			byte[] k = resp.raw.get(i);
			byte[] v = resp.raw.get(i+1);
			resp.keys.add(k);
			resp.items.put(k, v);
		}
		return resp;
	}
	
	public Response hscan(String name, String key_start, String key_end, int limit) throws Exception{
		return this._hscan("hscan", name, key_start, key_end, limit);
	}
	
	public Response hrscan(String name, String key_start, String key_end, int limit) throws Exception{
		return this._hscan("hrscan", name, key_start, key_end, limit);
	}
	
	public long hincr(String name, String key, long by) throws Exception{
		Response resp = link.request("hincr", name, key, (new Long(by)).toString());
		if(!resp.ok()){
			resp.exception();
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		long ret = 0;
		ret = Long.parseLong(new String(resp.raw.get(1)));
		return ret;
	}
	
	/* zset */
	
	public long zsize(String name) throws Exception{
		Response resp = link.request("zsize", name.getBytes(UTF8));
		if(!resp.ok()){
			resp.exception();
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		return Long.parseLong(new String(resp.raw.get(1)));
	}

	public void zset(String name, byte[] key, long score) throws Exception{
		Response resp = link.request("zset", name.getBytes(UTF8), key, String.valueOf(score).getBytes(UTF8));
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void zset(String name, String key, long score) throws Exception{
		zset(name, key.getBytes(UTF8), score);
	}
	
	public void zdel(String name, byte[] key) throws Exception{
		Response resp = link.request("zdel", name.getBytes(UTF8), key);
		if(resp.ok()){
			return;
		}
		resp.exception();
	}
	
	public void zdel(String name, String key) throws Exception{
		this.zdel(name, key.getBytes(UTF8));
	}
	
	/**
	 * 
	 * @param name
	 * @param key
	 * @return Double.NaN if not found.
	 * @throws Exception
	 */
	public double zget(String name, byte[] key) throws Exception{
		Response resp = link.request("zget", name.getBytes(UTF8), key);
		if(resp.not_found()){
			return Double.NaN;
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		if(resp.ok()){
			return Double.parseDouble(new String(resp.raw.get(1)));
		}
		resp.exception();
		return 0;
	}

	/**
	 * 
	 * @param name
	 * @param key
	 * @return Double.NaN if not found.
	 * @throws Exception
	 */
	public double zget(String name, String key) throws Exception{
		return zget(name, key.getBytes(UTF8));
	}

	private Response _zscan(String cmd, String name, String key, String score_start, String score_end, int limit) throws Exception{
		if(key == null){
			key = "";
		}
		if(score_start == null){
			score_start = "" ;
		}
		if(score_end == null){
			score_end = "" ;
		}
		Response resp = link.request(cmd, name, key, score_start, score_end, String.valueOf(limit));
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public Response zscan(String name, String key, String score_start, String score_end, int limit) throws Exception{
		return this._zscan("zscan", name, key, score_start, score_end, limit);
	}
	
	/**
	 * 按照score 升序的排名
	 * 如果一共N条记录,分数最低的返回0,分数最高的返回 N-1
	 * 返回数值[0-N),-1表示不存在
	 * 
	 * @param name
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public int zrank(String name,String key) throws Exception {
		Response resp = link.request("zrank", name,key);
		return this.getIntValue(resp) ;
	}
	
	/**
	 * 按照score 降序的排名
	 * 如果一共N条记录,分数最高的返回0,分数最低的返回 N-1
	 * 返回数值[0-N),-1表示不存在
	 * 
	 * @param name
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public int zrrank(String name,String key) throws Exception {
		Response resp = link.request("zrrank", name,key);
		return this.getIntValue(resp) ;
	}
	
	private int getIntValue(Response resp) throws Exception{
		if(!resp.ok()){
			resp.exception();
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		if(resp.ok()){
			return Integer.parseInt(new String(resp.raw.get(1)));
		}
		resp.exception();
		return -1;
	}
	
	public int zcount(String name,String score_start, String score_end) throws Exception{
		Response resp = link.request("zcount", name, score_start, score_end);
		return this.getIntValue(resp) ;
	}
	
	public Response zrscan(String name, String key, String score_start, String score_end, int limit) throws Exception{
		return this._zscan("zrscan", name, key, score_start, score_end, limit);
	}
	
	
	public long zincr(String name, String key, long by) throws Exception{
		Response resp = link.request("zincr", name, key, String.valueOf(by));
		if(!resp.ok()){
			resp.exception();
		}
		if(resp.raw.size() != 2){
			throw new Exception("Invalid response");
		}
		long ret = 0;
		ret = Long.parseLong(new String(resp.raw.get(1)));
		return ret;
	}

	/****************/
	
	public Response multi_get(String...keys) throws Exception{
		Response resp = link.request("multi_get", keys);
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public Response multi_get(List<String> keys) throws Exception{
		Response resp = link.requestString("multi_get", keys);
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public Response multi_get(byte[]...keys) throws Exception{
		Response resp = link.request("multi_get", keys);
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public void multi_set(String...kvs) throws Exception{
		if(kvs.length % 2 != 0){
			throw new Exception("Invalid arguments count");
		}
		Response resp = link.request("multi_set", kvs);
		if(!resp.ok()){
			resp.exception();
		}
	}
	
	public void multi_set(byte[]...kvs) throws Exception{
		if(kvs.length % 2 != 0){
			throw new Exception("Invalid arguments count");
		}
		Response resp = link.request("multi_set", kvs);
		if(!resp.ok()){
			resp.exception();
		}
	}
	
	public Response multi_del(String...keys) throws Exception{
		Response resp = link.request("multi_del", keys);
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
	
	public Response multi_del(byte[]...keys) throws Exception{
		Response resp = link.request("multi_del", keys);
		if(!resp.ok()){
			resp.exception();
		}
		resp.buildMap();
		return resp;
	}
}

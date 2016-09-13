package com.misty.engine.network;

import java.nio.ByteBuffer;

public class Packet {
	public byte id;
	public short size;
	public byte[] data;
	ByteBuffer buffer;

	private Packet(int size) {
		data = new byte[size];
		buffer = ByteBuffer.wrap(data);
	}

	public Packet(byte id, short s, byte[] payload) {
		this(s+3);
		this.id = id;
		this.size = s;
		put(id);
		putShort(s);
		put(payload);
		
	}
	public void toPayload() {
		buffer.position(3);
	}

	public Packet(int i, int j) {
		this(j+3);
		this.id = (byte)i;
		this.size = (short)j;
		put(id);
		putShort(size);
	}

	public void putString(String str) {
		assert(buffer.remaining() >= str.length() + 2);
		buffer.putShort((short)str.length());
		buffer.put(str.getBytes());
	}

	public void putInt(int i) {
		assert(buffer.remaining() >= 4);
		buffer.putInt(i);
	}

	public void putLong(long i) {
		assert(buffer.remaining() >= 8);
		buffer.putLong(i);
	}

	public void putShort(short i) {
		assert(buffer.remaining() >= 2);
		buffer.putShort(i);
	}

	public void putDouble(double i) {
		assert(buffer.remaining() >= 8);
		buffer.putDouble(i);
	}

	public void putBoolean(boolean i) {
		assert(buffer.remaining() >= 1);
		buffer.put(i?(byte)1:(byte)0);
	}
	
	public void putFloat(float i) {
		assert(buffer.remaining() >= 4);
		buffer.putFloat(i);
	}
	
	public void put(byte b) {
		buffer.put(b);
	}
	
	public void put(byte[] b) {
		buffer.put(b);
	}
	
	public void flush() {
		buffer.rewind();
	}
	
	public String getString() {
		short len = buffer.getShort();
		byte[] str = new byte[len];
		buffer.get(str, buffer.arrayOffset(), len);
		return new String(str);
	}

	public int getInt() {
		return buffer.getInt();
	}

	public long getLong() {
		return buffer.getLong();
	}

	public short getShort() {
		return buffer.getShort();
	}

	public double getDouble() {
		return buffer.getDouble();
	}
	
	public float getFloat() {
		return buffer.getFloat();
	}
	
	public boolean putBoolean() {
		return buffer.get() == 1;
	}

	
	
}

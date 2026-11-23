package com.indolibs.libs;

interface HttpHelper<T>{
	Object getStrObj() throws Exception;
	T getValue();
	void message(String str);
	void message(byte[] bytes);
	void setStrObj(String str);
	void setValue(T t);
	void state(String str);
}
package com.indolibs.libs;

import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

public class Content<T> implements HttpHelper<T> {
    private String strJson = "";
    private T value;

    public Content() {}

    public Content(T value) {
        this.value = value;
        if (this.value != null) {
            setValue(this.value);
        }
    }

    @Override 
    public void message(String str) {}
	
    @Override 
    public void message(byte[] bytes) {}
	
    @Override 
    public void setValue(T value) {
        this.value = value;
    }

    @Override 
    public T getValue() {
        return this.value;
    }

    @Override 
    public Object getStrObj() throws Exception {
        return new StringBuilder(this.strJson);
    }

    @Override 
    public void setStrObj(String str) {
        this.strJson = str;
    }
	
    @Override 
    public void state(String str) {}
}
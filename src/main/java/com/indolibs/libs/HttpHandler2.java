package com.indolibs.libs;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
//import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

public class HttpHandler2 {
    private Content<?> content;
    private String stringUrl;

    public HttpHandler2(Content<?> content) {
        this.content = content;
    }

    static String convertStreamToString2(InputStream inputStream) {
        Scanner scannerUseDelimiter = new Scanner(inputStream).useDelimiter("\\A");
        return scannerUseDelimiter.hasNext() ? scannerUseDelimiter.next() : "";
    }
	
	private HttpURLConnection requestConnection(String strUrl, String[] arrHeader, String[] arrData, String strMethod){
		String str = "";
		
		if (arrData != null) {
            try {
                if (arrData.length != 0) {
                    for (int i = 0; i < arrData.length; i++) {
                        String[] arrDataSplit = arrData[i].split(":", 2);
                        if (i > 0) {
                            str = str + "&";
                        }
                        str = str + arrDataSplit[0] + "=" + arrDataSplit[1];
                    }
                }
            } catch (Exception e) {
				 System.err.println("Error: " + e.getMessage());
            }
        }
        HttpURLConnection HttpURLConnection=null;
        try {

			URL url = new URI(strUrl).toURL();
			HttpURLConnection = (HttpURLConnection) url.openConnection();
            boolean hasHeaderAccept = false;
            if (arrHeader != null && arrHeader.length > 0) {
                for (String header : arrHeader) {
                    String[] arrHeaderSplit = header.split(":",2);
                    if (!arrHeaderSplit[0].toLowerCase().equals("content-type") && 
                        !arrHeaderSplit[0].toLowerCase().equals("user-agent") && 
                        !arrHeaderSplit[0].toLowerCase().equals("accept-language") && 
                        !arrHeaderSplit[0].toLowerCase().equals("connection")) {
                        HttpURLConnection.setRequestProperty(arrHeaderSplit[0], arrHeaderSplit[1]);
                    }
					
					if (arrHeaderSplit[0].toLowerCase().equals("accept")) {
                        hasHeaderAccept = true;
                    }
                }
            }
            
            HttpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            HttpURLConnection.setRequestProperty("Connection", "keep-alive");
            HttpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            HttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			if (!hasHeaderAccept) {
				HttpURLConnection.setRequestProperty("Accept", "*/*"); 
			}
			
            if (strMethod.toUpperCase().equals("POST")) {
                HttpURLConnection.setDoOutput(true);
                HttpURLConnection.getOutputStream().write(str.getBytes("utf-8"));
            }
            
            HttpURLConnection.connect();
		} catch (Exception e) {
            System.err.println("Error request service: " + e.getMessage());
        }
		return HttpURLConnection;
	}
	
    public String requestService(String strUrl, String[] arrHeader, String[] arrData, String strMethod) {
        String strConvertStreamToString2 = null;
        try {
			HttpURLConnection HttpURLConnection=requestConnection(strUrl,arrHeader,arrData,strMethod);
            strConvertStreamToString2 = convertStreamToString2(new BufferedInputStream(HttpURLConnection.getInputStream()));
            HttpURLConnection.disconnect();
        } catch (Exception e) {
            System.err.println("Error request service: " + e.getMessage());
        }
        
        return strConvertStreamToString2;
    }
	
	public byte[] requestBytesService(String strUrl, String[] arrHeader, String[] arrData, String strMethod) {
        byte[] bytes = null;
        try {
            HttpURLConnection HttpURLConnection=requestConnection(strUrl,arrHeader,arrData,strMethod);

            if (HttpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream input = new BufferedInputStream(HttpURLConnection.getInputStream());
                     ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    
                    byte[] data = new byte[4096];
                    int nRead;
                    while ((nRead = input.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    bytes = buffer.toByteArray();
                }
            }
            HttpURLConnection.disconnect();
        } catch (Exception e) {
            System.err.println("Error unduh gambar: " + e.getMessage());
        }
        return bytes;
    }
	
	public class HttpBytesLoader extends Thread {
		private String[] dataPosting;
        private final String url;
        private final String[] headers;
        private final HttpHandler2 httpHandler2;
		private final String method;
		private boolean isStart = true;
		private byte[] bytes=null;
		private boolean isFinished = false;
		
        public HttpBytesLoader(HttpHandler2 httpHandler2,String url, String[] headers,String[] dataPosting, String method) {
            this.url = url;
            this.headers = headers;
            this.httpHandler2 = httpHandler2;
			this.method = method;
        }

        @Override
        public void run() {
            this.bytes = requestBytesService(this.url, this.headers, this.dataPosting, this.method);
			
			if (this.bytes!=null && this.httpHandler2.content != null) {
				setBytes(this.bytes);
				this.httpHandler2.content.message(this.bytes);
				this.httpHandler2.content.state("{state : success}");
			}
			
			if (this.bytes == null) {
				this.httpHandler2.content.state("{state : failed}");
			}
			
			this.isFinished = true;
			
        }

        public HttpBytesLoader executeOnBackground() {
            if (this.isStart) {
				this.isStart = false;
				start();
				
			}
			return this;
        }
		
		public byte[] getBytes() {
			if (this.isStart) {
				this.isStart = false;
				start();
			}
			return this.bytes;
		}
		
        public void setBytes(byte[] bytes) {
			this.bytes=bytes;
        }
		
		public boolean isFinished() {
			return this.isFinished;
		}
    }

    public class HttpDataLoader<T> extends Thread implements HttpHelper<T> {
        private String[] dataPosting;
        private String[] headers;
        private String jsonStr;
        private String method;
        private final HttpHandler2 httpHandler2;
        private String url;
        private boolean isWaiting = true;
		private boolean isAsync = false;
		private boolean isFinished = false;
		
		public HttpDataLoader(HttpHandler2 httpHandler2, String url, String[] headers, String[] dataPosting, String method) {
            this.httpHandler2 = httpHandler2;
            this.jsonStr = null;
            this.method = method;
            this.headers = headers;
            this.dataPosting = dataPosting;
            this.url = url;
        }
		
		private synchronized void startAndWaitCurrentThread() {
			if (this.isWaiting) {
				this.isWaiting = false;
				start();
			}
			while (!this.isFinished) {
				try {
					wait();
				} catch (InterruptedException e) {
					currentThread().interrupt();
					System.err.println("Error: " + e.getMessage());
					break;
				}
			}
		}

        @Override
        public void run() {
			try {
				this.jsonStr = this.httpHandler2.requestService(this.url, this.headers, this.dataPosting, this.method);
				if (this.httpHandler2.content != null&&this.jsonStr != null) {
						this.httpHandler2.content.setStrObj(this.jsonStr);
						state("{state : success}");

				}else{
					state("{state : failed}");
				}
			} catch (Exception e) {
				state("{state : failed, message : "+e.getMessage()+"}");
			}finally {
				
				if (this.isAsync && this.httpHandler2.content != null) {
					this.httpHandler2.content.message(this.jsonStr);
					this.isAsync=false;
				}

				notifyAll();
				synchronized (this) {
					this.isFinished = true;
					notifyAll();
				}
				
			}

        }

		@Override
        public void state(String str){
			this.httpHandler2.content.state(str);
		}

        @Override
		public T getValue() {
			startAndWaitCurrentThread();
			return (this.httpHandler2.content != null) ? ((Content<T>)  this.httpHandler2.content).getValue() : null;
		}
		
		@Override
        public void setValue(T value) {
            if (this.httpHandler2.content != null) {
				((Content<T>)  this.httpHandler2.content).setValue(value);
            }
        }
		
		public synchronized HttpDataLoader executeOnBackground() {
            if (this.isWaiting) {
                this.isWaiting = false;
                this.isAsync = true;
                start();
            }
			return this;
        }

        public HttpDataLoader execute() {
            startAndWaitCurrentThread();
            message(this.jsonStr);
			return this;
        }
		
		public synchronized boolean isFinished() {
			return this.isFinished;
		}
		
		@Override 
		public void message(byte[] bytes){
			this.httpHandler2.content.message(bytes);
		}

        @Override
        public void message(String str) {
            startAndWaitCurrentThread();
            if (this.httpHandler2.content != null) {
                this.httpHandler2.content.message(str);
            }
        }

        @Override
        public Object getStrObj() throws Exception {
            startAndWaitCurrentThread();
            return (this.httpHandler2.content != null) ? this.httpHandler2.content.getStrObj() : null;
        }

        @Override
        public void setStrObj(String str) {
            if (this.httpHandler2.content != null) {
                this.httpHandler2.content.setStrObj(str);
            }
        }
    }
}

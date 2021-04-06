package com.mytools.util;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


//import com.sitech.miso.common.util.SSLClient;

public class MyHttpsUtils {

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[]{};
		}
	}
	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	/**
	 * * post方式请求服务器(https协议) * * @param url * 请求地址 * @param content * 参数
	 * * @param charset * 编码 * @return * @throws NoSuchAlgorithmException
	 * * @throws KeyManagementException * @throws IOException
	 */
	public static String post(String url, String content, String charset) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
		URL console = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.setDoOutput(true);
		conn.connect();
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.write(content.getBytes(charset));
		// 刷新、关闭
		out.flush();
		out.close();
		InputStream is = conn.getInputStream();
		if (is != null) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			 return new String(outStream.toByteArray());
			
		}
		return null;
	}

	public static String post(String url, String content, String charset,Map<String, String> headerParameters) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);
        URL console = new URL(url);
        //System.setProperty("https.protocols", "TLSv1.2,TLSv1.1");
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setDoOutput(true);
        conn.setDoInput(true); 
        // 设置 header
        if (headerParameters != null) {
            Iterator<String> iteratorHeader = headerParameters.keySet()
                    .iterator();
            while (iteratorHeader.hasNext()) {
                String key = iteratorHeader.next();
                conn.setRequestProperty(key,
                        headerParameters.get(key));
            }
        }
        conn.setRequestProperty("Content-Type",
                "application/json;charset=" + charset);
        conn.connect();
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(content.getBytes(charset));
        // 刷新、关闭
        out.flush();
        out.close();
        InputStream is = conn.getInputStream();
        if (is != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
             return new String(outStream.toByteArray());
            
        }
        return null;
    }
	
	public static String get(String url, String charset) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		SSLContext sc = SSLContext.getInstance("TLSv1.2");
		sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);
		URL console = new URL(url);
		//System.setProperty("https.protocols", "TLSv1.2,TLSv1.1");
		HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
		conn.setSSLSocketFactory(sc.getSocketFactory());
		conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
		conn.setDoOutput(true);
		conn.setDoInput(true); 
		
		conn.setRequestProperty("Content-Type",
				"application/json;charset=" + charset);
		conn.connect();

		InputStream is = conn.getInputStream();
		if (is != null) {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			return new String(outStream.toByteArray(),charset);
			
		}
		return null;
	}
	
    /**
     * @param url  地址
     * @param header 请求头
     * @param content 请求内容
     * @param charset 编码
     * @param contentType 请求方式
     * @param SSLversion SSL版本
     * @return
     */
    public static String doPost(String url, Map<String, String> header,String content,String charset,String contentType,String SSLversion){  
        HttpClient httpClient = null;  
        HttpPost httpPost = null;  
        String result = null;  
        try{  
            httpClient = new SSLClient(SSLversion);  
            httpPost = new HttpPost(url);  
          //请求头header信息
            if (MapUtils.isNotEmpty(header)) {
                for (Map.Entry<String, String> stringStringEntry : header.entrySet()) {
                    httpPost.setHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }
            //设置参数  
            StringEntity entity = new StringEntity(content,"utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType(contentType);
            httpPost.setEntity(entity);
            /*List<NameValuePair> list = new ArrayList<NameValuePair>();  
            Iterator iterator = map.entrySet().iterator();  
            while(iterator.hasNext()){  
                Entry<String,String> elem = (Entry<String, String>) iterator.next();  
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));  
            }  
            if(list.size() > 0){  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);  
                httpPost.setEntity(entity);  
            }  */
            HttpResponse response = httpClient.execute(httpPost);  
            if(response != null){  
                HttpEntity resEntity = response.getEntity();  
                if(resEntity != null){  
                    result = EntityUtils.toString(resEntity,charset);  
                }  
            } else {
            } 
        }catch(Exception ex){  
            ex.printStackTrace();  
        }  
        return result;  
    }  
    
    

    public static void main(String[] args) {
        String hpptsPostUrl = "https://api.cmburl.cn:48065/polypay/v1.0/mchorders/onlinepay";
        //doPostRequest(hpptsPostUrl, null, "11", null);
        /*Map<String,String> createMap = new HashMap<String,String>();  
        createMap.put("authuser","*****");  
        createMap.put("authpass","*****");  
        createMap.put("orgkey","****");  
        createMap.put("orgname","****"); */
        //doPost(hpptsPostUrl,null,"1111",null);
        try {
            post(hpptsPostUrl, "1111", "UTF-8", null);
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
class SSLClient extends DefaultHttpClient{  
    public static final String TLS_12="TLSv1.2";
    public static final String TLS_11="TLSv1.1";
    public SSLClient(String SSLversion) throws Exception{  
        super();
        //传输协议需要根据自己的判断　  
        SSLContext ctx = SSLContext.getInstance(SSLversion);  
        X509TrustManager tm = new X509TrustManager() {  
                public void checkClientTrusted(X509Certificate[] chain,  
                        String authType) throws CertificateException {  
                }  
                public void checkServerTrusted(X509Certificate[] chain,  
                        String authType) throws CertificateException {  
                }  
                public X509Certificate[] getAcceptedIssuers() {  
                    return null;  
                }  
        };  
        ctx.init(null, new TrustManager[]{tm}, null);  
        SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
        ClientConnectionManager ccm = this.getConnectionManager();  
        SchemeRegistry sr = ccm.getSchemeRegistry();  
        sr.register(new Scheme("https", 443, ssf));  
    }  
}

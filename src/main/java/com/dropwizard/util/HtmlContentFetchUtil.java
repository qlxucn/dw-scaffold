package com.dropwizard.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by aaron on 5/7/14.
 */
public class HtmlContentFetchUtil {
    private static Logger LOGGER = Logger.getLogger(HtmlContentFetchUtil.class);

    public static String getHtml(String url, CloseableHttpClient httpClient) {
        String html = null;
        CloseableHttpResponse response = null;
        try {
            long start = 0l;
            HttpGet request = new HttpGet(url);
            if( LOGGER.isDebugEnabled() ) {
                LOGGER.debug("Fetching HTML for URL [" + url + "]");
                start = System.currentTimeMillis();
            }
            response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if( LOGGER.isDebugEnabled() ) {
                LOGGER.debug("Received Status [" + statusCode + "] for URL [" + url + "], took [" + (System.currentTimeMillis()-start) + "] ms");
            }
            if( statusCode == 200 ) {
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader
                            (new InputStreamReader(response.getEntity().getContent()));
                    html = IOUtils.toString(rd);
                } finally {
                    IOUtils.closeQuietly(rd);
                }
            } else {
                LOGGER.info("Got response [" + statusCode + " while trying to get the HTML from URL [" + url + "]");
            }
        } catch(Exception ex) {
            LOGGER.error(ex.getClass().getName() + " - " + ex.getMessage() + " - for URL [" + url + "]", ex);
        } finally {
            if( response != null ) {
                EntityUtils.consumeQuietly(response.getEntity());
                HttpClientUtils.closeQuietly(response);
            }
        }
        return html;
    }

    public static String getHtml(String url)
    {
        StringBuffer buf = new StringBuffer();
        try{
            URL myUrl = new URL(url);
            URLConnection conn = myUrl.openConnection();
            DataInputStream in = new DataInputStream ( conn.getInputStream (  )  ) ;
            BufferedReader d = new BufferedReader(new InputStreamReader(in));
            while(d.ready())
            {
                buf.append(d.readLine());
            }
        }
        catch(Exception ex )
        {
            LOGGER.error(ex.getClass().getName() + " - " + ex.getMessage() + " - for URL [" + url + "]", ex);
        }
        return buf.toString();

    }

    public static Set<String> extractImages(String content)
    {
        Set<String> imageUrls = new HashSet<String>();
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(content, "UTF-8");
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String imgUrl = img.attr("src");
            imageUrls.add(imgUrl);
        }
        return imageUrls;
    }

    public static String getRawText(String content){
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(content, "UTF-8");
        return doc.text();
    }

    public static String replaceImageUrls(String content, Map<String, String> imageUrlMap)
    {
        StringBuilder contentsb = new StringBuilder();
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(content, "UTF-8");
        List<Element> filteredElements = new ArrayList<Element>();
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String oldImgUrl = img.attr("src");
            String newImgUrl = imageUrlMap.get(oldImgUrl);
            if(newImgUrl == null)
            {
                filteredElements.add(img);
            }
            else
            {
                img.attr("src", newImgUrl);
            }
        }
        for(Element e: filteredElements)
        {
            e.remove();
        }
        for (org.jsoup.nodes.Node node : doc.getElementsByTag("body").first().childNodes()) {
            contentsb.append(node.toString());
        }
        return contentsb.toString();
    }


}

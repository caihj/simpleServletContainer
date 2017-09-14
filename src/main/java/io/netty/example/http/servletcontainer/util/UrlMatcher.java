package io.netty.example.http.servletcontainer.util;

/**
 * Url匹配器
 */
public interface UrlMatcher {
    boolean match(String url,String urlToMatch);
}

package io.netty.example.http.servletcontainer.util;

public class SimpleUrlMatcher implements UrlMatcher {
    @Override
    public boolean match(String url, String urlToMatch) {
        return url.equalsIgnoreCase(urlToMatch);
    }
}

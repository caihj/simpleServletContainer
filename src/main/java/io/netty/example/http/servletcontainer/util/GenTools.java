package io.netty.example.http.servletcontainer.util;

import java.util.Enumeration;
import java.util.Iterator;

public class GenTools {

    public static Enumeration iteratorToEnumeration(final Iterator  iterable){
        return new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return iterable.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterable.next();
            }
        };
    }
}

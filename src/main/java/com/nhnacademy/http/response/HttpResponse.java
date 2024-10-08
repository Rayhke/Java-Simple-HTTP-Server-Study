package com.nhnacademy.http.response;

import java.io.IOException;
import java.io.PrintWriter;

public interface HttpResponse {

    /**
     * * Returns a <code>PrintWriter</code> object that can send character text to the client. The
     * <code>PrintWriter</code> uses the character encoding returned by {@link #getCharacterEncoding}. If the response's
     * character encoding has not been specified as described in <code>getCharacterEncoding</code> (i.e., the method
     * just returns the default value <code>ISO-8859-1</code>), <code>getWriter</code> updates it to
     * <code>ISO-8859-1</code>.
     * */
    PrintWriter getWriter() throws IOException;

    String getCharacterEncoding();

    /**
     * charset – a String specifying only the character set defined by IANA Character Sets
     * */
    void setCharacterEncoding(String charset);
}

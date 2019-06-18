package org.apache.jmeter.gui.action;

import org.apache.jmeter.protocol.http.curl.BasicCurlParser;
import org.apache.jmeter.protocol.http.gui.action.ParseCurlCommandAction;
import org.junit.Assert;
import org.junit.Test;

public class ParseCurlCommandActionTest {
    @Test
    public void testCreateCommentText1() {
        ParseCurlCommandAction p = new ParseCurlCommandAction();
        String cmdLine = "curl 'http://jmeter.apache.org/' --max-redirs 'b'";
        BasicCurlParser basicCurlParser = new BasicCurlParser();
        BasicCurlParser.Request request = basicCurlParser.parse(cmdLine);
        String comment = p.createCommentText(request);
        Assert.assertEquals("Http request should can be set the right comment",comment,
                "--max-redirs is in 'httpsampler.max_redirects(1062 line)' configure in jmeter.properties ");
        cmdLine = "curl 'http://jmeter.apache.org/' --include --keepalive-time '20'";
        basicCurlParser = new BasicCurlParser();
        request = basicCurlParser.parse(cmdLine);
        comment = p.createCommentText(request);
        Assert.assertEquals("Http request should can be set the right comment",comment.trim(),
                "--include --keepalive-time ignoring;");
        cmdLine = "curl 'http://jmeter.apache.org/' -x 'https://aa:bb@example.com:8042' --proxy-ntlm";
        basicCurlParser = new BasicCurlParser();
        request = basicCurlParser.parse(cmdLine);
        comment = p.createCommentText(request);
        Assert.assertEquals("Http request should can be set the right comment",comment,
                "--proxy-ntlm not supported; ");
        cmdLine = "curl 'http://jmeter.apache.org/' --include --keepalive-time '20'";
        basicCurlParser = new BasicCurlParser();
        request = basicCurlParser.parse(cmdLine);
        comment = p.createCommentText(request);
        Assert.assertEquals("Http request should can be set the right comment",comment.trim(),
                "--include --keepalive-time ignoring;");
        cmdLine = "curl 'http://jmeter.apache.org/'";
        basicCurlParser = new BasicCurlParser();
        request = basicCurlParser.parse(cmdLine);
        comment = p.createCommentText(request);
        Assert.assertTrue("Http request should can't be set the right comment",comment.isEmpty());
    }

}

package gnu.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class LibraryLoaderTest {

    @Test
    public void test1() throws Exception {

    }

    @Test
    public void test2() throws Exception {
        Pattern pattern = Pattern.compile("^(.+)(\\.[^\\.]+)$");
        Matcher matcher = pattern.matcher("hello.hello.world.txt");
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(".txt", matcher.group(2));
        Assert.assertEquals("hello.hello.world", matcher.group(1));
    }

}

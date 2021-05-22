import org.glassfish.grizzly.http.util.ByteChunk;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ByteChunkAppendTest {

    /**
     * this test provokes a NullPointerException instead of an IOException
     * as ByteChunk.out is null by default and not initialized and limit == bytesRead
     *
     * analog to what happens in FormAuthenticator.saveRequest between line 676 and line 687 here
     * https://github.com/apache/tomcat/blob/master/java/org/apache/catalina/authenticator/FormAuthenticator.java#L676
     * or between line 561 and line 570 here
     * https://github.com/payara/Payara/blob/master/appserver/web/web-core/src/main/java/org/apache/catalina/authenticator/FormAuthenticator.java#L561
     * if maxSavePostSize is 4096 or smaller and bytesRead has the same value as maxSavePostSize
     * => the request body must contain at least maxSavePostSize bytes
     */
    @Test
    public void testAppendNPEOutNull() throws IOException {
        int maxSavePostSize = 4096;

        ByteChunk bc = new ByteChunk();
        bc.setLimit(maxSavePostSize);

        byte[] buffer = new byte[4096];
        int bytesRead;

        // InputStream is = request.getInputStream();
        final InputStream is = new ByteArrayInputStream(getRandomBytes(maxSavePostSize));

        while ((bytesRead = is.read(buffer)) >= 0) {
            bc.append(buffer, 0, bytesRead);
        }
    }

    /**
     * minimal example on how to provoke the NPE:
     * for every value of limit where limit == len and ByteChunk.out is null (as per default) and ByteChunk.optimizedWrite == true (as per default)
     */
    @Test
    public void testAppendMinimalNPE() throws IOException {
        int sameValueForLimitAndLen = new Random().nextInt(8192);

        ByteChunk bc = new ByteChunk();
        bc.setLimit(sameValueForLimitAndLen);

        bc.append(getRandomBytes(sameValueForLimitAndLen), 0, sameValueForLimitAndLen);
    }

    /**
     * this test fails with an IOException as the maxSavePostSize is larger than the internal buffer (5124 vs. 4096 bytes)
     * which is fine as IOException is a declared exception which should be handled higher up in the call tree
     */
    @Test()
    public void testAppendNoNPELimitLargerThanLen() throws IOException {
        int sameValueForLimitAndLen = new Random().nextInt(8192);

        ByteChunk bc = new ByteChunk();
        bc.setLimit(sameValueForLimitAndLen);

        bc.append(getRandomBytes(sameValueForLimitAndLen - 1), 0, sameValueForLimitAndLen - 1);
    }

    /**
     * this test fails with an IOException as the maxSavePostSize is larger than the internal buffer (5124 vs. 4096 bytes)
     * which is fine as IOException is a declared exception which should be handled higher up in the call tree
     */
    @Test(expected = IOException.class)
    public void testAppendIOExceptionLenLargerThanLimit() throws IOException {
        int sameValueForLimitAndLen = new Random().nextInt(8192);

        ByteChunk bc = new ByteChunk();
        bc.setLimit(sameValueForLimitAndLen);

        bc.append(getRandomBytes(sameValueForLimitAndLen + 1), 0, sameValueForLimitAndLen + 1);
    }

    /**
     * this test passes as optimizedWrite is set to false
     */
    @Test
    public void testAppendNoNPEOptimizedWriteFalse() throws IOException {
        int sameValueForLimitAndLen = new Random().nextInt(8192);

        ByteChunk bc = new ByteChunk();
        bc.setLimit(sameValueForLimitAndLen);
        bc.setOptimizedWrite(false);

        bc.append(getRandomBytes(sameValueForLimitAndLen), 0, sameValueForLimitAndLen);
    }

    private static byte[] getRandomBytes(int targetStringLength) {
        byte[] array = new byte[targetStringLength];
        new Random().nextBytes(array);
        return array;
    }

}

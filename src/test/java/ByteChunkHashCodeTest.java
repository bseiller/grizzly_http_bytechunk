import org.glassfish.grizzly.http.util.ByteChunk;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;

public class ByteChunkHashCodeTest {

    @Test
    public void testHashCodeNPECharsetNull() {
        final ByteChunk bc = new ByteChunk();
        bc.hashCode();
    }

    @Test
    public void testHashCodeNPEInNull() {
        final ByteChunk bc = new ByteChunk();
        bc.setCharset(Charset.defaultCharset());
        bc.hashCode();
    }

    @Test
    public void testHashCodeNPEOutNull() {
        final ByteChunk bc = new ByteChunk();
        bc.setCharset(Charset.defaultCharset());
        bc.setByteInputChannel(new ByteChunk.ByteInputChannel() {
            public int realReadBytes(byte[] cbuf, int off, int len) throws IOException {
                return 0;
            }
        });
        bc.hashCode();
    }
}

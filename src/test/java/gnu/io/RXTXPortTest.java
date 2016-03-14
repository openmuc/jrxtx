package gnu.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "gnu.io.RXTXPort" })
@PrepareForTest(RXTXPort.class)
public class RXTXPortTest {

    @Test
    public void test_inputStream() throws Exception {

        RXTXPort rxtxPort = PowerMockito.mock(RXTXPort.class);

    }

}

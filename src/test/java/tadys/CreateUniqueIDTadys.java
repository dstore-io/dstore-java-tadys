package tadys;

import io.dstore.engine.EngineGrpc;
import io.dstore.helper.ValuesHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 11.10.16.
 */
public class CreateUniqueIDTadys {

    @Test
    public void testGetUniqueID() throws Exception {

        /* Creating a connection (actual it is a "EngineBlockingStub") */
        EngineGrpc.EngineBlockingStub stub = EngineGrpc.newBlockingStub(ConnectionHolder.getPublicChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);

        /* To creating a "UniqueID" we commit a User-Agent String. If the UserAgent is a known Bot-Agent you will just get a defaultUniqueID due to performance reasons */
        String uniqueID = stub.createUniqueID(ValuesHelper.value("A User-Agent")).getValue();

        Assert.assertNotNull(uniqueID);
        Assert.assertEquals(36, uniqueID.trim().length());

    }

}

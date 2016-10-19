package tadys;

import io.dstore.elastic.ElasticServiceGrpc;
import io.dstore.engine.EngineGrpc;
import io.dstore.engine.procedures.EngineProcGrpc;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 10.10.16.
 */
public class ConnectionTadys {

    /*
    *
    * Before executing these test-cases, please configure your dstore.io PORT and URL at
    *
    *  io.dstore.tadys.TadyHelper.URL AND io.dstore.tadys.TadyHelper.PORT
    *
    *  @see io.dstore.tadys.TadyHelper
    *  @see http://dev.dstore.io/wiki/grpcservice
    *
    * */

    @Test
    public void testProcedureConnection() throws Exception {
        EngineProcGrpc.EngineProcBlockingStub stub = EngineProcGrpc.newBlockingStub(ConnectionHolder.getAdminChannel())
                .withDeadlineAfter(200, TimeUnit.SECONDS);
        Assert.assertNotNull(stub);

    }

    @Test
    public void testEngineConnection() throws Exception {
        EngineGrpc.EngineBlockingStub stub = EngineGrpc.newBlockingStub(ConnectionHolder.getPublicChannel())
                .withDeadlineAfter(200, TimeUnit.SECONDS);
        Assert.assertNotNull(stub);
    }

    @Test
    public void testElasticServiceConnection() throws Exception {
        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ElasticServiceGrpc.newBlockingStub(
                ConnectionHolder.getPublicChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);
        Assert.assertNotNull(stub);
    }

}

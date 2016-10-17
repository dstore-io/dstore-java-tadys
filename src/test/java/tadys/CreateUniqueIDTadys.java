package tadys;

import io.dstore.engine.EngineGrpc;
import io.dstore.helper.DstoreCredentials;
import io.dstore.helper.ValuesHelper;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.internal.ManagedChannelImpl;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 11.10.16.
 */
public class CreateUniqueIDTadys {

    @Test
    public void testGetUniqueID() throws Exception {

        /* Creating a connection (actual it is a "EngineBlockingStub") */

        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials("publicuser", "public"), Executors.newSingleThreadExecutor()));
        EngineGrpc.EngineBlockingStub stub = EngineGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);

        /* To creating a "UniqueID" we commit a User-Agent String. If the UserAgent is a known Bot-Agent you will just get a defaultUniqueID due to performance reasons */
        String uniqueID = stub.createUniqueID(ValuesHelper.value("A User-Agent")).getValue();

        Assert.assertNotNull(uniqueID);
        Assert.assertEquals(36, uniqueID.trim().length());

    }

}

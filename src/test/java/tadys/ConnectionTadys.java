package tadys;

import io.dstore.elastic.ElasticServiceGrpc;
import io.dstore.engine.EngineGrpc;
import io.dstore.engine.procedures.EngineProcGrpc;
import io.dstore.helper.DstoreCredentials;
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
        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials("publicuser", "public"), Executors.newSingleThreadExecutor()));


        EngineProcGrpc.EngineProcBlockingStub stub = EngineProcGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);

        Assert.assertNotNull(stub);

    }

    @Test
    public void testEngineConnection() throws Exception {
        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials("publicuser", "public"), Executors.newSingleThreadExecutor()));


        EngineGrpc.EngineBlockingStub stub = EngineGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);

        Assert.assertNotNull(stub);
    }

    @Test
    public void testElasticServiceConnection() throws Exception {
        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials("publicuser", "public"), Executors.newSingleThreadExecutor()));


        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ElasticServiceGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);

        Assert.assertNotNull(stub);
    }




}

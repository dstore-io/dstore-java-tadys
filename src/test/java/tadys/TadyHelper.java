package tadys;

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

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 10.10.16.
 */
public class TadyHelper {

    /* Please configure your dstore.io port and url:  */

    public final static int PORT = 13921;
    public final static String URL = "try.dstore.io";
    public final static String USERNAME_AD_CONNECT = "dbap_dev";
    public final static String PASSWORD_AD_CONNECT = "wmxUUTkV87";


    public static EngineProcGrpc.EngineProcBlockingStub getProcedureExecutionBlockingStubForAdConnect() throws Exception {
        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials(USERNAME_AD_CONNECT, PASSWORD_AD_CONNECT), Executors.newSingleThreadExecutor()));

        return EngineProcGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);
    }


}

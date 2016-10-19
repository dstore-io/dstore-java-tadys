package tadys;

import io.dstore.elastic.ElasticServiceGrpc;
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
public class ConnectionHolder {

    /* Please configure your dstore.io port and url:  */

    public final static int PORT = 13921;
    public final static String URL = "try.dstore.io";
    public final static String USERNAME_AD_CONNECT = "dbap_dev";
    public final static String PASSWORD_AD_CONNECT = "wmxUUTkV87";

    private static ManagedChannelImpl channelInstance = null;
    private static Channel adChannelInstance = null;

    public static ManagedChannelImpl getChannel() throws Exception{
        if ( channelInstance == null ){
            channelInstance = NettyChannelBuilder.forAddress(ConnectionHolder.URL, ConnectionHolder.PORT)
                    .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                    .negotiationType(NegotiationType.TLS).build();
        }

        return channelInstance;
    }

    public static Channel getAdminChannel() throws Exception{
        if (adChannelInstance == null ){
            adChannelInstance =  ClientInterceptors.intercept(getChannel(),
                    new ClientAuthInterceptor(new DstoreCredentials(USERNAME_AD_CONNECT, PASSWORD_AD_CONNECT),
                            Executors.newSingleThreadExecutor()));
        }
        return adChannelInstance;
    }

    public static Channel getPublicChannel() throws Exception{
        return getChannel();
    }

    public static EngineProcGrpc.EngineProcBlockingStub getAdminEngineProcStub() throws Exception {
        return EngineProcGrpc.newBlockingStub(getAdminChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);
    }

    public static EngineProcGrpc.EngineProcBlockingStub getPublicEngineProcStub() throws Exception {
        return EngineProcGrpc.newBlockingStub(getPublicChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);
    }

    public static ElasticServiceGrpc.ElasticServiceBlockingStub getElasticServiceStub () throws Exception {
        return  ElasticServiceGrpc.newBlockingStub(getPublicChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);

    }

}

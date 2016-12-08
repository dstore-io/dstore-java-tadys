package tadys;

import io.dstore.elastic.ElasticServiceGrpc;
import io.dstore.engine.procedures.EngineProcGrpc;
import io.dstore.helper.DstoreCredentials;
import io.grpc.internal.ManagedChannelImpl;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 10.10.16.
 */
public class ConnectionHolder {

    /* Please configure your dstore.io port and url:  */

    public final static int PORT = 13896;
    public final static String URL = "try.dstore.io";
    public final static String USERNAME_AD_CONNECT = "dbap_dev";
    public final static String PASSWORD_AD_CONNECT = "wmxUUTkV87";

    private static ManagedChannelImpl channelInstance = null;

    public static ManagedChannelImpl getChannel() throws Exception {
        if (channelInstance == null) {
            channelInstance = NettyChannelBuilder.forAddress(ConnectionHolder.URL, ConnectionHolder.PORT)
                    .sslContext(GrpcSslContexts.forClient().trustManager(ClassLoader.class.getResourceAsStream("/dstore-try-ca.pem")).build())
                    // trust every certificate via : trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                    .negotiationType(NegotiationType.TLS).build();
        }

        return channelInstance;
    }

    public static EngineProcGrpc.EngineProcBlockingStub getAdminEngineProcStub() throws Exception {
        return EngineProcGrpc.newBlockingStub(getChannel())
                .withCallCredentials(new DstoreCredentials(USERNAME_AD_CONNECT, PASSWORD_AD_CONNECT))
                .withDeadlineAfter(200, TimeUnit.SECONDS);
    }

    public static EngineProcGrpc.EngineProcBlockingStub getPublicEngineProcStub() throws Exception {
        return EngineProcGrpc.newBlockingStub(getChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);
    }

    public static ElasticServiceGrpc.ElasticServiceBlockingStub getElasticServiceStub() throws Exception {
        return ElasticServiceGrpc.newBlockingStub(getChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);

    }

}

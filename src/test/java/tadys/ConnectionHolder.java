package tadys;

import io.dstore.elastic.ElasticGrpc;
import io.dstore.engine.procedures.EngineProcGrpc;
import io.dstore.helper.ChannelHelper;
import io.dstore.helper.DstoreCredentials;
import io.grpc.internal.ManagedChannelImpl;

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
        if (channelInstance == null)
            channelInstance = ChannelHelper.getSslChannel(ConnectionHolder.URL, ConnectionHolder.PORT, "/dstore-try-ca.pem");

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

    public static ElasticGrpc.ElasticBlockingStub getElasticServiceStub() throws Exception {
        return ElasticGrpc.newBlockingStub(getChannel()).withDeadlineAfter(200, TimeUnit.SECONDS);

    }

}

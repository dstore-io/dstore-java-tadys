package tadys;

import io.dstore.elastic.ElasticServiceGrpc;
import io.dstore.elastic.item.FacetedNavigation;
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

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hwies on 11.10.16.
 */
public class FacetedNavigationTadys {

    /*
    * TODO: Link !
    * @see http://dev.dstore.io:8080/wiki/facetedNavigation
    *
    *
    *
    * */

    @Test
    public void getAllProductsTady() throws Exception{

        /* First we need a "BlockinStub" again (a "ElasticServiceGrpc.ElasticServiceBlockingStub" to be accurate) */
        ManagedChannelImpl channel = NettyChannelBuilder.forAddress(TadyHelper.URL, TadyHelper.PORT)
                .sslContext(GrpcSslContexts.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build())
                .negotiationType(NegotiationType.TLS).build();

        Channel wrappedChannel = ClientInterceptors.intercept(channel, new ClientAuthInterceptor(new DstoreCredentials("publicuser", "public"), Executors.newSingleThreadExecutor()));
        ElasticServiceGrpc.ElasticServiceBlockingStub stub =  ElasticServiceGrpc.newBlockingStub(wrappedChannel).withDeadlineAfter(200, TimeUnit.SECONDS);

        /* Now we can define the Request. First a real simple one... */
        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .build();

        /* Executing the request and recieving the responses */
        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* We should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to find out how many Items we have */
        int itemCount = -1;
        int totalItemCount = -1;
        while( responses.hasNext() ){
            FacetedNavigation.Response response = responses.next();
            if (response.getItemCount() >  0 && response.getTotalHits() > 0 ){
                itemCount = response.getItemCount();

                /* TotalHits: (Number of all products and their variants)*/
                totalItemCount = response.getTotalHits();
            }
        }

        // should be 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemCount);

        // Should be more then 50000 (yes, we have more then "just a few" test items !)
        Assert.assertTrue( totalItemCount > 50000 );

    }

    /* Base-Query */

    /* Post-Query / Facetten */

    /* Item-Daten "sammeln" */


}

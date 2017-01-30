package tadys;

import io.dstore.elastic.BoolQuery;
import io.dstore.elastic.ElasticGrpc;
import io.dstore.elastic.Query;
import io.dstore.elastic.item.ItemGet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by hwies on 11.10.16.
 */
public class ItemGetTadys {

    /*
    * TODO: Link !
    * @see http://dev.dstore.io:8080/wiki/facetedNavigation
    *
    *
    *
    * */

    @Test
    public void getAllProductsTady() throws Exception {

        /* First we need a "BlockinStub" again (a "ElasticServiceGrpc.ElasticServiceBlockingStub" to be accurate) */
        ElasticGrpc.ElasticBlockingStub stub = ConnectionHolder.getElasticServiceStub();

        /* Now we can define the Request. First a real simple one... */
        ItemGet.Request request = ItemGet.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .build();

        /* Executing the request and recieving the responses */
        Iterator<ItemGet.Response> responses = stub.itemGet(request);

        /* We should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to find out how many Items we have */
        int itemCount = -1;
        int totalItemCount = -1;
        while (responses.hasNext()) {
            ItemGet.Response response = responses.next();
            if (response.getItemCount() > 0 && response.getTotalHits() > 0) {
                itemCount = response.getItemCount();

                /* TotalHits: (Number of all products and their variants)*/
                totalItemCount = response.getTotalHits();
            }
        }

        // should be 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemCount);

        // Should be more then 50000 (yes, we have more then "just a few" test items !)
        Assert.assertTrue(totalItemCount > 50000);

    }

    @Test
    public void getProductsforBaseQueryTady() throws Exception {

        ElasticGrpc.ElasticBlockingStub stub = ConnectionHolder.getElasticServiceStub();

        Query.Terms typeFilter = Query.Terms.newBuilder()
                .setFieldName("87")
                .addValue("boardgame")
                .build();

         /* Another Request. This time we set a base query to filter the results by their "type" */
        ItemGet.Request request = ItemGet.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .setBaseQuery(
                        BoolQuery.newBuilder()
                                .addFilter(Query.newBuilder().setTermsQuery(typeFilter))
                )
                .build();

        /* Executing the request and recieving the responses */
        Iterator<ItemGet.Response> responses = stub.itemGet(request);

        /* Again we should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to find out how many Items we now have */
        int itemCount = -1;
        int totalItemCount = -1;
        while (responses.hasNext()) {
            ItemGet.Response response = responses.next();
            if (response.getItemCount() > 0 && response.getTotalHits() > 0) {
                itemCount = response.getItemCount();

                /* TotalHits: (Number of all products and their variants)*/
                totalItemCount = response.getTotalHits();
            }
        }

        // should be still 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemCount);

        // Should be less then 50000 item now, because a whole lot of them are not of "type=boardgame" (see base query above)
        Assert.assertTrue(totalItemCount < 50000);

        // But there should be still at least more then 30000
        Assert.assertTrue(totalItemCount > 30000);

    }

    /* Post-Query / Facetten */

    /* Item-Daten "sammeln" */


}

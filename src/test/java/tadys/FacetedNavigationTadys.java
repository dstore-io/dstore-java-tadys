package tadys;

import io.dstore.elastic.Elastic;
import io.dstore.elastic.ElasticServiceGrpc;
import io.dstore.elastic.item.ElasticItem;
import io.dstore.elastic.item.ElasticNode;
import io.dstore.elastic.item.FacetedNavigation;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
        ElasticServiceGrpc.ElasticServiceBlockingStub stub =  ConnectionHolder.getElasticServiceStub();

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

    @Test
    public void getProductsforBaseQueryTady() throws Exception{

        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ConnectionHolder.getElasticServiceStub();

        Elastic.Query.Terms typeFilter = Elastic.Query.Terms.newBuilder()
                .setFieldName("87")
                .addValue("boardgame")
                .build();

         /* Another Request. This time we set a base query to filter the results by their "type" */
        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .setBaseQuery(
                        Elastic.BoolQuery.newBuilder()
                                .addFilter(Elastic.Query.newBuilder().setTermsQuery(typeFilter))
                )
                .build();

        /* Executing the request and recieving the responses */
        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* Again we should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to find out how many Items we now have */
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

        // should be still 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemCount);

        // Should be less then 50000 item now, because a whole lot of them are not of "type=boardgame" (see base query above)
        Assert.assertTrue( totalItemCount < 50000 );

        // But there should be still at least more then 30000
        Assert.assertTrue( totalItemCount > 30000 );

    }

    @Test
    public void getProductsforFacetFilterTady() throws Exception{

        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ConnectionHolder.getElasticServiceStub();

        Elastic.Query.Terms typeFilter = Elastic.Query.Terms.newBuilder()
                .setFieldName("87")
                .addValue("boardgame")
                .build();

         /* Another Request. This time we set a base query to filter the results by their "type" */
        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .setBaseQuery(
                        Elastic.BoolQuery.newBuilder()
                                .addFilter(Elastic.Query.newBuilder().setTermsQuery(typeFilter))
                )
                .build();

        /* Executing the request and recieving the responses */
        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* Again we should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to find out how many Items we now have */
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

        // should be still 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemCount);

        // Should be less then 50000 item now, because a whole lot of them are not of "type=boardgame" (see base query above)
        Assert.assertTrue( totalItemCount < 50000 );

        // But there should be still at least more then 30000
        Assert.assertTrue( totalItemCount > 30000 );

    }


    @Test
    public void getFacetsTady() throws Exception{

        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ConnectionHolder.getElasticServiceStub();

         /* Another Request. This time we adding a facet but with no filter on it. So in the response we get all
         existing values for the field for use in a guided navigation
         */
        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                /* The facet for the "Publisher" (CharacteristicID "79") */
                .addFacet(FacetedNavigation.Request.Facet.newBuilder().setFieldName("variants.79"))
                .build();

        /* Executing the request and recieving the responses */
        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* Again we should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to get all the values for the facet */
        List<ElasticItem.Facet.FacetValue> facetValueList = new ArrayList<ElasticItem.Facet.FacetValue>();
        while( responses.hasNext() ){
            FacetedNavigation.Response response = responses.next();
            if ( response.getFacetList() != null && response.getFacetList().size() > 0 ){
                facetValueList = response.getFacetList().get(0).getFacetValueList();
            }

        }

        // there should be more then 10000 facet values
        Assert.assertTrue(facetValueList.size()>10000);

        // and there should be an entry for "Fantasy Flight Games"
        ElasticItem.Facet.FacetValue ffgValue = null;
        for (ElasticItem.Facet.FacetValue fv : facetValueList ){
            if (ValuesHelper.convertToObject(fv.getValue()).equals("Fantasy Flight Games")){
                ffgValue = fv;
                break;
            }
        }
        Assert.assertNotNull(ffgValue);

        /* And There should be at least 900 items for this value */
        Assert.assertTrue( ffgValue.getMatchingItemCount() > 900 );


    }

    @Test
    public void getFacetsWithFilterTady() throws Exception{

        ElasticServiceGrpc.ElasticServiceBlockingStub stub = ConnectionHolder.getElasticServiceStub();

        Elastic.Query.Terms languageFilter = Elastic.Query.Terms.newBuilder()
                .setFieldName("variants.86")
                .addValue("English")
                .build();

        /* We create a postQueryFilter to simulate a user has selected the facet "Fantasy Flight Games" at the
           "Publishers" facet.
        */
        Elastic.BoolQuery.Builder postQueryBuilder = Elastic.BoolQuery.newBuilder();
        Elastic.Query.Terms.Builder facetFilterBuilder = Elastic.Query.Terms.newBuilder().setFieldName("variants.79").addValue("Fantasy Flight Games");
        postQueryBuilder.addFilter(Elastic.Query.newBuilder().setTermsQuery(facetFilterBuilder.build()));

         /* Another Request. This time we adding the facet and setting a post query with a filter for the facet */
        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setBaseQuery(
                        Elastic.BoolQuery.newBuilder()
                                .addFilter(Elastic.Query.newBuilder().setTermsQuery(languageFilter)))
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                /* The facet for the "Publisher" (CharacteristicID "79") */
                .addFacet(FacetedNavigation.Request.Facet.newBuilder().setFieldName("variants.79"))
                .setPostQuery(postQueryBuilder.build())
                .build();

        /* Executing the request and recieving the responses */
        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* Again we should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Lets try to get all the values for the facet */
        List<ElasticItem.Facet.FacetValue> facetValueList = new ArrayList<ElasticItem.Facet.FacetValue>();
        String firstVariantPublisher = null;
        while( responses.hasNext() ){
            FacetedNavigation.Response response = responses.next();
            if ( response.getFacetList() != null && response.getFacetList().size() > 0 ){
                facetValueList = response.getFacetList().get(0).getFacetValueList();
            }

            if ( response.getItemList() != null && response.getItemList().size() > 0 ){
                firstVariantPublisher = (String) ValuesHelper.convertToObject(response.getItemList().get(0).
                        getVariantNode(0).getFieldsMap().get("79").getValue(0));
            }
        }

        // there still should be an entry for "Fantasy Flight Games"
        ElasticItem.Facet.FacetValue ffgValue = null;
        for (ElasticItem.Facet.FacetValue fv : facetValueList ){
            if (ValuesHelper.convertToObject(fv.getValue()).equals("Fantasy Flight Games")){
                ffgValue = fv;
                break;
            }
        }
        Assert.assertNotNull(ffgValue);

        /* and this time value should be active now */
        Assert.assertTrue( ffgValue.getActive());

        /* In addition all items in the response should have at least one variant which is from the Publisher
        "Fantasy Flight Games"
        */
        Assert.assertEquals( "Fantasy Flight Games", firstVariantPublisher);
    }

    @Test
    public void aggregateItemDataTady() throws Exception{

        ElasticServiceGrpc.ElasticServiceBlockingStub stub =  ConnectionHolder.getElasticServiceStub();

        FacetedNavigation.Request request = FacetedNavigation.Request.newBuilder()
                /* We want 50 Items per "page" */
                .setSize(50)
                /* and we want to start with page 1 (or Item 0) */
                .setFrom(0)
                .build();


        Iterator<FacetedNavigation.Response> responses = stub.facetedNavigation(request);

        /* We should have an iterator with all responses now */
        Assert.assertNotNull(responses);

        /* and there should be at least one iterable response in responses */
        Assert.assertTrue(responses.hasNext());

        /* Now we create a List of Items from the result*/
        List<ElasticItem.Item> itemList = new ArrayList<ElasticItem.Item>();

        while( responses.hasNext() ){
            FacetedNavigation.Response response = responses.next();
            if (response.getItemList() != null && response.getItemList().size() > 0 ){
                itemList.addAll(response.getItemList() );
            }
        }

        // should be 50, because of "setSize(50)" above
        Assert.assertEquals(50, itemList.size());

        /* The products name, for example is the value to characteristic id 8: */
        String descriptionValue = (String) ValuesHelper.convertToObject(itemList.get(5).getNode().getFieldsMap().get("8").getValue(0));
        Assert.assertEquals("Animorphs", descriptionValue);

        /* another field is for example the product image url: */
        String productImageURL = (String) ValuesHelper.convertToObject(itemList.get(5).getNode().getFieldsMap().get("63").getValue(0));
        Assert.assertEquals("http://cf.geekdo-images.com/images/pic190852_t.jpg", productImageURL);

        /* Now we want to get a fields value from a variant */
        String firstVariantPublisher = (String) ValuesHelper.convertToObject(itemList.get(5).getVariantNode(0).getFieldsMap().get("78").getValue(0));
        Assert.assertEquals("Decipher Cassie & Jake edition", firstVariantPublisher);

        /* And now the value of the same field but from another variant */
        String secondVariantPublisher = (String) ValuesHelper.convertToObject(itemList.get(5).getVariantNode(1).getFieldsMap().get("78").getValue(0));
        Assert.assertEquals("Decipher Rachel & Marco edition", secondVariantPublisher);

    }


}

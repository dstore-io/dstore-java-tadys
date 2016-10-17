package tadys;

import io.dstore.engine.procedures.MiDatatypeTestAd;
import io.dstore.helper.ValuesHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by hwies on 10.10.16.
 */
public class ExecuteProcedureTadys {

    // TODO: Link!
    // @see http://dev.dstore.io:8080/wiki/mi_DatatypeTest_Ad

    @Test
    public void testExecuteProcedureWithoutParameter()throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad" without setting any parameters */

        Iterator<MiDatatypeTestAd.Response> resultIterator = TadyHelper.getProcedureExecutionBlockingStubForAdConnect().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .build()
        );

        boolean resultWithResultRowsExists = false;

        /* Iterating over all result object, trying to find one which has rows - which represents (at least a part of) the actual result */
        while(resultIterator.hasNext()){
            if ( resultIterator.next().getRowCount() >  0){
                resultWithResultRowsExists = true;
            }
        }

        Assert.assertTrue(resultWithResultRowsExists);

    }

    @Test
    public void testExecuteProcedureWithParameter()throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad",  setting a parameter which should cause that we don't get a result set  */

        Iterator<MiDatatypeTestAd.Response> resultIterator = TadyHelper.getProcedureExecutionBlockingStubForAdConnect().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .setGetResultSet(ValuesHelper.value(false))
                        .build()
        );

        boolean resultWithResultRowsExists = false;

        /* Iterating over all result object, trying to find one which has rows - which shouldn't exists because of "GetResultSet = false" */
        while(resultIterator.hasNext()){
            if ( resultIterator.next().getRowCount() >  0){
                resultWithResultRowsExists = true;
            }
        }

        Assert.assertFalse(resultWithResultRowsExists);

    }

    @Test
    public void testExecuteProcedureGetOutputParameters()throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad",  setting a parameter which should cause that output parameters are set after execution  */


        Iterator<MiDatatypeTestAd.Response> resultIterator = TadyHelper.getProcedureExecutionBlockingStubForAdConnect().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .setGetResultSet(ValuesHelper.value(false))
                        .setSetOutputParams(ValuesHelper.value(true))
                        .build()
        );

        String testChar = null;

        /* Setting and checking output parameter value */
        while(resultIterator.hasNext()){

            MiDatatypeTestAd.Response result = resultIterator.next();

            if ( result.getTestChar().isInitialized() && result.getTestChar() != null ){
                testChar = result.getTestChar().getValue().trim();
            }
        }

        Assert.assertEquals("test char", testChar);

    }

    /* TODO:  Result Set verarbeiten */
    /* TODO: Messages */
}

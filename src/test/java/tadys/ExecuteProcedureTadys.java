package tadys;

import io.dstore.engine.ProcedureMessage;
import io.dstore.engine.procedures.MiDatatypeTestAd;
import io.dstore.engine.procedures.MiGetUnits;
import io.dstore.helper.DstoreMetadata;
import io.dstore.helper.ValuesHelper;
import io.grpc.StatusRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hwies on 10.10.16.
 */
public class ExecuteProcedureTadys {

    // TODO: Link!
    // @see http://dev.dstore.io:8080/wiki/mi_DatatypeTest_Ad

    @Test
    public void testExecuteProcedureWithoutParameter() throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad" without setting any parameters */

        Iterator<MiDatatypeTestAd.Response> resultIterator = ConnectionHolder.getAdminEngineProcStub().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .build()
        );

        boolean resultWithResultRowsExists = false;

        /* Iterating over all result object, trying to find one which has rows - which represents (at least a part of) the actual result */
        while (resultIterator.hasNext()) {
            if (resultIterator.next().getRowCount() > 0) {
                resultWithResultRowsExists = true;
            }
        }

        Assert.assertTrue(resultWithResultRowsExists);

    }

    @Test
    public void testExecuteProcedureWithParameter() throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad",  setting a parameter which should cause that we don't get a result set  */

        Iterator<MiDatatypeTestAd.Response> resultIterator = ConnectionHolder.getAdminEngineProcStub().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .setGetResultSet(ValuesHelper.value(false))
                        .build()
        );

        boolean resultWithResultRowsExists = false;

        /* Iterating over all result object, trying to find one which has rows - which shouldn't exists because of "GetResultSet = false" */
        while (resultIterator.hasNext()) {
            if (resultIterator.next().getRowCount() > 0) {
                resultWithResultRowsExists = true;
            }
        }

        Assert.assertFalse(resultWithResultRowsExists);

    }

    @Test
    public void testExecuteProcedureGetOutputParameters() throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad",  setting a parameter which should cause that output parameters are set after execution  */


        Iterator<MiDatatypeTestAd.Response> resultIterator = ConnectionHolder.getAdminEngineProcStub().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .setGetResultSet(ValuesHelper.value(false))
                        .setSetOutputParams(ValuesHelper.value(true))
                        .build()
        );

        String testChar = null;

        /* Setting and checking output parameter value */
        while (resultIterator.hasNext()) {

            MiDatatypeTestAd.Response result = resultIterator.next();

            if (result.hasTestChar()) {
                testChar = result.getTestChar().getValue().trim();
            }
        }

        Assert.assertEquals("test char", testChar);

    }

    @Test
    public void testGetAndProcessResultSet() throws Exception {

        /* Executing the procedure "mi_DatatypeTest_Ad" */
        Iterator<MiDatatypeTestAd.Response> resultIterator = ConnectionHolder.getAdminEngineProcStub().miDatatypeTestAd(
                MiDatatypeTestAd.Parameters.newBuilder()
                        .build()
        );

        /* we store the result in a list (for rows) of maps (with some key/values pairs) - just to show how to process
        * the result sets
        * */
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        /* iterating over all results  */
        while (resultIterator.hasNext()) {

            MiDatatypeTestAd.Response result = resultIterator.next();

            if (result.getRowCount() > 0) {
                /* if the result has rows it contains (a part) of the procedures result set */
                for (MiDatatypeTestAd.Response.Row row : result.getRowList()) {
                    /* we save some values of the procedures result in a map (hard coded column name as key) */
                    Map<String, Object> rowMap = new HashMap<String, Object>();
                    rowMap.put("test_text", row.getTestText().getValue().trim());
                    rowMap.put("test_bit", row.getTestBit().getValue());
                    rowMap.put("test_integer", row.getTestInteger().getValue());
                    rowMap.put("test_datetime", ValuesHelper.toDate(row.getTestDatetime()));
                    rowMap.put("test_decimal", ValuesHelper.toBigDecimal(row.getTestDecimal()));
                    /* for each row we save a single map in a list */
                    resultList.add(rowMap);
                }
            }
        }

        /* we should get just one row */
        Assert.assertEquals(1, resultList.size());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:S");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        /* and we expect the following values */
        Assert.assertEquals("test text", resultList.get(0).get("test_text"));
        Assert.assertEquals(true, resultList.get(0).get("test_bit"));
        Assert.assertEquals(17, resultList.get(0).get("test_integer"));
        Assert.assertEquals(sdf.parse("23.05.2006 17:42:59:333"), resultList.get(0).get("test_datetime"));
        Assert.assertEquals(new BigDecimal("-17.425923"), resultList.get(0).get("test_decimal"));

    }

    @Test
    public void testGetAndProcessMessagesAfterProcedureError() throws Exception {

        /* Executing the procedure "mi_GetUnits" */
        Iterator<MiGetUnits.Response> resultIterator = ConnectionHolder.getAdminEngineProcStub().miGetUnits(
                MiGetUnits.Parameters.newBuilder()
                        /* this parameter value causes an error - and a print message */
                        .setActive(ValuesHelper.value(3))
                        .build()
        );

        List<String> messageList = new ArrayList<String>();
        String returnStatus = null;

        try {
            /* iterating over all results */
            while (resultIterator.hasNext()) {
                MiGetUnits.Response result = resultIterator.next();
                /* Before the expected exception (see below) there should be a message result we can process */
                if (result.getMessageList() != null && result.getMessageList().size() > 0) {
                    for (ProcedureMessage.Message message : result.getMessageList()) {
                        messageList.add(message.getMessage());
                    }
                }
            }
        } catch (StatusRuntimeException ignore) {
            /*  We expect an exception because of "setActive = 3" - in this case we set the return status  */
            returnStatus = ignore.getTrailers().get(DstoreMetadata.ENGINE_RETURN_STATUS_KEY);
        }

        /* The Return Status should be "-500" (invalid parameter) */
        Assert.assertEquals("-500", returnStatus);

        /* There should be a print message */
        Assert.assertTrue(messageList.size() > 0);


        /* And it should be the cause of the expected error */
        Assert.assertEquals("mi_GetUnits : parameter \"@Active\" is invalid", messageList.get(0));

    }
}

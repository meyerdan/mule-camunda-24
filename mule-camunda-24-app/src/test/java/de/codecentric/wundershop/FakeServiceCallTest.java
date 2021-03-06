package de.codecentric.wundershop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import de.codecentric.wundershop.fakeshopservice.FakeShopserviceImplementation;
import de.codecentric.wundershop.shopservice.ShopStatus;
import de.codecentric.wundershop.shopservice.to.GetUnprocessedPaymentsResponse;

public class FakeServiceCallTest extends FunctionalTestCase {
    
    protected String getConfigResources() {
	return "src/main/app/fakeshopsystem.xml,src/main/app/wundershop.xml";
    }
    
    @Test
    public void testGetUnprocessedPayments() throws MuleException {
	GetUnprocessedPaymentsResponse response = (GetUnprocessedPaymentsResponse) call("getUnprocessedPayments", null);
        List<String> empty = response.getPayments();
	assertTrue(empty.isEmpty());
    }
    
    @Test
    public void testMarkPaymentProcessed() throws MuleException {
	FakeShopserviceImplementation fakeShop = muleContext.getRegistry().lookupObject("fakeShop");
        fakeShop.markOrderAsPaid("first");
        fakeShop.markOrderAsPaid("second");
	GetUnprocessedPaymentsResponse response = (GetUnprocessedPaymentsResponse) call("getUnprocessedPayments", null);
        List<String> list = response.getPayments();
	assertEquals(2, list.size());
	assertEquals("first", list.get(0));
	assertEquals("second", list.get(1));
	call("markPaymentProcessed", "first");
	response = (GetUnprocessedPaymentsResponse) call("getUnprocessedPayments", null);
        list = response.getPayments();
	assertEquals(1, list.size());
	assertEquals("second", list.get(0));
    }
    
    @Test
    public void testSetStatusFlow() throws MuleException {
	FakeShopserviceImplementation fakeShop = muleContext.getRegistry().lookupObject("fakeShop");
	MuleClient client = muleContext.getClient();
	Object args[] = new Object[] { "id", ShopStatus.SHIPPED };
	MuleMessage reply = client.send("vm://set-status", args, null);
	assertNotNull(reply);
	assertEquals(ShopStatus.SHIPPED, fakeShop.getStatus("id"));
    }
    
    @Test
    public void testMarkOrderAsShipped() throws MuleException {
	call("setStatus", new Object[] { "first", ShopStatus.SHIPPED });
	// Not verified: There should be a log message...
    }
    
    private Object call(String operation, Object argument) throws MuleException {
	Map<String, Object> props = new HashMap<>();
	props.put("operation", operation);
	MuleClient client = muleContext.getClient();
	MuleMessage reply = client.send("vm://fakeservicecaller", argument, props);
	return reply.getPayload();
    }
}

package de.codecentric.wundershop;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import de.codecentric.wundershop.domain.Bestellung;

/**
 * Kleiner Testfall, damit man das Json nicht manuell schreiben muss.
 */
public class OrderFlowTest extends FunctionalTestCase {
    private String bestellungJson;
    
    protected String getConfigResources() {
	return "src/main/app/wundershop.xml";
    }

    @Before
    public void readJson() throws Exception {
	StringBuilder sb = new StringBuilder();
	try (Reader rd = new InputStreamReader(getClass().getResourceAsStream("bestellung.json"))) {
	    int ch = rd.read();
	    while (ch != -1) {
		sb.append((char)ch);
		ch = rd.read();
	    }
	}
	bestellungJson = sb.toString();
    }
    
    @Test
    public void test() throws MuleException {
	MuleClient client = muleContext.getClient();
	client.dispatch("vm://vm-mail-test-in", bestellungJson, null);
	MuleMessage reply = client.request("vm://vm-mail-test-out", 10000);
	assertNotNull(reply);
	Bestellung b  = (Bestellung) reply.getPayload();
	assertNotNull(b);
	assertEquals("Keine schlaue Bemerkung", b.getBemerkung());
	assertEquals(2, b.getPositionen().size());
    }
}
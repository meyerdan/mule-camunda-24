package de.codecentric.wundershop.shopservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import de.codecentric.wundershop.shopservice.to.GetUnprocessedPaymentsResponse;

@WebService(targetNamespace = "http://shop.de/services")
public interface Shopservice {
    /**
     * @param id ID of an order
     * @param status New status of the order.
     */
    @WebMethod
    public void setStatus(String id, ShopStatus status);

    /**
     * @return Order IDs of orders where a payment is received but not yet processed.
     */
    @WebMethod
    public GetUnprocessedPaymentsResponse getUnprocessedPayments();
    
    /**
     * @param id ID of a marked as processed payment, no longer returned by {@link #getUnprocessedPayments()}.
     */
    @WebMethod
    public String markPaymentProcessed(String id);
}

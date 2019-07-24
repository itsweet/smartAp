package com.example.lit.smartap_20180111.core.network.stack;

import com.example.lit.smartap_20180111.core.coap.EmptyMessage;
import com.example.lit.smartap_20180111.core.coap.Request;
import com.example.lit.smartap_20180111.core.coap.Response;
import com.example.lit.smartap_20180111.core.network.Exchange;
import com.example.lit.smartap_20180111.core.server.MessageDeliverer;

import java.util.concurrent.ScheduledExecutorService;

/**
 * CoapStack is what CoapEndpoint uses to send messages through distinct layers.
 */
public interface CoapStack {

	// delegate to top
	void sendRequest(Request request);

	// delegate to top
	void sendResponse(Exchange exchange, Response response);

	// delegate to top
	void sendEmptyMessage(Exchange exchange, EmptyMessage message);

	// delegate to bottom
	void receiveRequest(Exchange exchange, Request request);

	// delegate to bottom
	void receiveResponse(Exchange exchange, Response response);

	// delegate to bottom
	void receiveEmptyMessage(Exchange exchange, EmptyMessage message);

	void setExecutor(ScheduledExecutorService executor);

	void setDeliverer(MessageDeliverer deliverer);

	void destroy();

	boolean hasDeliverer();
}

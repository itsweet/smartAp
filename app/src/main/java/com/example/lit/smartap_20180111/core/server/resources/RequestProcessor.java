/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Martin Lanter - architect and re-implementation
 *    Dominique Im Obersteg - parsers and initial implementation
 *    Daniel Pauli - parsers and initial implementation
 *    Kai Hudalla - logging
 ******************************************************************************/
package com.example.lit.smartap_20180111.core.server.resources;

import com.example.lit.smartap_20180111.core.network.Exchange;

/**
 * A RequestProcessor is able to process requests in the sense of a server that
 * responds to the request with a response message.
 */
public interface RequestProcessor {

	/**
	 * Process the request from the specified exchange.
	 *
	 * @param exchange the exchange with the request
	 */
	public void processRequest(Exchange exchange);
	
}

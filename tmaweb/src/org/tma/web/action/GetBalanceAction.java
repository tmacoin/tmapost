package org.tma.web.action;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.GetBalanceRequest;
import org.tma.peer.thin.ResponseHolder;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

import com.opensymphony.xwork2.ActionSupport;

public class GetBalanceAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private String tmaAddress;
	private String balance;

    public String execute() throws Exception {
    	logger.debug("tmaAddress: {}", tmaAddress);
    	if(!StringUtil.isTmaAddressValid(tmaAddress)) {
    		addActionMessage("TMA address " + tmaAddress + " is not valid");
    		return SUCCESS;
    	}
        
        Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
        GetBalanceRequest request = new GetBalanceRequest(Network.getInstance(), tmaAddress);
		request.start();
		balance = (String)ResponseHolder.getInstance().getObject(request.getCorrelationId()); 
		
		logger.debug("balance: {} for {}", balance, tmaAddress);

        return SUCCESS;
    }

	public String getTmaAddress() {
		return tmaAddress;
	}

	public void setTmaAddress(String tmaAddress) {
		this.tmaAddress = tmaAddress;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

}

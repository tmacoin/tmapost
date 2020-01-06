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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.Ratee;
import org.tma.peer.thin.Rating;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchRateeRequest;
import org.tma.peer.thin.SearchRatingRequest;
import org.tma.util.StringUtil;

import com.opensymphony.xwork2.ActionSupport;

public class ShowPostAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final Logger logger = LogManager.getLogger();
	private String identifier;
	private String name;
	private Ratee post;
	private List<Rating> ratings;

    public String execute() throws Exception {
    	findRatee(name, identifier);
    	findRatings();
        return SUCCESS;
    }
    
	private void findRatee(String account, String transactionId) {
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}

		SearchRateeRequest request = new SearchRateeRequest(network, account, transactionId);
		request.start();
		post = (Ratee) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(post == null) {
			logger.debug("Failed to retrieve ratee. Please try again");
			addActionMessage("Failed to retrieve ratee. Please try again");
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void findRatings() {
		if(post == null) {
			return;
		}
		
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		String accountName = StringUtil.trim(post.getName());
		SearchRatingRequest request = new SearchRatingRequest(network, accountName, post.getTransactionId());
		request.start();
		ratings = (List<Rating>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(ratings == null) {
			addActionMessage("Failed to retrieve ratings. Please try again");
			return;
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ratee getPost() {
		return post;
	}

	public List<Rating> getRatings() {
		return ratings;
	}

}

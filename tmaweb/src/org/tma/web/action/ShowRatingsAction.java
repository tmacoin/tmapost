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

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.Rating;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchRatingForRaterRequest;
import org.tma.util.TmaLogger;

import com.opensymphony.xwork2.ActionSupport;

public class ShowRatingsAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	
	private String rater;
	private List<Rating> ratings;

    public String execute() throws Exception {
    	findRatings();
        return SUCCESS;
    }
	
	@SuppressWarnings("unchecked")
	private void findRatings() {
		
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		SearchRatingForRaterRequest request = new SearchRatingForRaterRequest(network, rater);
		request.start();
		ratings = (List<Rating>) ResponseHolder.getInstance().getObject(request.getCorrelationId());

		
		if(ratings == null) {
			addActionMessage("Failed to retrieve ratings. Please try again");
			return;
		}
		logger.debug("ratings.size()={}", ratings.size());
	}

	public List<Rating> getRatings() {
		return ratings;
	}

	public String getRater() {
		return rater;
	}

	public void setRater(String rater) {
		this.rater = rater;
	}

}

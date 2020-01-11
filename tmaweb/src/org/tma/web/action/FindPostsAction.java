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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tma.peer.BootstrapRequest;
import org.tma.peer.Network;
import org.tma.peer.thin.Ratee;
import org.tma.peer.thin.ResponseHolder;
import org.tma.peer.thin.SearchRateesRequest;
import org.tma.util.StringUtil;
import org.tma.util.TmaLogger;

import com.opensymphony.xwork2.ActionSupport;

public class FindPostsAction extends ActionSupport {

	private static final long serialVersionUID = 2135249924702466538L;
	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final int MAX_NUMBER_OF_KEYWORDS = 10;
	
	private String post;
	private String keywords;
	private List<Ratee> posts;
	

    public String execute() throws Exception {

    	findRatees();
    	logger.debug("Number of posts found: {}", posts==null? 0: posts.size());

        return SUCCESS;
    }


	public String getPost() {
		return post;
	}


	public void setPost(String post) {
		this.post = post;
	}

	public String getKeywords() {
		return keywords;
	}


	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	@SuppressWarnings("unchecked")
	private void findRatees() {
		Set<String> words = getKeywords(keywords);
		Network network = Network.getInstance();
		if(!network.isPeerSetComplete()) {
			new BootstrapRequest(network).start();
		}
		
		String accountName = StringUtil.trim(post);
		SearchRateesRequest request = new SearchRateesRequest(network, accountName, words);
		request.start();
		posts = (List<Ratee>) ResponseHolder.getInstance().getObject(request.getCorrelationId());
		
		if(posts == null) {
			addActionMessage("Failed to retrieve posts. Please try again");
			return;
		}
		
		if(posts.size() == 0) {
			addActionMessage("No posts were found for provided keywords.");
			return;
		}
		
	}
	
	public Set<String> getKeywords(String keywords) {
		Set<String> set = new HashSet<String>();
		String[] strings = keywords.split(" ");
		for(String str: strings) {
			if(set.size() > MAX_NUMBER_OF_KEYWORDS) {
				break;
			}
			if(!"".equals(str)) {
				set.add(str.toLowerCase());
			}
		}
		return set;
	}


	public List<Ratee> getPosts() {
		return posts;
	}


}

/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.peer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.tma.util.Base58;
import org.tma.util.GsonUtil;
import org.tma.util.Listeners;
import org.tma.util.QueueMap;
import org.tma.util.StringUtil;
import org.tma.util.ThreadExecutor;
import org.tma.util.TmaLogger;
import org.tma.util.TmaRunnable;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class Peer implements Serializable {

	private static final TmaLogger logger = TmaLogger.getLogger();
	private static final Map<String, String> connectedNetworkIdentifiers =  Collections.synchronizedMap(new QueueMap<String, String>(1));
	private static final Listeners listeners = Listeners.getInstance();
	private static final GsonUtil gsonUtil = GsonUtil.getInstance();

	private static final long serialVersionUID = -2552131269155138846L;
	private static final Map<String, Peer> peers = new WeakHashMap<String, Peer>();
	private static final int WAIT_PERIOD_MINUTES = 1;
	private static final int REQUESTS_QUEUE_SIZE = 100;
	public static final int CONNECT_TIMEOUT = 10000;
	
	private InetSocketAddress iNetAddress;
    private boolean fromPeer;
    private boolean localPeer;
    
    private String networkIdentifier;
    private String version = Message.VERSION;
    private int blockchainId;
    private transient boolean blockchainIdSet;
    private transient Socket socket;
    private transient JsonWriter writer;
	private transient JsonReader reader;
	private transient boolean receiverStarted = false;
	private transient boolean senderStarted = false;
	private transient Map<Integer, BlockingQueue<Response>> responses = Collections.synchronizedMap(new HashMap<Integer, BlockingQueue<Response>>());
	private transient BlockingQueue<Request> requests;
	private transient long responseCounter;
	private boolean saved;
	private boolean thin;
	private transient boolean doDisconnect;
	private transient Set<PeerResetListener> resetListeners = new HashSet<>();
	
	public boolean isConnected() {
		Socket socket = this.socket;
		boolean result = socket != null && socket.isConnected() && !socket.isClosed();
		return result;
	}
	
	private void poisonResponses() {
		Set<Integer> keys;
		Map<Integer, BlockingQueue<Response>> responses = getResponses();
		synchronized (responses) {
			keys = new HashSet<Integer>(responses.keySet());
		}
		for (int i : keys) {
			getResponses(i).offer(new PoisonPillResponse());
		}
	}
    
    public static synchronized Peer getInstance(InetSocketAddress iNetAddress) {
    	Peer peer = new Peer(iNetAddress);
    	Peer instance = peers.get(peer.toString());
    	if(instance == null) {
    		instance = peer;
    		peers.put(instance.toString(), instance);
    	}
    	return instance;
    }
    

	private Peer(InetSocketAddress iNetAddress) {
		this.iNetAddress = iNetAddress;
	}

	public InetSocketAddress getiNetAddress() {
		return iNetAddress;
	}

	public String toString() {
		if(iNetAddress == null || iNetAddress.getAddress() == null) {
			return "";
		}
		return iNetAddress.getAddress().getHostAddress() + ":" + iNetAddress.getPort();
	}

	public boolean equals(Object object) {
		if(object == null || !(object instanceof Peer)) {
			return false;
		}
		Peer peer = (Peer)object;
		return toString().equals(peer.toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}


	public synchronized Socket getSocket() throws Exception {
		Socket localSocket = socket;
		if(localSocket != null && !localSocket.isClosed()) {
			return localSocket;
		}
		localSocket = new Socket();
		
		localSocket.connect(iNetAddress, CONNECT_TIMEOUT);
		
		Network network = Network.getInstance();
		if(network.getBlockchainId() == getBlockchainId()) {
			logger.debug("{} <--> {} Opened socket {}", network.getBlockchainId(), getBlockchainId(), localSocket);
		}
		socket = localSocket;
		setDoDisconnect(false);
		return localSocket;
	}
	
	public Socket getRawSocket() {
		return socket;
	}
	
	public void reset(String reason) {
		setDoDisconnect(true);
		Socket localSocket = socket;
		if(localSocket == null) {
			return;
		}
		
		try {
			if (localSocket != null) {
				localSocket.close();
				Network network = Network.getInstance();
				if(network.getBlockchainId() == getBlockchainId()) {
					logger.debug("{} <--> {} Closed socket {} {} fromPeer={} localPeer={} reason={}", network.getBlockchainId(), getBlockchainId(), localSocket, networkIdentifier, fromPeer, localPeer, reason);
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		socket = null;
		writer = null;
		reader = null;
		receiverStarted = false;
		senderStarted = false;
		getRequests().offer(new PoisonPillRequest());
		poisonResponses();
		if(!isFromPeer()) {
			Map<String, Peer> map = SkipPeers.getInstance().getMap();
			synchronized(map) {
				map.put(networkIdentifier, this);
			}
		}
		clearResponses();
		responseCounter = 0;
		for(PeerResetListener listener: getResetListeners()) {
			try {
				listener.onPeerReset(this);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void save() {
		if(saved || isFromPeer() || !isBlockchainIdSet()) {
			return;
		}
		try {
			saved = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}


	public JsonWriter getWriter() throws Exception {
		if(writer != null) {
			return writer;
		}
		writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(getSocket().getOutputStream(), StandardCharsets.UTF_8)));
		writer.setLenient(true);
		return writer;
	}


	public JsonReader getReader() throws Exception {
		if(reader != null) {
			return reader;
		}
		reader = new JsonReader(new BufferedReader(new InputStreamReader(getSocket().getInputStream(), StandardCharsets.UTF_8)));
		reader.setLenient(true);
		return reader;
	}
	
	public boolean startReceiver(final Network network) {
		if (receiverStarted) {
			return true;
		}
		synchronized (this) {
			if (receiverStarted) {
				return true;
			}
			try {
				clearResponses();
				getWriter();
				getReader();
			} catch (SocketTimeoutException e) {
				network.removePeer(this, e.getMessage());
				return false;
			} catch (ConnectException e) {
				network.removePeer(this, e.getMessage());
				return false;
			} catch (NoRouteToHostException e) {
				network.removePeer(this, e.getMessage());
				return false;
			} catch (IOException e) {
				network.removePeer(this, e.getMessage());
				return false;
			} catch (Throwable e) {
				logger.error("iNetAddress={}", iNetAddress);
				logger.error(e.getMessage(), e);
				network.removePeer(this, e.getMessage());
				return false;
			}

			ThreadExecutor.getInstance().execute(new TmaRunnable("Peer receiver " + socketToString(socket)) {
				public void doRun() {
					receive(network);
				}
			});

			receiverStarted = true;
		}
		return true;
	}
	
	public void send(Network network, Request firstRequest) {
		try {
			if(REQUESTS_QUEUE_SIZE == getRequests().size() ) {
				network.removePeer(this, "getRequests().size()=" + getRequests().size());
				firstRequest.onSendComplete(this);
				return;
			}
			getRequests().put(firstRequest);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		startSender(network, firstRequest);
	}
	
	private void startSender(final Network network, Request firstRequest) {
		if(senderStarted) {
			return;
		}
		synchronized (this) {
			if(senderStarted) {
				return;
			}
			try {
				clearRequests();
				getRequests().put(firstRequest);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			ThreadExecutor.getInstance().execute(new TmaRunnable("Peer sender /" + iNetAddress.getAddress().getHostAddress() + ":" + iNetAddress.getPort()) {
				public void doRun() {
					sendLoop(network);
				}
			});
			senderStarted = true;
		}
	}
	
	private void clearRequests() {
		BlockingQueue<Request> requests = getRequests();
		List<Request> list = new ArrayList<>();
		requests.drainTo(list);
		for(Request request: list) {
			request.onSendComplete(this);
		}
	}
	
	private void sendLoop(Network network) {
		do {
			Request request = null;
			try {
				request = getRequests().take();
				if(request instanceof PoisonPillRequest) {
					break;
				}
				if (!doSend(network, request).isSuccess()) {
					network.removePeer(this, "doSend did not return success");
					break;
				}
			} catch (InterruptedException e) {
				break;
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			} finally {
				if(request != null) {
					request.onSendComplete(this);
				}
			}
		} while(isConnected());
		senderStarted = false;
		clearRequests();
	}
	
	private void clearResponses() {
		Map<Integer, BlockingQueue<Response>> responses = getResponses();
		synchronized (responses) {
			for (BlockingQueue<Response> responseQueue : responses.values()) {
				responseQueue.drainTo(new ArrayList<Response>());
			}
			getResponses().clear();
		}
	}
	
	private Response doSend(Network network, Request firstRequest) {
		Response response = new Response();
		if (getiNetAddress() == null) {
			return response;
		}
		if(!startReceiver(network)) {
			return response;
		}
		try {
			JsonWriter writer = getWriter();
			
			Request request = firstRequest;
			do {
				synchronized (this) {
					request.setNetworkIdentifier(network.getNetworkIdentifier());
					gsonUtil.write(request, writer);
					BlockingQueue<Response> responseQueue = getResponses(firstRequest.getCorrelationId());
					if(!senderStarted) {
						network.removePeer(this, "!senderStarted");
						clearResponses();
						return new Response();
					}
					response = responseQueue.poll(WAIT_PERIOD_MINUTES, TimeUnit.MINUTES);
					if(response == null) {
						network.removePeer(this, "response == null");
						clearResponses();
						return new Response();
					}
					
					if(!response.isValid()) {
						network.removePeer(this, "!response.isValid()");
						clearResponses();
						return new Response();
					}
					if(StringUtil.compareVersions(request.getVersion(), Message.VERSION) > 0) {
						listeners.sendEvent(new HigherVersionEvent(request.getVersion()));
					}
					if(response instanceof PoisonPillResponse) {
						int size = getResponses(firstRequest.getCorrelationId()).size();
						if (size > 0) {
							logger.error("getResponses().size()={}", size);
						}
						network.removePeer(this, "PoisonPillResponse");
						return new Response();
					}
					if(response.getCorrelationId() != firstRequest.getCorrelationId()) {
						logger.error("response.getCorrelationId()={}, firstRequest.getCorrelationId()={}", response.getCorrelationId(), firstRequest.getCorrelationId());
						logger.error("response={}, firstRequest={}", response, firstRequest);
						network.removePeer(this, "response.getCorrelationId() != firstRequest.getCorrelationId()");
						return new Response();
					}

					request = response.getRequest(network, this);
					setBlockchainId(response.getBlockchainId());

					if(response.isDoDisconnect()) {
						network.removePeer(this, "response.isDoDisconnect()");
						return new Response();
					}
					if(getNetworkIdentifier() !=null && !network.isMate(getBlockchainId()) && isBlockchainIdSet()) {
						network.removePeer(this, "getNetworkIdentifier() !=null && !network.isMate(getBlockchainId()) && isBlockchainIdSet()");
						return new Response();
					}
					save();
					if(request != null) {
						request.setCorrelationId(firstRequest.getCorrelationId());
					}
				}
			} while (response.hasNext());
		} catch (IllegalStateException e) {
			network.removePeer(this, e.getMessage());
			return new Response();
		} catch (SocketException e) {
			network.removePeer(this, e.getMessage());
			return new Response();
		} catch (JsonIOException e) {
			network.removePeer(this, e.getMessage());
			return new Response();
		} catch (IOException e) {
			network.removePeer(this, e.getMessage());
			return new Response();
		} catch (Throwable e) {
	    	logger.error("iNetAddress={}", iNetAddress);
			logger.error(e.getMessage(), e);
			network.removePeer(this, e.getMessage());
			return new Response();
		} finally {
			synchronized (getResponses()) {
				getResponses().remove(firstRequest.getCorrelationId());
			}
		}
		response.setSuccess(true);
		return response;
	}
	
	private boolean isBlockchainTypeValid(String requestNetworkIdentifier) {
		byte[] bytes = Base58.decode(requestNetworkIdentifier);
		return bytes[0] == StringUtil.BLOCKCHAIN_TYPE[0] && bytes[1] == StringUtil.BLOCKCHAIN_TYPE[1];
	}
	
	private boolean receiveLoop(Network network) throws Exception {
		JsonWriter writer = this.writer;
		Request request;
		Response response;
		Message message;
		if(reader == null) {
			return false;
		}
		message = gsonUtil.read(reader);
		if (message == null) {
			try {
				gsonUtil.write(new Response(), writer);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		if (message instanceof DisconnectResponse) {
			network.removePeer(this, message.toString());
			return false;
		}
		if (message instanceof PoisonPillResponse) {
			response = (Response) message;
			setNetworkIdentifier(response.getNetworkIdentifier());
			BlockingQueue<Response> responseQueue = getResponses(response.getCorrelationId());
			responseQueue.offer(response);
			network.removePeer(this, message.toString());
			return false;
		}
		if (message instanceof Response) {
			response = (Response) message;
			if(
					network.getNetworkIdentifier().equals(response.getNetworkIdentifier())
					|| (getNetworkIdentifier() == null && network.getConnectedNetworkIds().contains(response.getNetworkIdentifier()))
			) {
				return false;
			}
			
			if(!response.isValid()) {
				return false;
			}
			
			setNetworkIdentifier(response.getNetworkIdentifier());
			BlockingQueue<Response> responseQueue = getResponses(response.getCorrelationId());
			if(!isConnected()) {
				return false;
			}
			
			boolean offerSuccess = responseQueue.offer(response, WAIT_PERIOD_MINUTES, TimeUnit.MINUTES);
			if(!offerSuccess) {
				logger.error("{} Peer.receiveLoop timeout after {} minutes waiting to offer response. socket={}, response={}", network.getBlockchainId(), WAIT_PERIOD_MINUTES, socket, response);
				return false;
			}
			return true;
		}
		request = (Request) message;
		if(!isBlockchainTypeValid(request.getNetworkIdentifier())) {
			throw new Exception("Blockchain type is not valid");
		}
		
		if(StringUtil.compareVersions(request.getVersion(), Message.VERSION) < 0) {
			//logger.error("received request from peer version {} which is older than current version {}. Disconnecting.", request.getVersion(), Request.VERSION);
		}
		if(StringUtil.compareVersions(request.getVersion(), Message.VERSION) > 0) {
			listeners.sendEvent(new HigherVersionEvent(request.getVersion()));
		}
		if(
				network.getNetworkIdentifier().equals(request.getNetworkIdentifier())
				|| (getNetworkIdentifier() == null && network.getConnectedNetworkIds().contains(request.getNetworkIdentifier()))
				|| !request.isValid()
		) {
			setNetworkIdentifier(request.getNetworkIdentifier());
			response = new PoisonPillResponse();
			response.setCorrelationId(request.getCorrelationId());
			response.setNetworkIdentifier(network.getNetworkIdentifier());
			gsonUtil.write(response, writer);
			network.removePeer(this, "Connected to itself or double connected or request invalid");
			
			return false;
		}
		if(!network.getConnectedNetworkIds().contains(request.getNetworkIdentifier())) {
			if(connectedNetworkIdentifiers.get(request.getNetworkIdentifier()) != null) {
				network.add(this);
			} else {
				//logger.debug("Connected to {}", this);
			}
			connectedNetworkIdentifiers.put(request.getNetworkIdentifier(), request.getNetworkIdentifier());
		}
		setNetworkIdentifier(request.getNetworkIdentifier());
		setVersion(request.getVersion());
		setBlockchainId(request.getBlockchainId());
		save();

		response = request.getResponse(network, this);
		if(!response.isNotCounted()) {
			responseCounter++;
		}
		response.setCorrelationId(request.getCorrelationId());
		if(writer == null) {
			network.removePeer(this, "writer == null");
			return false;
		}
		response.setNetworkIdentifier(network.getNetworkIdentifier());
		if(responseCounter == 1 && network.isPeerSetFull(getBlockchainId()) && isBlockchainIdSet()) {
			response.setDoDisconnect(true);
		}
		if(getNetworkIdentifier() !=null && !network.isMate(getBlockchainId()) && isBlockchainIdSet()) {
			response.setDoDisconnect(true);
		}
		gsonUtil.write(response, writer);
		if(response.isDoDisconnect()) {
			network.removePeer(this, "response.isDoDisconnect()");
			return false;
		}
		return true;
	}
	
	private void receive(Network network) {
		if(socket == null || socket.isClosed()) {
			return;
		}
		JsonWriter writer = null;
		JsonReader reader = null;
		try {
			writer = getWriter(); 
			reader = getReader();
			do {
				if (!receiveLoop(network)) {
					break;
				}
			} while (isConnected());
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		network.removePeer(this, "outside receive()");
		poisonResponses();
		receiverStarted = false;
		senderStarted = false;
		getRequests().offer(new PoisonPillRequest());
	}


	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private BlockingQueue<Response> getResponses(int correlationId) {
		Map<Integer, BlockingQueue<Response>> responses = getResponses();
		synchronized (responses) {
			BlockingQueue<Response> responseQueue = responses.get(correlationId);
			if (responseQueue == null) {
				responseQueue = new SynchronousQueue<Response>();
				responses.put(correlationId, responseQueue);
			}
			return responseQueue;
		}
	}

	public boolean isFromPeer() {
		return fromPeer;
	}

	public void setFromPeer(boolean fromPeer) {
		this.fromPeer = fromPeer;
	}

	public String getNetworkIdentifier() {
		return networkIdentifier;
	}

	public void setNetworkIdentifier(String networkIdentifier) {
		if(networkIdentifier == null) {
			return;
		}
		this.networkIdentifier = networkIdentifier;
	}

	public int getBlockchainId() {
		return blockchainId;
	}

	public void setBlockchainId(int blockchainId) {
		this.blockchainId = blockchainId;
		blockchainIdSet = true;
	}

	public BlockingQueue<Request> getRequests() {
		if(requests == null) {
			requests = new ArrayBlockingQueue<Request>(REQUESTS_QUEUE_SIZE);
		}
		return requests;
	}

	public Map<Integer, BlockingQueue<Response>> getResponses() {
		if(responses == null) {
			responses = Collections.synchronizedMap(new HashMap<Integer, BlockingQueue<Response>>());
		}
		return responses;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	private String socketToString(Socket socket) {
		String result = "/" + socket.getInetAddress().getHostAddress() + ":" + 
				socket.getPort() + ", localPort=" + 
				socket.getLocalPort();
		return result;
	}

	public boolean isLocalPeer() {
		return localPeer;
	}

	public void setLocalPeer(boolean localPeer) {
		this.localPeer = localPeer;
	}

	public void setResponseCounter(long responseCounter) {
		this.responseCounter = responseCounter;
	}

	public boolean isBlockchainIdSet() {
		return blockchainIdSet;
	}

	public boolean isThin() {
		return thin;
	}
	
	public boolean isDoDisconnect() {
		return doDisconnect;
	}
	
	public void addResetListener(PeerResetListener listener) {
		getResetListeners().add(listener);
	}

	private Set<PeerResetListener> getResetListeners() {
		if(resetListeners == null) {
			resetListeners = new HashSet<>();
		}
		return resetListeners;
	}

	public void setDoDisconnect(boolean doDisconnect) {
		this.doDisconnect = doDisconnect;
	}

}

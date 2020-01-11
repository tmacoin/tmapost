package org.tma.web.action;
import org.tma.util.TmaLogger;

import com.opensymphony.xwork2.ActionSupport;

public class StartNetworkAction extends ActionSupport {

    private static final long serialVersionUID = 7353477345330099548L;
    private static final TmaLogger logger = TmaLogger.getLogger();
    
    private String passphrase;
    private boolean walletLoaded;

    public String execute() throws Exception {
        walletLoaded = !Wallets.getInstance().getNames(Wallets.TMA).isEmpty();
        if(walletLoaded) {
        	return SUCCESS;
        }
        startNetwork();
        walletLoaded = !Wallets.getInstance().getNames(Wallets.TMA).isEmpty();
        return SUCCESS;
    }
    
    private void startNetwork() {
    	logger.debug("TMA POST Web starting up");
		try {
			Wallets.WALLET_NAME = "0";
			PasswordUtil passwordUtil = new PasswordUtil();
			if (!passwordUtil.loadKeys(passphrase)) {
				logger.error("Could not load keys");
				return;
			}
			StartNetwork.getInstance().start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
    }

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
	
	public boolean isWalletLoaded() {
		return walletLoaded;
	}

}

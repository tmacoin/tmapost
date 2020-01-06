package org.tma.web.action;
import com.opensymphony.xwork2.ActionSupport;

public class StartAction extends ActionSupport {

    private static final long serialVersionUID = 7353477345330099548L;
    
    private boolean walletLoaded;

    public String execute() throws Exception {
        walletLoaded = !Wallets.getInstance().getNames(Wallets.TMA).isEmpty();
        return SUCCESS;
    }

	public boolean isWalletLoaded() {
		return walletLoaded;
	}
}

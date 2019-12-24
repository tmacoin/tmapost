/*******************************************************************************
 * Copyright � 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;

import net.miginfocom.swing.MigLayout;

public class TmaPost {
	
	private static final Logger logger = LogManager.getLogger();
	public static final String KEYS = "config/keys.csv";

	private JFrame frame;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	
	@Option(name="-w", aliases="--walletId", usage="Optional wallet id number, default 0", metaVar="<wallet id number>")
	private String walletId = "0";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//TwitterStore.getInstance().removeAll();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TmaPost tmapost = new TmaPost();
					tmapost.parseArgs(args);
					Wallets.WALLET_NAME = tmapost.walletId;
					tmapost.frame.setVisible(true);
					logger.debug("TMA Post Started");
					logger.debug("Wallets.WALLET_NAME={}", Wallets.WALLET_NAME);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TmaPost() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().setLayout(null);
		frame.setTitle("TMA Post");

		File file = new File(KEYS);
		if(!file.exists()) {
			createNewPassphrase();
		} else {
			enterPassphrase();
		}
		frame.setSize(640, 480);
		setIcon();
		
	}
	
	private void createNewPassphrase() {
		
		JPanel form = new JPanel(new MigLayout(
		        "wrap 2",
		        "[right][fill]"
		        ));
		
		JLabel label = new JLabel("Enter Passphrase:");
		form.add(label);
		
		passwordField = new JPasswordField(45);
		form.add(passwordField);
		
		label = new JLabel("Reenter New Passphrase:");
		form.add(label);

		confirmPasswordField = new JPasswordField(45);
		form.add(confirmPasswordField);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new CreateKeyAction(frame, passwordField, confirmPasswordField));
		p.add(btnSubmit);
		form.add(p);

		frame.getContentPane().add(form, BorderLayout.NORTH);
		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	
	private void enterPassphrase() {
		
		JPanel form = new JPanel(new MigLayout("wrap 2", "[right][fill]"));
		
		JLabel label = new JLabel("Enter Passphrase:");
		form.add(label);
		
		passwordField = new JPasswordField(45);
		form.add(passwordField);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SubmitPasswordAction(frame, passwordField));
		p.add(btnSubmit);
		form.add(p);

		frame.getContentPane().add(form);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	
	private void setIcon() {
		try {
            URL resource = frame.getClass().getResource("/tmaLogo.png");
            BufferedImage image = ImageIO.read(resource);
            frame.setIconImage(image);
        } catch (IOException e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	private void parseArgs(final String[] arguments) throws IOException {
		final CmdLineParser parser = new CmdLineParser(this);
		if (arguments.length != 0 && arguments.length != 2) {
			printUsage(parser);
		}
		try {
			parser.parseArgument(arguments);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printUsage(parser);
		}
	}
	
	private void printUsage(CmdLineParser parser) {
		System.err.println("Usage: org.tma.post.TmaPost [options...]");
		parser.printUsage(System.err);
		System.err.println("Example: java org.tma.post.TmaPost" + parser.printExample(OptionHandlerFilter.REQUIRED));
		System.exit(-1);
	}
	
	
}

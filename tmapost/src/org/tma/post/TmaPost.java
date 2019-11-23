/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

public class TmaPost {
	
	private static final Logger logger = LogManager.getLogger();
	public static final String KEYS = "config/keys.csv";

	private JFrame frame;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TmaPost window = new TmaPost();
					window.frame.setVisible(true);
					logger.debug("TMA Post Started");
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
		
		setIcon();
		
	}
	
	private void createNewPassphrase() {
		
		JPanel form = new JPanel(new BorderLayout());
		
		JPanel labelPanel = new JPanel(new GridLayout(3, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(3, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Enter Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		label = new JLabel("Reenter New Passphrase:");
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordField = new JPasswordField(20);
		p.add(passwordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		confirmPasswordField = new JPasswordField(20);
		p.add(confirmPasswordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new CreateKeyAction(frame, passwordField, confirmPasswordField));
		p.add(btnSubmit);
		fieldPanel.add(p);

		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
		
		//frame.pack();
	}
	
	private void enterPassphrase() {
		
		JPanel form = new JPanel(new BorderLayout());
		
		JPanel labelPanel = new JPanel(new GridLayout(2, 1));
		JPanel fieldPanel = new JPanel(new GridLayout(2, 1));
		form.add(labelPanel, BorderLayout.WEST);
		form.add(fieldPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel("Enter Passphrase:", JLabel.RIGHT);
		labelPanel.add(label);
		
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordField = new JPasswordField(20);
		p.add(passwordField);
		fieldPanel.add(p);
		
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SubmitPasswordAction(frame, passwordField));
		p.add(btnSubmit);
		fieldPanel.add(p);

		frame.getContentPane().add(form, BorderLayout.NORTH);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
		
		frame.setSize(640, 480);
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
	
	
}

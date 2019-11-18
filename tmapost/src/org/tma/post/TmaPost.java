/*******************************************************************************
 * Copyright © 2018 Tma Coin admin@tmacoin.org, tmacoin@yahoo.com All rights reserved. No warranty, explicit or implicit, provided.
 * Permission granted to use Tma Coin client/blockchain free of charge. Any part of the software cannot be copied or modified to run or be used on any new or existing blockchain, distributed ledger, consensus system.
 * Any contribution to the project to improve and promote Tma Coin is welcome and automatically becomes part of Tma Coin project with this copyright.
 *
 * Authors addresses: 8LpN97eRQ2CQ95DaZoMiNLmuSM7NKKVKrUda, 6XUtJgWAzbqCH2XkU3eJhMm1eDcsQ8vDg8Uo
 *******************************************************************************/
package org.tma.post;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
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
		JLabel lblEnterPassphrase = new JLabel("Enter Passphrase:");
		lblEnterPassphrase.setBounds(49, 104, 160, 14);
		frame.getContentPane().add(lblEnterPassphrase);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(209, 101, 138, 20);
		frame.getContentPane().add(passwordField);
		
		JLabel lblconfirmPassphrase = new JLabel("Reenter New Passphrase:");
		lblconfirmPassphrase.setBounds(49, 134, 160, 14);
		frame.getContentPane().add(lblconfirmPassphrase);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setBounds(209, 131, 138, 20);
		frame.getContentPane().add(confirmPasswordField);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new CreateKeyAction(frame, passwordField, confirmPasswordField));
		btnSubmit.setBounds(256, 162, 91, 23);
		frame.getContentPane().add(btnSubmit);
		
		frame.getRootPane().setDefaultButton(btnSubmit);
	}
	
	private void enterPassphrase() {
		JLabel lblEnterPassphrase = new JLabel("Enter Passphrase:");
		lblEnterPassphrase.setBounds(79, 124, 120, 14);
		frame.getContentPane().add(lblEnterPassphrase);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(209, 121, 138, 20);
		frame.getContentPane().add(passwordField);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setAction(new SubmitPasswordAction(frame, passwordField));
		btnSubmit.setBounds(256, 152, 91, 23);
		frame.getContentPane().add(btnSubmit);
		
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
	
	
}

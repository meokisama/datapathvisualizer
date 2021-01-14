package com.asu.plp.communication;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.asu.plp.event.SnapshotListener;

public class FrontendConsumer implements Runnable, ExceptionListener {
	private List<SnapshotListener> listeners = new ArrayList<SnapshotListener>();

	public void addListener(SnapshotListener toAdd) {
		listeners.add(toAdd);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();

				connection.setExceptionListener(this);

				// Create a Session
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue("TEST.FOO");

				// Create a MessageConsumer from the Session to the Topic or Queue
				MessageConsumer consumer = session.createConsumer(destination);

				// Wait for a message
				Message message = consumer.receive(1000);

				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					String text = textMessage.getText();
					System.out.println("Received: " + text);
					for (SnapshotListener hl : listeners)
						hl.receiveSnapshot(text);
				} else {
					System.out.println("Received: " + message);
				}

				consumer.close();
				session.close();
				connection.close();
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}
	}
	public synchronized void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}
}
package com.asu.plp.communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class BackendProducer implements Runnable {
	public void run() {

		int test_number = 0;
		while (true) {
			try {
				Thread.sleep(1000);
				// Create a ConnectionFactory
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();

				// Create a Session
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createQueue("TEST.FOO");

				// Create a MessageProducer from the Session to the Topic or Queue
				MessageProducer producer = session.createProducer(destination);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

				// Create a messages
				String conf_file = "test_data/fake" + String.valueOf(test_number) + ".json";
				test_number++;
				if (test_number > 4)
					test_number = 0;
				BufferedReader reader;
				String line = null;
				String text = "";
				try {
					reader = new BufferedReader(new FileReader (conf_file));
					while((line = reader.readLine()) != null) {
						text += line;
					}
					reader.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TextMessage message = session.createTextMessage(text);

				// Tell the producer to send the message
				System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
				producer.send(message);

				// Clean up
				session.close();
				connection.close();
			}
			catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}
	}
}

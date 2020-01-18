package application;

import java.util.*;
import java.util.logging.Level;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class SendMail {//Example:  SendMail.sendMail("avishimon@gmail.com", "Password Recovery", "Your password is: 123456");

	public static void sendMail(String receiverEmail, String messageTitle, String messageBody) throws Exception {

		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "25");
		String senderEmail = "ICMsystem2020@gmail.com";
		String password = "System123456";
		
		Session session = Session.getInstance(properties, new Authenticator()
				{
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(senderEmail, password);
					}
				});
		
		Message message = prepareMessage(session, senderEmail, receiverEmail, messageTitle, messageBody);
		
		//Transport.send(message);
		Transport transport = session.getTransport("smtp");
		
		transport.send(message);
	}

	/**
	 * Prepare the message
	 * @param session
	 * @param myAccountEmail
	 * @param recepient
	 * @param messageTitle
	 * @param messageBody
	 * @return
	 */
	private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String messageTitle, String messageBody)
	{
		try
		{
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myAccountEmail));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
			message.setSubject(messageTitle);
			message.setText(messageBody);
			return message;
		}
		catch(Exception ex)
		{
			System.out.println("OH NO");		
		}
		return null;
	}
}


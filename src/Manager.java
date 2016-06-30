import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Manager {
    private final static String WORK_QUEUE = "Nqueue";
    private final static String NUMBERS_QUEUE = "ArmstrongNumbers";
    private final static String RABBIT_MQ_SERVER = "localhost";

    private static void publishAllWork() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ_SERVER);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(WORK_QUEUE, false, false, false, null);
        for (int i = 1; i < 30; i++) {
            String message = Integer.toString(i);
            channel.basicPublish("", WORK_QUEUE, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }

    private static void readAllNumbers() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(NUMBERS_QUEUE, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };
        channel.basicConsume(NUMBERS_QUEUE, true, consumer);
    }

    public static void main(String[] argv) throws java.io.IOException, TimeoutException {
        publishAllWork();
        readAllNumbers();
    }

}

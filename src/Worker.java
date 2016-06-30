import com.rabbitmq.client.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeoutException;

public class Worker {
    private final static String WORK_QUEUE = "Nqueue";
    private final static String NUMBERS_QUEUE = "ArmstrongNumbers";
    private final static String RABBIT_MQ_SERVER = "localhost";

    private static Channel channel = null;


    private static void doWork(int N) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT_MQ_SERVER);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(NUMBERS_QUEUE, false, false, false, null);

        Generator.generate(N, channel, NUMBERS_QUEUE);

        channel.close();
        connection.close();
    }

    public static void main(String[] argv) throws java.io.IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(WORK_QUEUE, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    doWork(Integer.parseInt(message));
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(WORK_QUEUE, true, consumer);
    }



}

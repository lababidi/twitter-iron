import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by mahmoud on 4/27/15.
 */
public class RetrieverAMQP implements Runnable{
    private QueueingConsumer consumer;

    public RetrieverAMQP(AMQPSettings settings){

        AMQP.BasicProperties.Builder bob = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties minBasic = bob.build();
        AMQP.BasicProperties minPersistentBasic = bob.deliveryMode(2).build();
        AMQP.BasicProperties persistentBasic
                = bob.priority(0).contentType("application/octet-stream").build();
        AMQP.BasicProperties persistentTextPlain = bob.contentType("text/plain").build();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(settings.user);//(String) conf.get("spout.amqp.user"));
        factory.setPassword(settings.password);//(String) conf.get("spout.amqp.password"));
        factory.setVirtualHost(settings.virtualHost);//(String) conf.get("spout.amqp.virtualhost"));
        factory.setHost(settings.host);//(String) conf.get("spout.amqp.host"));
        factory.setPort(settings.port);//((Long) conf.get("spout.amqp.port")).intValue());
        Connection conn;
        try {
            conn = factory.newConnection();
            Channel channel = conn.createChannel();
            String routingKey = "storm";
            channel.exchangeDeclare(settings.exchange, "direct", true);
            AMQP.Queue.DeclareOk queue = channel.queueDeclare();
            String queueName = queue.getQueue();
            channel.queueBind(queueName, settings.exchange, routingKey);
            consumer = new QueueingConsumer(channel);
            boolean autoAck = true;
            String consumerTag = channel.basicConsume(queueName, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed to init amqp connection");
        }
    }

    public void next(){
        try {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
            if (delivery == null)
                return;
            final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
            final byte[] message = delivery.getBody();


        } catch (ShutdownSignalException | ConsumerCancelledException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {

    }
}

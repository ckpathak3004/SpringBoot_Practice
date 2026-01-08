package ck.test.demo.kafka.listner;

import ck.test.demo.pojo.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import org.apache.kafka.common.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class MyKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(MyKafkaListener.class);

    private  ObjectMapper objectMapper =new ObjectMapper();

    @KafkaListener(
            topics = "myTestTopic",
            groupId = "test-group-force-new-v4",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenUserList(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,Acknowledgment acknowledgment)  {
        log.info("Consuming message - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);

        try {
            //since we are using string StringDeserializer in kafkaListenerContainerFactory ,so need to parse this message to desired object type
            List<UserResponse> users = deserializeUserList(message);
            processUsers(users);
            acknowledgment.acknowledge(); //acknowledge manually

            log.info("Successfully processed message from offset: {}", offset);

        } catch (JsonProcessingException e) {
            log.error("Deserialization error for message at offset {}: {}", offset, e.getMessage());
            // Send to DLQ or handle appropriately
            handleDeserializationError(message, topic, partition, offset);

        } catch (Exception e) {
            log.error("Processing error for message at offset {}: {}", offset, e.getMessage(), e);
            // Don't acknowledge - will trigger retry based on error handler configuration
            throw e;
        }
    }

    private List<UserResponse> deserializeUserList(String message) throws JsonProcessingException {
       // return objectMapper.readValue(message, new TypeReference<List<UserResponse>>() {});
          Gson gson= new Gson();
        Type listType = new TypeToken<List<UserResponse>>() {}.getType(); //serialized using jackson library and deserialize using Gson Library
        return gson.fromJson(message,listType);
    }

    private void processUsers(List<UserResponse> users) {
        log.info("Processing {} users", users.size());
        for (UserResponse user : users) {
            // Your business logic
             System.out.println("User Name:"+ user.getUserID());
            System.out.println("User Role:"+ user.getRole());
        }
    }

    private void handleDeserializationError(String message, String topic, int partition, long offset) {
        log.warn("Sending malformed message to DLQ - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);
        // Implement DLQ logic here (e.g., send to a separate Kafka topic or database)
    }

    @KafkaListener(
            topics = "myTestTopic2",
            groupId = "test-group-force-new-v4",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenMsgFromTopic2(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,Acknowledgment acknowledgment){
          System.out.println("Message from topic 2:"+message);
           acknowledgment.acknowledge();
    }


}

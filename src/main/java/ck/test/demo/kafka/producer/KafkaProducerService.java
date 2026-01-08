package ck.test.demo.kafka.producer;

import ck.test.demo.pojo.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducerService {

    private static final String TOPIC_NAME = "myTestTopic";
    private static final String TOPIC_NAME2 = "myTestTopic2";
    @Resource(name="kafkaTemplate")
    private final KafkaTemplate<String, String> kafkaTemplate;


    public KafkaProducerService(KafkaTemplate<String,String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async //run this method call using different thread
    public void sendUserPayload(List<UserResponse> message) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String payload=objectMapper.writeValueAsString(message);
       // Gson gson =new Gson();
       //String payload=gson.toJson(message);
        String threadName = Thread.currentThread().getName();
        System.out.println("Currently running on thread in method:sendUserPayload() : " + threadName);
        CompletableFuture<SendResult<String, String>> response= kafkaTemplate.send(TOPIC_NAME, payload);
        try{
            System.out.println("Key :"+ response.copy().get().getProducerRecord().value());
        } catch (Exception exception){
            System.out.println("failed:"+exception.getMessage());
        }
        kafkaTemplate.send(TOPIC_NAME2,"sending message on topic 2");
    }
}

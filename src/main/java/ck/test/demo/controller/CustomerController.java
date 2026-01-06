package ck.test.demo.controller;

import ck.test.demo.kafka.producer.KafkaProducerService;
import ck.test.demo.pojo.UserResponse;
import ck.test.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.json.JsonMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Resource(name ="userRepository")
    private UserRepository userRepository;

    @Resource(name ="kafkaProducerService")
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private JsonMapper jsonMapper;

    // POST - Create a new product by accepting JSON
    @GetMapping(value = "/getCustomer")
    public ResponseEntity<String> createProduct(@Valid @RequestParam(required = true) String emailId) throws JsonProcessingException {
        List<Object[]> response = userRepository.findUserDetailsByEmail(emailId);
        List users= new ArrayList<>();
            for (Object[] objectArray:response){
                UserResponse userResponse=new UserResponse();
                userResponse.setUserID((String)objectArray[0]);
                userResponse.setRole((String)objectArray[1]);
                users.add(userResponse);
            }

            kafkaProducerService.sendUserPayload(users);
        return new ResponseEntity<>(jsonMapper.writeValueAsString(users), HttpStatus.OK);
    }
}

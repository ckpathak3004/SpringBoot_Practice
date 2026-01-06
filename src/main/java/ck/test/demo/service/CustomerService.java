package ck.test.demo.service;

import ck.test.demo.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CustomerService {
    List<User> getAllUsersAndRole();
}


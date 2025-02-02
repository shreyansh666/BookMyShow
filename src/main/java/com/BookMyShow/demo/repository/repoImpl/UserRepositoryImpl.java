package com.BookMyShow.demo.repository.repoImpl;//package com.example.LibraryManagementSystem.repository.repoImpl;


import com.BookMyShow.demo.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;



    public void updatePassword(String email, String newPassword) {
        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update().set("passwordHash", newPassword);
//        System.out.println(query+ "   f " + update + " " + newPassword);
        mongoTemplate.updateFirst(query, update, User.class);
    }
}
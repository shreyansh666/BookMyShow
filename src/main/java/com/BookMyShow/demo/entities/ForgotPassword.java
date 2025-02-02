package com.BookMyShow.demo.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "forgot_password")
public class ForgotPassword {

    @Id
    private String id;

    private Integer otp;
    private Date ExpirationTime;

    @DBRef
    private User user;


}

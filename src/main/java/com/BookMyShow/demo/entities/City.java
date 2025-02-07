package com.BookMyShow.demo.entities;



import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;



@Data
@SuperBuilder
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(collection = "city")

public class City {

    @Id
    private String id;
    private String name;

//    @DBRef
//    private List<Theater> theaters;


}

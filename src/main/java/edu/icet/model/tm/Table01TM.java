package edu.icet.model.tm;

import lombok.*;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Table01TM {
    private String id;
    private String title;
    private String name;
    private LocalDate dob;
    private Double salary;


}

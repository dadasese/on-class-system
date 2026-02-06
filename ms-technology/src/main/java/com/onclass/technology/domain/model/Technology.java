package com.onclass.technology.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("technologies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Technology {
    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("description")
    private String description;

}

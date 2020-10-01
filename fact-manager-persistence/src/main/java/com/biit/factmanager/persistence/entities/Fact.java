package com.biit.factmanager.persistence.entities;


import java.util.Date;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;


@Entity
@Primary
@Table(name = "facts")
public class Fact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "created_at")
    private Date createdAt;


    @Column(name = "fact")
    private String fact;


    public Fact() {
        setCreatedAt(new Date());
    }


    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt == null ? null : new Date(createdAt.getTime());
    }

    public String getFact(){
        return fact;
    }

    public void setFact(String fact) { this.fact = fact; }


    public Integer getId() {
        return id;
    }
}

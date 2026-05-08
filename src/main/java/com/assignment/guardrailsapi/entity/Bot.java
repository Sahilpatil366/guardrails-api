package com.assignment.guardrailsapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "bots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "persona_description")
    private String personaDescription;
}
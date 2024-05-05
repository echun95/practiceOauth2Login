package com.project.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_TEST")
public class Test extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_ID")
    private Long testId;

    @Column(name = "TEST_NAME")
    private String name;

    @Builder
    public Test(Long testId, String name) {
        this.testId = testId;
        this.name = name;
    }
}

package com.authentication.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractEntity<U> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //We provide @CreatedBy, @LastModifiedBy to capture the user who created or modified the entity
    //As well as @CreatedDate and @LastModifiedDate to capture the point in time this happened
    //The generic type U defines of what type the properties annotated with @CreatedBy or @LastModifiedBy have to be
    @CreatedBy
    protected U createdBy;
    @CreatedDate
    protected Date creationDate;
    @LastModifiedBy
    protected U lastModifiedBy;
    @LastModifiedDate
    protected Date lastModifiedDate;
}

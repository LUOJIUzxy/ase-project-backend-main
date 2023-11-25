package com.tum.model;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "projects")
public class Project {
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String name;

    protected Project() {
    }

    public Project(String name) {
        this.name = name;
    }

}
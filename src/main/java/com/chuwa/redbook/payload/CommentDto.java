package com.chuwa.redbook.payload;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
/**
 * @author b1go
 * @date 6/23/22 11:10 PM
 */
public class CommentDto {


    private long id;

    @Pattern(
            regexp = "^[A-Za-z\\s]{2,30}$",
            message = "Name must contain only letters and spaces, 2–30 characters long."
    )
    private String name;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format."
    )
    private String email;

    @Size(min = 10, max = 500, message = "Comment must be 10–500 characters long.")
    @Pattern(
            regexp = "^[A-Za-z0-9\\s.,!?'\"]{10,500}$",
            message = "Comment contains invalid characters."
    )
    private String body;

    public CommentDto() {}

    public CommentDto(String name, String email, String body) {
        this.name = name;
        this.email = email;
        this.body = body;
    }

    public CommentDto(long id, String name, String email, String body) {
        this(name, email, body);
        this.id = id;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

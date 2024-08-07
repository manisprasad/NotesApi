package org.manis.notes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sticky_notes")
public class StickyNote {
    @Id
    private String id;
    private String title;
    private String content;
    private String color;
    private String groupName;
    private boolean favourite = false;
    private long createdAt;
    private long updatedAt;

    // Getters and setters
}

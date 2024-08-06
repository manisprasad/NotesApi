package org.manis.notes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sticky_note_groups")
public class StickyNoteGroup {
    @Id
    private ObjectId id;
    private String name;

    @DBRef
    private List<StickyNote> stickyNotes = new ArrayList<>();

    // Getters and setters
}

package org.manis.notes.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {
    private String name;
    private String email;
    private int totalGroup;
    private int totalStickyNotes;
    private int totalFav;
    private List<String> stickyNoteGroupNames;
    private Date createdDate;
    private String role;
    private List<StickyNote> recentActivities;
}

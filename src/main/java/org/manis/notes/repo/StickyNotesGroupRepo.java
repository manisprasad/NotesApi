package org.manis.notes.repo;

import org.bson.types.ObjectId;
import org.manis.notes.models.StickyNoteGroup;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StickyNotesGroupRepo extends MongoRepository<StickyNoteGroup, ObjectId> {

}

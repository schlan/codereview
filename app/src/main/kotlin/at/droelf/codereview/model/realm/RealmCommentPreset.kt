package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

public open class RealmCommentPreset(
        @PrimaryKey public open var id: Long = -1,
        public open var comment: String? = null
): RealmObject() {}
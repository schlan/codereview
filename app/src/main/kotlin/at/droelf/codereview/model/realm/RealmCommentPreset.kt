package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmCommentPreset(
        @PrimaryKey open var id: Long = -1,
        open var comment: String? = null
): RealmObject() {}
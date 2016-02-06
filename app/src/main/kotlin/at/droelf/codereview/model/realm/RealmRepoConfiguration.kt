package at.droelf.codereview.model.realm;

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey;

open class RealmRepoConfiguration(
        @PrimaryKey open var id: Long = -1,
        open var pullRequest: Int? = null,
        open var issues: Int? = null
): RealmObject() {}

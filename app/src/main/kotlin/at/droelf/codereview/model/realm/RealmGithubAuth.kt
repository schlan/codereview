package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RealmGithubAuth(
        @PrimaryKey open var id: Long = -1,
        open var url: String? = null,
        open var scopes: String? = null,
        open var token: String? = null,
        open var tokenLastEight: String? = null,
        open var hashedToken: String? = null,
        open var updatedAt: Date? = null
): RealmObject() {}
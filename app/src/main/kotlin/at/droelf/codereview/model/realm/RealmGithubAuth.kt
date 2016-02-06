package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

public open class RealmGithubAuth(
        @PrimaryKey public open var id: Long = -1,
        public open var url: String? = null,
        public open var scopes: String? = null,
        public open var token: String? = null,
        public open var tokenLastEight: String? = null,
        public open var hashedToken: String? = null,
        public open var updatedAt: Date? = null
): RealmObject() {}
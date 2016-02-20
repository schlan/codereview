package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

public open class RealmGithubAccount(
        @PrimaryKey public open var uuid: String? = null,
        public open var auth: RealmGithubAuth? = null,
        public open var user: RealmGithubUser? = null,
        public open var email: String? = null
): RealmObject() {}
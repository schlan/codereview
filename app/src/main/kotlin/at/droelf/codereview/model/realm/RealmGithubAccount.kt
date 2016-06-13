package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmGithubAccount(
        @PrimaryKey open var uuid: String? = null,
        open var auth: RealmGithubAuth? = null,
        open var user: RealmGithubUser? = null,
        open var email: String? = null
): RealmObject() {}
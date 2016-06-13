package at.droelf.codereview.model.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmGithubUser(
        @PrimaryKey open var id: Long = -1,
        open var login: String? = null,
        open var avatarUrl: String? = null,
        open var gravatarId: String? = null,
        open var url: String? = null,
        open var htmlUrl: String? = null,
        open var followersUrl: String? = null,
        open var followingUrl: String? = null,
        open var type: String? = null,
        open var siteAdmin: String? = null
) : RealmObject() {}
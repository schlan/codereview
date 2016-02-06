package at.droelf.codereview.storage

import android.content.Context
import android.content.SharedPreferences
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.realm.RealmGithubAccount
import at.droelf.codereview.model.realm.RealmHelper
import com.google.gson.Gson
import io.realm.Realm

class GithubUserStorage(private val context: Context): RealmHelper {

    private val dataKey = "github_data"

    fun userStored(): Boolean {
        val realm = realm()
        val count = realm.allObjects(RealmGithubAccount::class.java).count()
        return count > 0
    }

    fun storeUser(userData: Model.GithubAuth) {
        val realm = realm()
        realm.beginTransaction()
        realm.copyToRealm(accountToRealm(userData))
//        val user = realm.copyFromRealm(userToRealm(userData.user))
//        val auth = realm.copyToRealm(authResponseToRealm(userData.auth))
//        val account = realm.createObject(RealmGithubAccount::class.java)
//        account.uuid = userData.uuid.toString()
//        account.auth = auth
//        account.user = user
        realm.commitTransaction()
    }

    fun getUserBlocking(): Model.GithubAuth? {
        val realm = realm()
        val auth = realm.allObjects(RealmGithubAccount::class.java).firstOrNull() ?: return null
        return accountToGithub(auth)
    }

    private fun realm(): Realm {
        return Realm.getInstance(context)
    }

}
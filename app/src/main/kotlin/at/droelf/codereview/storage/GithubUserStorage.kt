package at.droelf.codereview.storage

import android.content.SharedPreferences
import at.droelf.codereview.model.Model
import com.google.gson.Gson
import com.google.gson.JsonParseException
import rx.Observable

class GithubUserStorage(private val sharedPreferences: SharedPreferences, private val gson: Gson) {

    private val dataKey = "github_data"

    fun userStored(): Boolean {
        return sharedPreferences.contains(dataKey)
    }

    fun storeUser(userData: Model.GithubAuth) {
        val jsonData = gson.toJson(userData)
        sharedPreferences
                .edit()
                .putString(dataKey, jsonData)
                .apply()
    }

    fun getUser(): Observable<Model.GithubAuth> {
        return Observable.create({
            val json = sharedPreferences.getString(dataKey, "")
            if (json.length > 0) {
                val data = gson.fromJson(json, Model.GithubAuth::class.java)
                if (data != null) {
                    it.onNext(data)
                    it.onCompleted()
                } else {
                    sharedPreferences.edit().clear()
                    it.onError(JsonParseException("unable to parse json"))
                }
            } else {
                sharedPreferences.edit().clear()
                it.onError(JsonParseException("No user stored"))
            }
        })
    }

}
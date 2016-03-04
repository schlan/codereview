package at.droelf.codereview.provider

import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.model.ResponseHolder
import rx.Observable


interface GithubFilter {

    fun repoFilter(repos: List<Model.GithubSubscription>): Observable<List<Model.GithubSubscription>> {
        return Observable.just(repos).map{ removeHiddenRepos(it) }
    }

    fun prFilter(subscriptions: List<Model.GithubSubscription>, prs: ResponseHolder<List<GithubModel.PullRequest>>, user: GithubModel.User, githubProvider: GithubProvider)
            : Observable<ResponseHolder<List<GithubModel.PullRequest>>> {

        val prList = prs.data
        if(prList.isEmpty()) return Observable.empty()
        val subscription = subscriptions.find { it.repo.id == prList.firstOrNull()?.base?.repo?.id } ?: return Observable.just(prs)

        val filteredPrs = prList.map { pr ->
            when(subscription.config.pullRequests){
                Model.WatchType.All -> {
                    Observable.just(Pair(pr, true))
                }
                Model.WatchType.Mine -> {
                    Observable.just(Pair(pr, user.id == pr.user.id))
                }
                Model.WatchType.Participating -> {
                    val owner = subscription.repo.owner.login
                    val repo = subscription.repo.name
                    val number = pr.number

                    val mine = Observable.just(user.id == pr.user.id)
                    val reviewComments = githubProvider.reviewComments(owner, repo, number)
                            .map { it.filter{ it.user.id == user.id }.isNotEmpty() }
                    val comments = githubProvider.comments(owner, repo, number)
                            .map { it.filter { it.user.id == user.id }.isNotEmpty() }

                    Observable.concat(mine, comments, reviewComments)
                            .firstOrDefault(false) { it }
                            .map{ Pair(pr, it) }
                }
                else -> {
                    Observable.just(Pair(pr, false))
                }
            }
        }

        return Observable
                .merge(filteredPrs)
                .filter { it.second }
                .map { it.first }
                .toList()
                .map { ResponseHolder(it, prs.source, prs.timeStamp, prs.alwaysUpToDate, prs.notUpToDate) }
    }

    private fun removeHiddenRepos(repos: List<Model.GithubSubscription>): List<Model.GithubSubscription> {
        return repos.filter { it.config.pullRequests != Model.WatchType.Hide }
    }

}
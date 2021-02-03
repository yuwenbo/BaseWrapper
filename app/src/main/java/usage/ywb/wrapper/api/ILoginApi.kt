package usage.ywb.wrapper.api

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import usage.ywb.wrapper.modules.home.entity.Domain
import usage.ywb.wrapper.modules.home.entity.Login
import usage.ywb.wrapper.mvp.common.entity.ConvertEntity

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/11 ]
 */
interface ILoginApi {
    @FormUrlEncoded
    @POST("http://XXX/noperm/task/login/getDomain")
    fun getDomain(@Field("USERNAME") username: String?): Observable<ConvertEntity<Domain>>?

    @FormUrlEncoded
    @POST("noperm/task/login/post")
    fun  //    @POST("login")
            login(@Field("USERNAME") username: String?, @Field("PASSWORD") password: String?): Observable<Login>?
}
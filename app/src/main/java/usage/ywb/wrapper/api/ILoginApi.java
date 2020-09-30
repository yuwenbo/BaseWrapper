package usage.ywb.wrapper.api;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import usage.ywb.wrapper.mvp.ui.base.entity.ConvertEntity;
import usage.ywb.wrapper.modules.home.entity.Domain;
import usage.ywb.wrapper.modules.home.entity.Login;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/11 ]
 */
public interface ILoginApi {

    @FormUrlEncoded
    @POST("http://XXX/noperm/task/login/getDomain")
    Observable<ConvertEntity<Domain>> getDomain(@Field("USERNAME") String username);

    @FormUrlEncoded
    @POST("noperm/task/login/post")
//    @POST("login")
    Observable<Login> login(@Field("USERNAME") String username, @Field("PASSWORD") String password);



}

package usage.ywb.wrapper.common

import usage.ywb.wrapper.modules.home.entity.User

/**
 * 全局共享数据缓存
 *
 * @author yuwenbo
 * @version [ V.2.1.9  2018/6/26 ]
 */
object GlobalCache {
    var loginRunningUser: User? = null
    var domain //动态域名
            : String? = null
    var token: String? = null

    //根据服务器类型获取所有的接口相同部分的完整地址
    val rootPath: String
        get() = Key.PROTOCOL + domain + "/" + Key.PATH

}
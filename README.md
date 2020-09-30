# BaseWrapper
一个基于MVP+Retrofit2+Gson+Butterknife+ARouter的组件化项目Demo

模块划分规则
core，系统核心组件，主要包括：网络请求、数据库、推送、地图、多媒体（相机、音视频）等等，不包含任何业务，不能依赖其他任何组件。可被除core以外的模块依赖。

base，基础封装组件，主要包括：通用的资源文件（color/style/shape等），项目架构封装，一些第三方SDK的二次封装，自定义View，各种Utils类等。只能被common/module依赖。

common，业务通用组件，公共的业务模块，可供其他业务模块调用。可添加对core/base模块的依赖，只能被module依赖。common之间应避免相互依赖。

module，业务模块，项目的主要业务按模块，可建立对core、base、common的依赖，module互相之间不能有任何依赖关系，应做到绝对解耦，满足独立编译/运行/打包的条件。

app，最上层的应用模块，主工程项目统一入口。使用ARouter的路由方式调用业务模块，不对业务模块产生强依赖。

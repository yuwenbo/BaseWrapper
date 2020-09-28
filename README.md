# BaseWrapper
一个基于MVP+Retrofit2+Gson+Butterknife+ARouter的组件化项目Demo

模块命名规则
base，最基础的组件，所有的模块都可以依赖这个模块，主要用以工程、全局的资源文件（color/style/shape等），不能依赖其他模块。

common，通用组件，一些不包含业务的基础封装，如自定义View，第三方SDK的二次封装（网络请求、数据库、推送、地图等等），项目架构封装。只能添加对base模块的依赖。

commonModule，业务公用组件，公共的业务模块，可独立成一个业务模块，也可供其他组件调用。只能添加对base和common模块的依赖。module之间应尽量避免互相依赖。

module，业务模块，项目的主要业务按模块划分，只能建立对base、common、commonModule的依赖，module互相之间不能有任何依赖关系，应做到绝对解耦。



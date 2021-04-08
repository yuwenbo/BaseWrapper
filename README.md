# BaseWrapper
一个基于MVP+Retrofit2+Gson+Butterknife+ARouter的组件化项目Demo

<h3>模块划分规则:</h3>


## 1，base
基础封装组件，主要包括：通用的资源文件（color/style/shape等），项目架构封装，一些第三方SDK的二次封装，自定义View，Utils工具类等。可被common/module/app依赖。不能依赖其上层组件。

## 2，core
系统核心组件，主要包括：网络请求、数据库、推送、地图、多媒体（相机、音视频）等等，不包含任何业务，不能依赖其上层和平级组件。可被其上层组件依赖。

## 3，common
业务通用组件，公共的业务模块，可供其他业务模块调用。可添加对core/base模块的依赖，只能被module/app依赖。common之间应尽量避免相互依赖。

## 4，module
业务模块，项目的主要业务模块，可建立对core、base、common的依赖，module互相之间不能有任何依赖关系，应做到绝对解耦，可满足独立编译/运行/打包的条件。

## 5，app
app最上层的应用模块，主工程项目统一入口。使用ARouter的路由方式调用业务模块，不对业务模块产生强依赖。

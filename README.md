# RetrofitHelper
这是一个简化使用Retrofit的库

## 1、引用
```
api("com.fpliu:RetrofitHelper:1.0.0")
```
默认引用的是<a href="https://github.com/square/retrofit" target="_blank">Retrofit官方的库</a>，
我自己也对Retrofit官方的库进行了扩展，使得原生的支持简单JSON，
要想使用<a href="https://github.com/leleliu008/retrofit" target=_blank>我扩展的Retrofit库</a>，需要如下设置：

```
api("com.fpliu:RetrofitHelper:1.0.0") {
    //将内置的retrofit2依赖去掉，因为我们要使用自己扩展的retrofit2
    exclude("com.squareup.retrofit2", "retrofit")
}
api("com.fpliu:retrofit:2.4.0")
```

## 2、配置（非必须）
示例：
```
RetrofitRequest.init(object : RetrofitRequest.AbstractConfig() {
    override fun getBaseUrl(): String {
        return Config.BASE_URL
    }

    override fun createOkHttpClientBuilder(): OkHttpClient.Builder {
        return super.createOkHttpClientBuilder().apply {
            //设置缓存目录和缓存大小：10M
            cache(Cache(cacheDir, 10 * 1024 * 1024))
            addInterceptor(object : RequestInterceptor() {
                override fun filter(request: Request): Boolean {
                    return Config.HOST !== request.url().host()
                }

                override fun config(originRequest: Request, newRequestBuilder: Request.Builder) {
                    val imei = Environment.getInstance().imei?.takeIf { it != "" }
                        ?: System.currentTimeMillis().toString()
                    val ts = System.currentTimeMillis()
                    newRequestBuilder.apply {
                        addHeader("imei", imei)

                        originRequest.url().newBuilder().apply {
                            addQueryParameter("ts", "$ts")
                        }.build().let { url(it) }
                    }
                }
            })
            if (BuildConfig.DEBUG) {
                addInterceptor(LogInterceptor({ Logger.i(TAG, it) }))
            }
        }
    }
})
```
这里您可以配置的东西很多，根据自身情况发挥即可

## 3、下载
1、配置（非必须）：
```
Downloader.downloadInfoPersistent = DownloadInfoPersistent2File("/Users/leleliu008/xx/")
```
2、使用示例：
```
Downloader.downloadSync("https://www.baidu.com/", File("/Users/leleliu008/baidu.html"))
```

## 4、注意
<br>
该库使用了<code>lambda</code>表达式，所以，您用在<code>Android</code>项目上的时候，必须设置要编译成<code>JVM1.8</code>版本的目标代码。
<br><br>
如果您的<code>gradle</code>配置脚本使用的是<code>kotlin</code>语言，在<code>build.gradle.kts</code>中如下配置：
<br>

```
android {
    ...
    compileOptions {
        //使用JAVA8语法解析
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
}
```

如果您的<code>gradle</code>配置脚本使用的是<code>groovy</code>语言，在<code>build.gradle</code>中如下配置：

```
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

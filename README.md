# Redcord
欢迎加我WX: songhanghanghang 
计划重启该项目

>红绳-连接你我
[https://github.com/songhanghang/redcord](https://github.com/songhanghang/redcord)

## 缘起

是否遇到过，有急事找Ta, 但是电话打不通，微信没人接，即使科技再发达，找Ta时你也抓瞎！

如果你和家人都是iphone用户，可以开启[家人位置共享](https://support.apple.com/zh-cn/HT201087)，这样可以随时随地查看Ta在什么位置。

Android现在并没有很好的解决方案，强大如微信也必须手动开启位置共享，当然微信不做是有他的道理...

但是我确实有这样的需求，我的TA只要一出门，那是永远联系不上的, 嗯！确实有必要实时给她定位！于是乎，手撕了这个程序...

先梳理下有哪些需求：
1. 需要定位
2. 需要把定位信息同步给我
3. 需要程序一直活着

定位问题高德地图SDK解决，同步问题leancloud解决，怎么才能一直活着，这是个头疼的问题，微信可以被各大手机厂商白名单呵护着, 但是一般App不可能有这个待遇。

业内能人志士为了保活使出各种奇淫技巧，例如：
1. 双进程互调
2. 可见前台进程
3. 一个可见像素点Activity
4. 应用联盟(流氓联盟)，通过不同的App发广播互调
5. 利用Android系统漏洞
总之黑白手段都有，但是活的都不光彩，程序还是要漂亮的活着！

于是乎想到了Android的动态壁纸，这玩意好，壁纸是脸面，系统要脸，就不能让它死，死了也要主动拉起来!

那么做个好看的动态壁纸，通过壁纸定位和同步信息就可以解决这个问题.

Coding....

emmmmm....

![图片](https://wx2.sinaimg.cn/mw690/006292TQly1gb59mnxaezg30go09enm1.gif)

### 下载

[https://github.com/songhanghang/redcord/blob/master/app/release/redcord.apk](https://github.com/songhanghang/redcord/blob/master/app/release/redcord.apk)

### 软件功能介绍

![图片](https://wx3.sinaimg.cn/mw690/006292TQgy1gb0qio0w64g307t0gy1kz.gif)

#### 注册与登录
注册不需要手机号,不需要邮箱（不会上传你的隐私），直接点击注册即可，注册成功后会生成你的ID, 显示在屏幕的左上角，这是你以后登录的唯一凭证（注意保密），注册成功后，需要配对TA的ID, 才能使用，也可以发送自己ID给Ta, 让对方完成配对。配对完成请及时在详情页备份两人Id。
你与Ta的ID唯一绑定，绑定后不可解绑，或者与他人绑定。

#### 主界面

App地图界面，左上角显示两人ID, 中间为彼此位置及驾车导航路线，信息框显示Ta的位置信息，提供一些常用功能:
1. 见Ta       `一键导航见Ta，高德，百度地图`
2. 设置壁纸
3. 备份       `备份两人ID, 以便换手机，或者清空缓存找回`
4. 打赏       `用的leancloud的服务，免费的访问量很少，很期望您的支持`
5. 关于       `指向这篇ReadMe`

#### 动态壁纸

2.0版本支持桌面实时地图显示，TA的位置一目了然。


#### 其他
1. 耗电   ` 主界面:10s定位一次，退出后不再定位;动态壁纸:可见时30s定位一次，不可见不定位。所以不用担心耗电问题
`
2. 用户隐私 `没有上传你任何其他信息，自行查看代码`
3. 数据服务 `用的leancloud提供数据支持，每天只有3万次免费请求，所以急需您的支持`
4. Bug `小米手机上完成的开发测试，其他机器有问题，欢迎反馈`

### 联系方式
 

 邮箱: 1131442853@qq.com

[github: https://github.com/songhanghang](https://github.com/songhanghang)


# sftp-pool

https://github.com/meilonme/sftp-pool

一款基于apache commons-pool2  sftp 连接池

### jsftp-spring-boot-starter
spring-boot 项目引入
```xml
<dependency>
    <groupId>me.meilon.jsftp</groupId>
    <artifactId>jsftp-spring-boot-starter</artifactId>
    <version>${最新稳定版本}</version>
</dependency>
```
引入 jsftp-spring-boot-starter 后, 启动项目会自动加载配置;

application.yml 配置样例
```yaml
sftp-pool:
  # 获取资源的等待时间。blockWhenExhausted 为 true 时有效。-1 代表无时间限制，一直阻塞直到有可用的资源
  maxWaitMillis: -1
  # 每个key最小保持的空闲链接数, 默认 1
  minIdlePerKey: 1
  # 每个key最大保持的空闲链接数, 默认 8
  maxIdlePerKey: 8
  # 每个key最大可存在的链接数, 默认 8
  maxTotalPerKey: 8
  # 对象空闲的最小时间，达到此值后空闲对象将可能会被移除。
  #不受最小连接数限制影响
  # -1 表示不移除；默认 30 分钟
  minEvictableIdleTimeMillis: 180000
  #连接空闲的最小时间，达到此值后空闲链接将会被移除，
  #但会保留最小空闲连接数
  #默认为-1.
  softMinEvictableIdleTimeMillis: -1
  # “空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。
  # 默认值 -1L
  timeBetweenEvictionRunsMillis: -1
  # 资源耗尽时，是否阻塞等待获取资源，默认 true
  blockWhenExhausted: true
  connConfigs:
    sftp1:
      host: 192.168.1.121
      port: 22
      userName: root
      password: root
      # 是否自动关闭
      autoDisconnect: false
    sftp2:
      host: 192.168.1.122
      port: 22
      userName: root
      password: root
```

### jsftpClient 

使用 jsftpClient 会自动从链接池获取 sftp 链接, 使用完毕后自动交还给连接池; 因此可以避免链接忘记关闭的情况;
```xml
<dependency>
    <groupId>me.meilon.jsftp</groupId>
    <artifactId>jsftp-pool-client</artifactId>
    <version>${最新稳定版本}</version>
</dependency>
```
jsftpClient 提供了两个函数式调用的 run 方法;
用户也可以继承 JsftpClient 进行扩展;

jsftpClient 使用样例;

有返回值的调用
```
JsftpClientFactory jsftpClientFactory = new JsftpClientFactory();
JsftpClient client = jsftpClientFactory.createSftpClient(host,port,username,password);
List<String> fileNames = client.run(sftp ->{
    String homeBath = sftp.getHome();
    return sftp.listFileNames(homeBath);
});
```

无返回值的调用
```
JsftpClientFactory jsftpClientFactory = new JsftpClientFactory();
JsftpClient client = jsftpClientFactory.createSftpClient(host,port,username,password);
client.run(sftp ->{
    String homeBath = sftp.getHome();
    sftp.cd(homeBath);
});
```


### sftp-pool-core

基于 maven 引入 sftp-pool-core
```xml
<dependency>
    <groupId>me.meilon.jsftp</groupId>
    <artifactId>sftp-pool-core</artifactId>
    <version>${最新稳定版本}</version>
</dependency>
```

sftp-pool-core 提供了基础的sftpi链接池功能;

使用 sftp-pool-core 用户需要自行初始化链接池工厂 SftpPooledFactory;

使用 SftpPooledFactory getSftpPool() 方法获取链接池(使用懒加载模式, 首次获取链接池时自动初始化链接池);

使用 SftpPooledFactory setSftpConnConfig() 方法设置一个SFTP链接配置. 
可以设置多个SFTP链接配置, SftpPooledFactory 会根据 sftpId 分配到不同的池里

样例: 初始化链接池
```
    // 创建链接池工厂
    SftpPooledFactory factory = new SftpPooledFactory();

    // 设置SFTP链接配置
    String sftpId = "sftp1";
    String host = "127.0.0.1";
    int port = 22;
    String username = "user";
    String password = "psw";
    factory.setSftpConnConfig(host,port, username, password,sftpId,false);
    
    // 可设置多个SFTP链接配置, SftpPooledFactory 会根据 sftpId 分配到不同的池里
    String sftpId2 = "sftp2";
    String host2 = "127.0.0.1";
    int port2 = 22;
    String username2 = "user";
    String password2 = "psw";
    factory.setSftpConnConfig(host2,port2, username2, password2,sftpId2,false);

```

使用 SftpPool borrowObject() 方法获取一个SFTP链接, 需要前面设置SFTP链接配置对应的 sftpId 作为入参

SftpConnect 实现了Closeable接口, 推荐使用自动关闭;
当然也可以手动调用 SftpConnect 的 close() 方法以归还 SFTP 链接回链接池;

样例: 业务代码中使用链接池获取SFTP链接;
```
    String sftpId = "sftp1";

    // 通过 SftpPooledFactory 获取链接池
    SftpPool pool = factory.getSftpPool();
    
    // 通过连接池的 borrowObject 方法获取SFTP链接
    try (SftpConnect sftp = pool.borrowObject(sftpId)){
        List<ChannelSftp.LsEntry> files =  sftp.listFiles("/data/");
        for (ChannelSftp.LsEntry file : files) {
            System.out.println(file.getFilename());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
```
SftpPooledFactory 有一个静态方法 createConnect() 可用于创建一个SFTP链接;

需要注意的是, 使用此方法创建的链接不会被纳管到链接池, 当你调用 SftpConnect 的 close() 方法时, 该链接不会被返还到链接池中, 而是直接关闭此链接;
```
    String host = "127.0.0.1";
    int port = 22;
    String username = "user";
    String password = "psw";
    SftpConnect sftp = SftpPooledFactory.createConnect(host,port, username, password);
```

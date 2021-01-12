# sftp-pool

http://sftp.meilon.me/

https://github.com/meilonme/sftp-pool

一款基于apache commons-pool2  sftp 连接池

已发布到 MAVEN
```xml
<dependency>
  <groupId>me.meilon.sftp</groupId>
  <artifactId>sftp-pool-core</artifactId>
  <version>0.0.3</version>
</dependency>
```

### 使用帮助
通过连接池创建一个sftp链接
```java
        SftpPooledFactory factory = new SftpPooledFactory();
        SftpPool pool = factory.createSftpPool();
        try (SftpConnect sftp = pool.borrowObject(host,port, username, password)){
            List<ChannelSftp.LsEntry> files =  sftp.listFiles("/data/");
            for (ChannelSftp.LsEntry file : files) {
    
                System.out.println(file.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```

如果需要创建一个不纳管到链接池的sftp链接
```java
        try (SftpConnect sftp = SftpPooledFactory.createConnect(host,port, username, password)){
            List<ChannelSftp.LsEntry> files =  sftp.listFiles("/data/");
            for (ChannelSftp.LsEntry file : files) {
    
                System.out.println(file.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```
# sftp-pool

http://sftp.meilon.me/

https://github.com/meilonme/sftp-pool

https://issues.sonatype.org/browse/OSSRH-63177

一款基于apache commons-pool2  sftp 连接池


### 使用帮助
通过连接池创建一个sftp链接
```java
        SftpPoolConfig conf = new SftpPoolConfig();
        SftpPooledFactory factory = Factory.INSTANCE.getFactory();
        SftpPool pool = factory.createSftpPool(conf);
        SftpConnConfig config = factory.setSftpConnConfig(host,port, username,passwd);
        try (SftpConnect sftp = pool.borrowObject(config)){
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
        try (SftpConnect sftp = SftpPooledFactory.createConnect(host,port, username,passwd)){
            List<ChannelSftp.LsEntry> files =  sftp.listFiles("/data/");
            for (ChannelSftp.LsEntry file : files) {

                System.out.println(file.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```
package cn.cyc.ai.cog.base;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基础服务 Base-Server 启动入口（默认端口 8805）。
 * <p>提供字典、枚举与磁盘文件存储。</p>
 */
@SpringBootApplication(scanBasePackages = {
        "cn.cyc.ai.cog.base",
        "cn.cyc.ai.cog.common.mybatis"
})
@MapperScan({"cn.cyc.ai.cog.base.dict.mapper", "cn.cyc.ai.cog.base.file.mapper"})
public class BaseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseServerApplication.class, args);
    }
}

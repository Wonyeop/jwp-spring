package next.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(
	basePackages = { "next.service", "next.dao", "next.aspect" },
	excludeFilters = @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION)
)
@PropertySource("classpath:application.properties")
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AppConfig {
//	private static final String DB_DRIVER = "org.h2.Driver";
//	private static final String DB_URL = "jdbc:h2:~/jwp-basic;AUTO_SERVER=TRUE";
//	private static final String DB_USERNAME = "sa";
//	private static final String DB_PW = "";
	
//	@Autowired
//	private Environment env;
	
	@Value("${db.driver}")
	private String dbDriver;
	@Value("${db.url}")
	private String dbUrl;
	@Value("${db.username}")
	private String dbUsername;
	@Value("${db.password}")
	private String dbPassword;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev(){
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
//		ds.setDriverClassName(DB_DRIVER);
//		ds.setUrl(DB_URL);
//		ds.setUsername(DB_USERNAME);
//		ds.setPassword(DB_PW);
//		ds.setDriverClassName(env.getProperty("db.driver"));
//		ds.setUrl(env.getProperty("db.url"));
//		ds.setUsername(env.getProperty("db.username"));
//		ds.setPassword(env.getProperty("db.password"));
		ds.setDriverClassName(dbDriver);
		ds.setUrl(dbUrl);
		ds.setUsername(dbUsername);
		ds.setPassword(dbPassword);
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}

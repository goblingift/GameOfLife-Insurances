package gift.goblin.goli;

import gift.goblin.goli.dto.GameStatus;
import gift.goblin.goli.service.DatabaseInitializer;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
public class GameOfLifeInsuranceApplication {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    DatabaseInitializer databaseInitializer;

    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeInsuranceApplication.class, args);
    }

    @Bean(name = "gameStatus")
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION)
    public GameStatus getGameStatus() {
        logger.info("Created new GameStatus-object.");
        return new GameStatus(true, true);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
    
    @PostConstruct
    public void initMongoDB() {
        databaseInitializer.initializeRoles();
        databaseInitializer.initializeAdminUser();
    }

}

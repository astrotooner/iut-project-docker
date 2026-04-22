package org.example.iutprojectdocker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class HelloController {

    /**
     * @return Number of hits or null if Redis is unavailable
     */
    private Long getHitCountSafe() throws InterruptedException {
        int retries = 5;

        while (retries >= 0) {
            try (Jedis jedis = new Jedis("redis", 6379)) {
                return jedis.incr("hits");
            } catch (Exception e) {
                if (retries == 0) {
                    return null;
                }
                retries--;
                Thread.sleep(500);
            }
        }
        return null;
    }


    /**
     * @return A greeting message that includes the value of the "APP_USER" environment variable, or "World" if it is not set.
     */

	@GetMapping("/")
   		 public String hello() {
        	try {
            	// 1. Logique de la branche Username-feature
            	String appUser = System.getenv("APP_USER");
            	String name = (appUser == null || appUser.isEmpty()) ? "World" : appUser;
            	String base = String.format("Hello %s!", name);

            	// 2. Logique de la branche redis-feature
            	Long count = getHitCountSafe();
            	return (count == null)
                    	? base + " (Redis indisponible)\n"
                    	: base + String.format(" J'ai été visité %d fois.\n", count);

        	} catch (Exception e) {
            		return "Error: " + e.getMessage();
        	}
    
    	}
}

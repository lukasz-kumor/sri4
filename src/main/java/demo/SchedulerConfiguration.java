package demo;

import io.spring.guides.gs_producing_web_service.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    private ClientRepository clientRepository;
    @Autowired
    public SchedulerConfiguration(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void minusDueDays() {
        System.out.println(Thread.currentThread().getName()+" The Task1 executed at "+ new Date());


        try {
            clientRepository.minusDueDays();
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
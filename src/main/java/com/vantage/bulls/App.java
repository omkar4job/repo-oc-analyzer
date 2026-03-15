package com.vantage.bulls;

import com.vantage.bulls.dao.OptionChainDAO;
import com.vantage.bulls.dto.OptionChainResponse;
import com.vantage.bulls.service.OptionChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App implements CommandLineRunner {

    @Autowired
    private OptionChainService ocService;

    @Autowired
    private OptionChainDAO ocDAOService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n\nHello World! It's " + Calendar.getInstance().getTime() + " now\n");

        // This will execute once the Spring Context is fully loaded
        OptionChainResponse optionChain = ocService.getOptionChain();
        ocDAOService.saveOptionChain(optionChain);


    }
}
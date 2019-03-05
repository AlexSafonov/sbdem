package com.entr.sbdem.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

@RestController
@RequestMapping("/logger")
@Log4j2
public class Log4j2Controller {
    //This controller was made only for educating purpose.
    @GetMapping
    public String loggerTest(){
       // Logger logger = LoggerFactory.getLogger(RootSimpleController.class);
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");
        System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );
        return "logger test return";
    }
}

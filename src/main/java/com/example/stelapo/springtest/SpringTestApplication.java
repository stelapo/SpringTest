package com.example.stelapo.springtest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

@SpringBootApplication
@Slf4j
public class SpringTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTestApplication.class, args);

        WebClient client = WebClient.create("https://www.baeldung.com/spring-5-webclient");
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = client.get();
        WebClient.ResponseSpec responseSpec = requestHeadersUriSpec.retrieve();
        Mono<String> response = responseSpec
                .bodyToMono(String.class)
                //.log()
                ;
        response
                .subscribe(successValue -> log.info("1... " + successValue.substring(0,30)),
                          errorConsumer -> log.error("", errorConsumer),
                          () -> log.info("Http request completed!"));
        /*String stringResponse = null;
        try {
            stringResponse = response.toFuture().get();
            System.out.println("2... " + stringResponse.substring(0,30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/


        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i != 4) return i;
                    throw new RuntimeException("Got to 4");
                })
                //.delaySequence(Duration.ofSeconds(2))
                .delayElements(Duration.ofSeconds(10))
                ;
        ints.subscribe(i -> log.info(i + ""),
                error -> log.error("Error: " + error),
                () -> log.info("Completed!"));


        Flux.interval(Duration.ofMillis(300), Schedulers.newSingle("test"))
                .takeUntil(aLong -> aLong == 101)
                .filter(aLong -> aLong%3==0)
                .cancelOn(Schedulers.newSingle("cancelScheduler"))
                .subscribe(aLong -> {log.info(aLong + " aLong");},
                        error -> log.error("Error: " + error),
                        () -> log.info("aLong completed!"));
    }

}

package DeBug.emotion.Controller;


import DeBug.emotion.domain.Chat;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Slf4j
@CrossOrigin("*")
public class test {

    private static final String SSE_URL = "http://localhost:9900/sse/";

    @RequestMapping("/test2")
    public String sss(@RequestBody Chat chat){
        System.out.println("hi");
        System.out.println(chat.getMessage());

        return "200";
    }
    @RequestMapping("/receive-sse")
    @ResponseBody
    public void receiveSSE(@RequestParam("BCID") String BCID, HttpServletResponse response) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> requestEntity = RequestEntity
                .get(SSE_URL + BCID)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        String body = responseEntity.getBody();

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        System.out.println(body);
    }

    @GetMapping(value = "/data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getData() {
        return WebClient.create("http://localhost:9900")
                .get()
                .uri("/data")
                .retrieve()
                .bodyToFlux(String.class);
    }

    @RequestMapping("/test")
    public String Test(@RequestParam("BCID")String id) {

        String URI = "http://10.20.92.36:9900/z/"+id;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(URI);
        request.addHeader("accept", "application/json");

        try {
            HttpResponse response = httpClient.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(jsonString);
            return "200";
        }catch (Exception e ){
            return "400";
        }
    }


    @GetMapping("/sse")
    public ResponseEntity<SseEmitter> handleSSE() {

        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            boolean a = true;
            try {
                while(a) {
                    emitter.send(SseEmitter.event().data("Data " + 1));
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }).start();
        return ResponseEntity.ok(emitter);
    }


}
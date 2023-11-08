package DeBug.emotion.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

import java.time.Duration;


@Controller
public class con {
    @GetMapping("/sse2")
    public ResponseEntity<Flux<String>> streamEvents() {
        // SSE 메시지 생성
        Flux<String> eventStream = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "data: Hello, world!\n\n");
        // Response 객체 생성
        return ResponseEntity.ok()
                .header("Content-Type", "text/event-stream;charset=UTF-8")
                .body(eventStream);
    }
}

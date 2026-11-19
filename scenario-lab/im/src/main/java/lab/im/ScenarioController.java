package lab.im;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "im: 即时消息：发送幂等与会话序号";
    }
    @PostMapping("/api/messages") public long send(@RequestParam String request,@RequestParam String body) {
        return service.send(1,request,body);
    }
}

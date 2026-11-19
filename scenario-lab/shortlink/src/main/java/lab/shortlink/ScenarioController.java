package lab.shortlink;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "shortlink: 短链：碰撞重试与过期校验";
    }
    @PostMapping("/api/links") public String create(@RequestParam String target) {
        return service.create(target,System.currentTimeMillis()+3600000,()->java.util.UUID.randomUUID().toString().substring(0,8));
    }
    @GetMapping("/s/{code}") public org.springframework.http.ResponseEntity<Void> redirect(@PathVariable String code) {
        return org.springframework.http.ResponseEntity.status(302).location(java.net.URI.create(service.resolve(code,System.currentTimeMillis()))).build();
    }
}

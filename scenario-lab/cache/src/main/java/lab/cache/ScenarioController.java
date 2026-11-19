package lab.cache;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "cache: 缓存一致性：旧读回填及版本栅栏";
    }
    @PostMapping("/api/product") public void update(@RequestParam long price) {
        service.updatePrice(price);
    }
    @GetMapping("/api/product") public long price() {
        return service.cachedPrice();
    }
}

package lab.qrlogin;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "qrlogin: 二维码登录：一次性兑换及浏览器绑定";
    }
    @PostMapping("/api/scenes") public void create(@RequestParam String id,@RequestParam String secret) {
        service.create(id,secret,System.currentTimeMillis()+120000);
    }
    @PostMapping("/api/scan") public boolean scan(@RequestParam String id,@RequestParam long user) {
        return service.scan(id,user,System.currentTimeMillis());
    }
    @PostMapping("/api/confirm") public boolean confirm(@RequestParam String id,@RequestParam long user) {
        return service.confirm(id,user,System.currentTimeMillis());
    }
    @PostMapping("/api/redeem") public boolean redeem(@RequestParam String id,@RequestParam String secret) {
        return service.redeem(id,secret,System.currentTimeMillis());
    }
}

package kr.co.koreazinc.actuator.health;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.util.ObjectUtils;

public class ServerComposite implements CompositeHealthContributor {

    private Map<String, HealthContributor> contributors = new LinkedHashMap<>();

    public ServerComposite(HealthProperties properties) {
        if (!ObjectUtils.isEmpty(properties.getServer())) {
            properties.getServer()
                .forEach(server->contributors.put(server.getName(), new ServerHealthIndicator(server.getHost(), server.getPort(), server.getTimeout())));
        }
    }

    @Override
    public HealthContributor getContributor(String name) {
        return contributors.get(name);
    }

    @Override
    public Iterator<NamedContributor<HealthContributor>> iterator() {
        return contributors.entrySet().stream().map((entry)->NamedContributor.of(entry.getKey(), entry.getValue())).iterator();
    }
}
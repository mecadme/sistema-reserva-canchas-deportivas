package ec.edu.ups.reservas.usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ServiceKeyInterceptor serviceKeyInterceptor;

    public WebConfig(ServiceKeyInterceptor serviceKeyInterceptor) {
        this.serviceKeyInterceptor = serviceKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serviceKeyInterceptor).addPathPatterns("/internal/**");
    }
}

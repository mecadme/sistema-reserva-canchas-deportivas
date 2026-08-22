package ec.edu.ups.reservas.reportes;
import io.swagger.v3.oas.annotations.OpenAPIDefinition; import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication @OpenAPIDefinition(info=@Info(title="API de Reportes",version="v1"))
public class ReportesApplication {public static void main(String[]args){SpringApplication.run(ReportesApplication.class,args);}}
